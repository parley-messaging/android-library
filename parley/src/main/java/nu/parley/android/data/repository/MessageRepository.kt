package nu.parley.android.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nu.parley.android.Parley
import nu.parley.android.data.model.Media
import nu.parley.android.data.model.Message
import nu.parley.android.data.model.MessageStatus
import nu.parley.android.data.model.MimeType.Companion.fromValue
import nu.parley.android.data.net.ParleyHttpRequestMethod
import nu.parley.android.data.net.RepositoryCallback
import nu.parley.android.data.net.request.UpdateMessageStatusRequest
import nu.parley.android.data.net.response.base.DataResponse
import nu.parley.android.data.net.response.base.PagingResponse
import nu.parley.android.data.net.response.message.CreateMediaResponse
import nu.parley.android.data.net.response.message.CreateMessageResponse
import nu.parley.android.data.net.response.message.GetMessagesResponse
import nu.parley.android.data.net.response.message.GetUnseenResponse
import nu.parley.android.util.FileUtil
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class MessageRepository {
    private class TypeTokenMessage : TypeToken<DataResponse<Message>>()
    private class TypeTokenMessages : TypeToken<GetMessagesResponse>()
    private class TypeTokenCreate : TypeToken<DataResponse<CreateMessageResponse>>()
//    private class TypeTokenUpdateStatus : TypeToken<VoidResponse>()
    private class TypeTokenCreateMedia : TypeToken<DataResponse<CreateMediaResponse>>()
    private class TypeTokenUnseenCount : TypeToken<DataResponse<GetUnseenResponse>>()

    fun findAll(callback: RepositoryCallback<GetMessagesResponse>) {
        val network = Parley.getInstance().network
        network.networkSession.request(
            network.url + network.path + "messages",
            null,
            ParleyHttpRequestMethod.Get,
            onCompletion = {
                val response = Gson()
                    .getAdapter(TypeTokenMessages())
                    .fromJson(it)
                callback.onSuccess(response)
            },
            onFailed = { statusCode, message ->
                callback.onFailed(statusCode, message)
            }
        )
    }

    fun getOlder(
        previousPaging: PagingResponse,
        callback: RepositoryCallback<GetMessagesResponse>
    ) {
        val network = Parley.getInstance().network
        network.networkSession.request(
            network.url + network.path + previousPaging.before,
            null,
            ParleyHttpRequestMethod.Get,
            onCompletion = {
                val response = Gson()
                    .getAdapter(TypeTokenMessages())
                    .fromJson(it)
                callback.onSuccess(response)
            },
            onFailed = { statusCode, message ->
                callback.onFailed(statusCode, message)
            }
        )
    }

    fun send(
        message: Message,
        media: String?,
        callback: RepositoryCallback<Message>
    ) {
        val network = Parley.getInstance().network
        val onCompletion: (String) -> Unit = {
            val response = Gson()
                .getAdapter(TypeTokenCreate())
                .fromJson(it)

            val updatedMessage = Message.withIdAndStatus(
                message,
                response.data.messageId,
                Message.SEND_STATUS_SUCCESS,
            )
            callback.onSuccess(updatedMessage)
        }
        val onFailed: (Int?, String?) -> Unit = { statusCode, errorMessage ->
            callback.onFailed(statusCode, errorMessage)
        }
        val url = network.url + network.path + "messages"
        if (media == null) {
            network.networkSession.request(
                url,
                Gson().toJson(message),
                ParleyHttpRequestMethod.Post,
                onCompletion = onCompletion,
                onFailed = onFailed
            )
        } else {
            network.networkSession.upload(
                url,
                File(media),
                FileUtil.getMimeType(File(media).absolutePath),
                "image",
                onCompletion = onCompletion,
                onFailed = onFailed
            )
        }
    }

    fun sendMedia(
        message: Message,
        media: String,
        callback: RepositoryCallback<Message>
    ) {
        val network = Parley.getInstance().network
        val url = network.url + network.path + "media"
        network.networkSession.upload(
            url,
            media,
            FileUtil.getMimeType(media),
            "media",
            onCompletion = {
                val response = Gson()
                    .getAdapter(TypeTokenCreateMedia())
                    .fromJson(it)

                val mediaType = FileUtil.getMimeType(media)
                val mimeType = fromValue(mediaType!!)
                val updatedMessage = Message.withMedia(
                    message,
                    Media(
                        response.data.id, File(media).name, mimeType.value
                    )
                )
                callback.onSuccess(updatedMessage)
            },
            onFailed = { statusCode, errorMessage ->
                callback.onFailed(statusCode, errorMessage)
            }
        )
    }

    fun get(messageId: Int, callback: RepositoryCallback<Message>) {
        val network = Parley.getInstance().network
        network.networkSession.request(
            network.url + network.path + "messages/$messageId",
            null,
            ParleyHttpRequestMethod.Get,
            onCompletion = {
                val response = Gson()
                    .getAdapter(TypeTokenMessage())
                    .fromJson(it)
                callback.onSuccess(response.data)
            },
            onFailed = { statusCode, message ->
                callback.onFailed(statusCode, message)
            }
        )
    }

    fun getUnseen(callback: RepositoryCallback<Int>) {
        val network = Parley.getInstance().network
        if (network.apiVersion.isSupportingMessageStatus.not()) {
            // This is not possible on older clientApi versions
            callback.onFailed(-1, "This clientApi version does not support retrieving the unseen count.")
            return
        }
        network.networkSession.request(
            network.url + network.path + "messages/unseen/count",
            null,
            ParleyHttpRequestMethod.Get,
            onCompletion = {
                val response = Gson()
                    .getAdapter(TypeTokenUnseenCount())
                    .fromJson(it)
                callback.onSuccess(response.data.count)
            },
            onFailed = { statusCode, message ->
                callback.onFailed(statusCode, message)
            }
        )
    }

    suspend fun updateStatusRead(messageIds: Set<Int>, callback: RepositoryCallback<Void>) {
        val network = Parley.getInstance().network
        if (network.apiVersion.isSupportingMessageStatus.not()) {
            // This is not possible on older clientApi versions
            callback.onSuccess(null)
            return
        }
        suspendCoroutine { cont ->
            network.networkSession.request(
                network.url + network.path + "messages/status/${MessageStatus.Read.key}",
                Gson().toJson(
                    UpdateMessageStatusRequest(
                        messageIds,
                    )
                ),
                ParleyHttpRequestMethod.Put,
                onCompletion = {
//                val response = Gson()
//                    .getAdapter(TypeTokenUpdateStatus())
//                    .fromJson(it)
                    callback.onSuccess(null)
                    cont.resume(Unit)
                },
                onFailed = { statusCode, message ->
                    callback.onFailed(statusCode, message)
                    cont.resume(Unit)
                }
            )
        }
    }
}
