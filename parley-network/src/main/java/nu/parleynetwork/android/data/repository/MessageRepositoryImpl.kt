package nu.parleynetwork.android.data.repository

import nu.parley.android.data.model.Media
import nu.parley.android.data.model.Message
import nu.parley.android.data.model.MimeType.Companion.fromValue
import nu.parley.android.data.net.Connectivity
import nu.parley.android.data.net.RepositoryCallback
import nu.parley.android.data.net.response.ParleyPaging
import nu.parley.android.data.net.response.ParleyResponse
import nu.parley.android.data.net.response.ParleyResponsePostMedia
import nu.parley.android.data.net.response.ParleyResponsePostMessage
import nu.parley.android.data.repository.MessageRepository
import nu.parley.android.util.FileUtil
import nu.parleynetwork.android.data.model.MessageJson
import nu.parleynetwork.android.data.model.ParleyResponseJson
import nu.parleynetwork.android.data.model.ParleyResponsePostMediaJson
import nu.parleynetwork.android.data.model.ParleyResponsePostMessageJson
import nu.parleynetwork.android.data.net.MessageService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MessageRepositoryImpl : MessageRepository {
    public override fun findAll(callback: RepositoryCallback<ParleyResponse<List<Message>>>) {
        var messagesCall = Connectivity.getRetrofit().create(
            MessageService::class.java
        ).findAll()

        messagesCall.enqueue(object : Callback<ParleyResponseJson<List<MessageJson>>> {
            public override fun onResponse(
                call: Call<ParleyResponseJson<List<MessageJson>>>,
                response: Response<ParleyResponseJson<List<MessageJson>>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()!!
                    callback.onSuccess(body.toParleyResponse(body.data.map { it.toMessage() }))
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(call: Call<ParleyResponseJson<List<MessageJson>>>, t: Throwable) {
                t.printStackTrace()
                callback.onFailed(null, t.message)
            }
        })
    }

    public override fun getOlder(
        previousPaging: ParleyPaging,
        callback: RepositoryCallback<ParleyResponse<List<Message>>>
    ) {
        var messagesCall = Connectivity.getRetrofit().create(
            nu.parleynetwork.android.data.net.MessageService::class.java
        ).getOlder(previousPaging.before)

        messagesCall.enqueue(object : Callback<ParleyResponseJson<List<MessageJson>>> {
            public override fun onResponse(
                call: Call<ParleyResponseJson<List<MessageJson>>>,
                response: Response<ParleyResponseJson<List<MessageJson>>>
            ) {
                if (response.isSuccessful) {
                    val parleyResponseJson = response.body()!!
                    callback.onSuccess(
                        parleyResponseJson.toParleyResponse(
                            parleyResponseJson.data.map { it.toMessage() }
                        )
                    )
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(call: Call<ParleyResponseJson<List<MessageJson>>>, t: Throwable) {
                t.printStackTrace()
                callback.onFailed(null, t.message)
            }
        })
    }

    public override fun send(
        message: Message,
        media: String?,
        callback: RepositoryCallback<Message>
    ) {
        var messagesCall: Call<ParleyResponseJson<ParleyResponsePostMessageJson>>
        if (media == null) {
            // Text or media message
            messagesCall =
                Connectivity.getRetrofit().create(MessageService::class.java).post(MessageJson.from(message))
        } else {
            // Image message API V1.2: Uploading it together when sending the message
            var file = File(media)
            var mediaType = FileUtil.getMimeType(file.absolutePath)
            var mimeType = fromValue(
                mediaType!!
            )
            var requestBody = RequestBody.create(MediaType.parse(mimeType.value), media)

            var filePart = MultipartBody.Part.createFormData("image", file.name, requestBody)
            messagesCall =
                Connectivity.getRetrofit().create(MessageService::class.java).postImage(filePart)
        }

        messagesCall.enqueue(object : Callback<ParleyResponseJson<ParleyResponsePostMessageJson>> {
            public override fun onResponse(
                call: Call<ParleyResponseJson<ParleyResponsePostMessageJson>>,
                response: Response<ParleyResponseJson<ParleyResponsePostMessageJson>>
            ) {
                if (response.isSuccessful) {
                    var updatedMessage = Message.withIdAndStatus(
                        message,
                        response.body()!!.data.toParleyResponsePostMessage().messageId, Message.SEND_STATUS_SUCCESS
                    )
                    callback.onSuccess(updatedMessage)
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(
                call: Call<ParleyResponseJson<ParleyResponsePostMessageJson>>,
                t: Throwable
            ) {
                t.printStackTrace()
                callback.onFailed(null, t.message)
            }
        })
    }

    public override fun sendMedia(
        message: Message,
        media: String,
        callback: RepositoryCallback<Message>
    ) {
        // API V1.6+: Uploading media
        var file = File(media)
        var mediaType = FileUtil.getMimeType(media)
        var mimeType = fromValue(
            mediaType!!
        )
        var requestBody = RequestBody.create(MediaType.parse(mimeType.value), file)

        var filePart = MultipartBody.Part.createFormData("media", file.name, requestBody)

        var messagesCall = Connectivity.getRetrofit().create(
            MessageService::class.java
        ).postMedia(filePart)
        messagesCall.enqueue(object : Callback<ParleyResponseJson<ParleyResponsePostMediaJson>> {
            public override fun onResponse(
                call: Call<ParleyResponseJson<ParleyResponsePostMediaJson>>,
                response: Response<ParleyResponseJson<ParleyResponsePostMediaJson>>
            ) {
                if (response.isSuccessful) {
                    val updatedMessage = Message.withMedia(
                        message,
                        Media(
                            response.body()!!.data.mediaId!!, file.name, mimeType.value
                        )
                    )
                    callback.onSuccess(updatedMessage)
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(
                call: Call<ParleyResponseJson<ParleyResponsePostMediaJson>>,
                t: Throwable
            ) {
                t.printStackTrace()
                callback.onFailed(null, t.message)
            }
        })
    }

    public override fun get(messageId: Int, callback: RepositoryCallback<Message>) {
        var messagesCall = Connectivity.getRetrofit().create(
            nu.parleynetwork.android.data.net.MessageService::class.java
        ).get(messageId)

        messagesCall.enqueue(object : Callback<ParleyResponseJson<MessageJson>> {
            public override fun onResponse(
                call: Call<ParleyResponseJson<MessageJson>>,
                response: Response<ParleyResponseJson<MessageJson>>
            ) {
                if (response.isSuccessful) {
                    val messageJson = response.body()!!.data
                    val message = messageJson.toMessage()
                    callback.onSuccess(message)
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(call: Call<ParleyResponseJson<MessageJson>>, t: Throwable) {
                t.printStackTrace()
                callback.onFailed(null, t.message)
            }
        })
    }
}
