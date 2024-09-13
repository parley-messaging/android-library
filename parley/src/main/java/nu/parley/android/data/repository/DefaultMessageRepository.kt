package nu.parley.android.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nu.parley.android.Parley
import nu.parley.android.data.model.Media
import nu.parley.android.data.model.Message
import nu.parley.android.data.model.MimeType.Companion.fromValue
import nu.parley.android.data.net.ParleyHttpRequestMethod
import nu.parley.android.data.net.RepositoryCallback
import nu.parley.android.data.net.response.ParleyPaging
import nu.parley.android.data.net.response.ParleyResponse
import nu.parley.android.data.net.response.ParleyResponsePostMedia
import nu.parley.android.data.net.response.ParleyResponsePostMessage
import nu.parley.android.data.net.service.DefaultNetworkSession
import nu.parley.android.util.FileUtil
import java.io.File

class DefaultMessageRepository : MessageRepository {
    private class MessageResponseTypeToken : TypeToken<ParleyResponse<Message>>()
    private class MessagesResponseTypeToken : TypeToken<ParleyResponse<List<Message>>>()
    private class MessagesResponsePostMessageTypeToken : TypeToken<ParleyResponse<ParleyResponsePostMessage>>()
    private class MessagesResponsePostMediaTypeToken : TypeToken<ParleyResponse<ParleyResponsePostMedia>>()

    public override fun findAll(callback: RepositoryCallback<ParleyResponse<List<Message>>>) {
        val network = Parley.getInstance().network
        network.networkSession.request(
            network.url + network.path + "messages",
            null,
            ParleyHttpRequestMethod.Get,
            emptyMap(),
            onCompetion = {
                val response = Gson()
                    .getAdapter(MessagesResponseTypeToken())
                    .fromJson(it)
                callback.onSuccess(response)
            },
            onFailed = { statusCode, message ->
                callback.onFailed(statusCode, message)
            }
        )
    }

    public override fun getOlder(
        previousPaging: ParleyPaging,
        callback: RepositoryCallback<ParleyResponse<List<Message>>>
    ) {
        val network = Parley.getInstance().network
        network.networkSession.request(
            network.url + network.path + previousPaging.before,
            null,
            ParleyHttpRequestMethod.Get,
            emptyMap(),
            onCompetion = {
                val response = Gson()
                    .getAdapter(MessagesResponseTypeToken())
                    .fromJson(it)
                callback.onSuccess(response)
            },
            onFailed = { statusCode, message ->
                callback.onFailed(statusCode, message)
            }
        )
    }

    public override fun send(
        message: Message,
        media: String?,
        callback: RepositoryCallback<Message>
    ) {
        val network = Parley.getInstance().network
        val onCompetion: (String) -> Unit = {
            val response = Gson()
                .getAdapter(MessagesResponsePostMessageTypeToken())
                .fromJson(it)

            var updatedMessage = Message.withIdAndStatus(
                message,
                response.data.messageId, Message.SEND_STATUS_SUCCESS
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
                emptyMap(),
                onCompetion = onCompetion,
                onFailed = onFailed
            )
        } else {
            network.networkSession.upload(
                url,
                File(media),
                FileUtil.getMimeType(File(media).absolutePath),
                "image",
                emptyMap(),
                onCompetion = onCompetion,
                onFailed = onFailed
            )
        }
    }

    public override fun sendMedia(
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
            emptyMap(),
            onCompetion = {
                val response = Gson()
                    .getAdapter(MessagesResponsePostMediaTypeToken())
                    .fromJson(it)

                val mediaType = FileUtil.getMimeType(media)
                val mimeType = fromValue(mediaType!!)
                val updatedMessage = Message.withMedia(
                    message,
                    Media(
                        response.data.mediaId!!, File(media).name, mimeType.value
                    )
                )
                callback.onSuccess(updatedMessage)
            },
            onFailed = { statusCode, errorMessage ->
                callback.onFailed(statusCode, errorMessage)
            }
        )
    }

    public override fun get(messageId: Int, callback: RepositoryCallback<Message>) {
        val network = Parley.getInstance().network
        network.networkSession.request(
            network.url + network.path + "messages/$messageId",
            null,
            ParleyHttpRequestMethod.Get,
            emptyMap(),
            onCompetion = {
                val response = Gson()
                    .getAdapter(MessageResponseTypeToken())
                    .fromJson(it)
                callback.onSuccess(response.data)
            },
            onFailed = { statusCode, message ->
                callback.onFailed(statusCode, message)
            }
        )
    }
}
