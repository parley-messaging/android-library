package nu.parley.android.util.mock

import nu.parley.android.data.model.Action
import nu.parley.android.data.model.Agent
import nu.parley.android.data.model.Media
import nu.parley.android.data.model.Message
import nu.parley.android.view.chat.MessageViewHolderFactory
import java.util.Date
import java.util.Random

internal object MockMessage {
    private fun generateRandomId() = Random().nextInt()

    private fun generateTimeStamp() = Date().time / 1000

    fun userText(text: String?, sendStatus: Int): Message {
        return userTextAndMedia(text, null, sendStatus)
    }

    fun userImage(url: String, sendStatus: Int): Message {
        return Message(
            generateRandomId(),
            generateTimeStamp(),
            null,
            null,
            url,
            null,
            MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_OWN,
            null,
            sendStatus,
            null,
            null
        )
    }

    fun userMedia(media: Media?, sendStatus: Int): Message {
        return userTextAndMedia(null, media, sendStatus)
    }

    fun agentText(agent: Agent?, text: String?): Message {
        return agentMessageAndMedia(agent, null, text, null)
    }

    fun agentImage(agent: Agent?, image: String?): Message {
        return return Message(
            generateRandomId(),
            generateTimeStamp(),
            null,
            null,
            image,
            null,
            MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT,
            agent,
            Message.SEND_STATUS_SUCCESS,
            null,
            null
        )
    }

    fun agentMedia(agent: Agent?, media: Media?): Message {
        return agentMessageAndMedia(agent, null, null, media)
    }

    fun userTextAndImage(text: String?, image: String?, sendStatus: Int): Message {
        return Message(
            generateRandomId(),
            generateTimeStamp(),
            null,
            text,
            image,
            null,
            MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_OWN,
            null,
            sendStatus,
            null,
            null
        )
    }

    fun userTextAndMedia(text: String?, media: Media?, sendStatus: Int): Message {
        return Message(
            generateRandomId(),
            generateTimeStamp(),
            null,
            text,
            null,
            media,
            MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_OWN,
            null,
            sendStatus,
            null,
            null
        )
    }

    fun agentMessageAndImage(agent: Agent?, title: String?, text: String?, image: String?): Message {
        return Message(
            generateRandomId(),
            generateTimeStamp(),
            title,
            text,
            image,
            null,
            MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT,
            agent,
            Message.SEND_STATUS_SUCCESS,
            null,
            null
        )
    }

    fun agentMessageAndMedia(agent: Agent?, title: String?, text: String?, media: Media?): Message {
        return Message(
            generateRandomId(),
            generateTimeStamp(),
            title,
            text,
            null,
            media,
            MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT,
            agent,
            Message.SEND_STATUS_SUCCESS,
            null,
            null
        )
    }

    fun agentFullImage(
        agent: Agent?,
        title: String?,
        text: String?,
        image: String?,
        actions: List<Action?>?,
        carousel: List<Message?>?
    ): Message {
        return Message(
            generateRandomId(),
            generateTimeStamp(),
            title,
            text,
            image,
            null,
            MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT,
            agent,
            Message.SEND_STATUS_SUCCESS,
            actions,
            carousel
        )
    }

    fun agentFull(
        agent: Agent?,
        title: String?,
        text: String?,
        media: Media?,
        actions: List<Action?>?,
        carousel: List<Message?>?
    ): Message {
        return Message(
            generateRandomId(),
            generateTimeStamp(),
            title,
            text,
            null,
            media,
            MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT,
            agent,
            Message.SEND_STATUS_SUCCESS,
            actions,
            carousel
        )
    }

    fun create(
        id: Int?,
        timeStamp: Long?,
        title: String?,
        message: String?,
        media: Media?,
        typeId: Int?,
        agent: Agent?,
        sendStatus: Int,
        carousel: List<Message?>?
    ): Message {
        return Message(
            id,
            timeStamp,
            title,
            message,
            null,
            media,
            typeId,
            agent,
            sendStatus,
            null,
            carousel
        )
    }

    fun agentCarousel(
        title: String?,
        text: String?,
        image: String?,
        actions: List<Action?>?
    ): Message {
        return Message(
            null,
            null,
            title,
            text,
            image,
            null,
            null,
            null,
            Message.SEND_STATUS_SUCCESS,
            actions,
            null
        )
    }
}
