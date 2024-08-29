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
import nu.parley.android.data.net.service.MessageService
import nu.parley.android.data.repository.MessageRepository
import nu.parley.android.util.FileUtil
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

        messagesCall.enqueue(object : Callback<ParleyResponse<List<Message>>> {
            public override fun onResponse(
                call: Call<ParleyResponse<List<Message>>>,
                response: Response<ParleyResponse<List<Message>>>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body())
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(call: Call<ParleyResponse<List<Message>>>, t: Throwable) {
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
            MessageService::class.java
        ).getOlder(previousPaging.before)

        messagesCall.enqueue(object : Callback<ParleyResponse<List<Message>>> {
            public override fun onResponse(
                call: Call<ParleyResponse<List<Message>>>,
                response: Response<ParleyResponse<List<Message>>>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body())
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(call: Call<ParleyResponse<List<Message>>>, t: Throwable) {
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
        var messagesCall: Call<ParleyResponse<ParleyResponsePostMessage?>?>
        if (media == null) {
            // Text or media message
            messagesCall =
                Connectivity.getRetrofit().create(MessageService::class.java).post(message)
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

        messagesCall.enqueue(object : Callback<ParleyResponse<ParleyResponsePostMessage?>?> {
            public override fun onResponse(
                call: Call<ParleyResponse<ParleyResponsePostMessage?>?>,
                response: Response<ParleyResponse<ParleyResponsePostMessage?>?>
            ) {
                if (response.isSuccessful) {
                    var updatedMessage = Message.withIdAndStatus(
                        message, response.body()!!
                            .data?.messageId, Message.SEND_STATUS_SUCCESS
                    )
                    callback.onSuccess(updatedMessage)
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(
                call: Call<ParleyResponse<ParleyResponsePostMessage?>?>,
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
        messagesCall.enqueue(object : Callback<ParleyResponse<ParleyResponsePostMedia>> {
            public override fun onResponse(
                call: Call<ParleyResponse<ParleyResponsePostMedia>>,
                response: Response<ParleyResponse<ParleyResponsePostMedia>>
            ) {
                if (response.isSuccessful) {
                    var media = Media(
                        response.body()!!.data.mediaId!!, file.name, mimeType.value
                    )
                    var updatedMessage = Message.withMedia(message, media)
                    callback.onSuccess(updatedMessage)
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(
                call: Call<ParleyResponse<ParleyResponsePostMedia>>,
                t: Throwable
            ) {
                t.printStackTrace()
                callback.onFailed(null, t.message)
            }
        })
    }

    public override fun get(messageId: Int, callback: RepositoryCallback<Message>) {
        var messagesCall = Connectivity.getRetrofit().create(
            MessageService::class.java
        ).get(messageId)

        messagesCall.enqueue(object : Callback<ParleyResponse<Message>> {
            public override fun onResponse(
                call: Call<ParleyResponse<Message>>,
                response: Response<ParleyResponse<Message>>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!.data)
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(call: Call<ParleyResponse<Message>>, t: Throwable) {
                t.printStackTrace()
                callback.onFailed(null, t.message)
            }
        })
    }
}
