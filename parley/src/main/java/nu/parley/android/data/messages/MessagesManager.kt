package nu.parley.android.data.messages

import com.google.gson.Gson
import nu.parley.android.data.model.Message
import nu.parley.android.data.model.MessageStatus
import nu.parley.android.data.net.response.base.PagingResponse
import nu.parley.android.util.CompareUtil
import nu.parley.android.util.DateUtil
import nu.parley.android.view.chat.MessageViewHolderFactory
import java.util.Date
import java.util.UUID

internal class MessagesManager {
    private val originalMessages = mutableListOf<Message>()
    private val messages = mutableListOf<Message>()

    private var dateUuids = mutableMapOf<Date, UUID>()
    private var welcomeMessageUuid = UUID.randomUUID()
    private var welcomeMessage = Message.ofTypeInfo(welcomeMessageUuid, "")
    private var stickyMessage: String? = null
    private var paging: PagingResponse? = null

    private var dataSource: ParleyDataSource? = null

    fun getPaging() = paging
    fun getStickyMessage() = stickyMessage
    fun getMessages() = messages
        .sortedBy { it.typeId }
        .sortedBy { it.date }

    fun setDataSource(dataSource: ParleyDataSource?) {
        this.dataSource = dataSource
        originalMessages.clear()

        if (dataSource == null) {
            welcomeMessage = null
            paging = null
        } else {
            originalMessages.addAll(dataSource.getAll())
            welcomeMessage = Message.ofTypeInfo(
                welcomeMessageUuid,
                dataSource.get(ParleyKeyValueDataSource.KEY_MESSAGE_INFO)
            )

            dataSource.get(ParleyKeyValueDataSource.KEY_PAGING)?.let { cached ->
                paging = Gson().fromJson(cached, PagingResponse::class.java)
            }
        }
        formatMessages()
    }

    /**
     * Returns the messages that are currently pending in a sorted way.
     *
     * @param oldestOnTop When `true`, oldest pending messages are on top of the returned list.
     * @return The pending messages
     */
    fun getPendingMessages(oldestOnTop: Boolean): List<Message> {
        val result = originalMessages.filter {
            it.sendStatus == Message.SEND_STATUS_PENDING
        }
        return if (oldestOnTop) {
            result.sortedByDescending { it.date }
        } else {
            result.sortedBy { it.date }
        }
    }

    fun begin(
        welcomeMessage: String?,
        stickyMessage: String?,
        messages: List<Message>,
        paging: PagingResponse?
    ) {
        this.originalMessages.clear()
        this.originalMessages.addAll(messages.sortedBy { it.date })
        this.stickyMessage = stickyMessage
        this.applyWelcomeMessage(welcomeMessage)
        this.applyPaging(paging)

        formatMessages()
        dataSource?.clear()
        dataSource?.add(this.originalMessages)
    }

    fun applyWelcomeMessage(message: String?) {
        welcomeMessage = Message.ofTypeInfo(welcomeMessageUuid, message)
        dataSource?.set(ParleyKeyValueDataSource.KEY_MESSAGE_INFO, message)
    }

    fun moreLoad(messages: MutableList<Message>) {
        originalMessages.addAll(messages.sortedBy { it.date })
        formatMessages()

        dataSource?.add(messages)
    }

    fun add(message: Message) {
        originalMessages.add(message)
        formatMessages()
        dataSource?.add(0, message)
    }

    /**
     * Adds only the new messages that we did not have in the dataset yet.
     *
     * @param messages A list of messages which may have messages that we have shown earlier
     * @return True if it did add messages, false if the messages where the same
     */
    fun addOnlyNew(messages: MutableList<Message>): Boolean {
        var didAddMessage = false
        messages.reverse()

        val pendingMessages =
            originalMessages.count { it.sendStatus == Message.SEND_STATUS_PENDING }

        for (message in messages) {
            var index: Int? = null
            for (i in originalMessages.indices) {
                if (CompareUtil.equals(originalMessages[i].id, message.id)) {
                    index = i
                }
            }

            if (index == null) {
                didAddMessage = true
                // Add them before the pending messages, as the pending messages will be later than the retrieved ones
                originalMessages.add(pendingMessages, message)
                dataSource?.add(pendingMessages, message)
            }
        }
        if (didAddMessage) {
            formatMessages()
        }
        return didAddMessage
    }

    fun update(message: Message) {
        val messagesIndex = messages.indexOfFirst { it.uuid == message.uuid }
        val originalIndex = originalMessages.indexOfFirst { it.uuid == welcomeMessageUuid }

        require(messagesIndex != -1) { "Given non-existing message to update!" }
        require(originalIndex != -1) { "Given non-existing message to update!" }

        originalMessages[originalIndex] = message
        messages[messagesIndex] = message

        dataSource?.update(message)
    }

    private fun findMostRecentMessage(): Message? {
        return messages
            .sortedByDescending { it.date }
            .firstOrNull {
                it.typeId != MessageViewHolderFactory.MESSAGE_TYPE_AGENT_TYPING
            }
    }

    private fun formatMessages() {
        messages.clear()

        if (originalMessages.isEmpty()) {
            addWelcomeMessage()
        } else {
            val sorted = originalMessages.sortedBy { it.date }
            var workingDate = sorted.first().date
            var addedWelcome = false
            addDateMessage(workingDate)
            sorted.forEach { message ->
                val messageDate = message.date
                if (DateUtil.isSameDay(workingDate, messageDate).not()) {
                    addDateMessage(messageDate)
                    workingDate = messageDate
                }
                if (DateUtil.isSameDay(workingDate, DateUtil.Today) && addedWelcome.not()) {
                    addWelcomeMessage()
                    addedWelcome = true
                }
                addChatMessage(message)
            }
            if (addedWelcome.not()) {
                addWelcomeMessage()
            }
        }
    }

    private fun addWelcomeMessage() {
        welcomeMessage?.let {
            messages.add(it)
        }
    }

    private fun addDateMessage(date: Date?) {
        val uuid = date?.let {
            dateUuids.getOrPut(it) { UUID.randomUUID() }
        }
        messages.add(Message.ofTypeDate(uuid, date))
    }

    private fun addChatMessage(message: Message) {
        messages.add(message)
    }

    fun addAgentTypingMessage() {
        messages.add(Message.ofTypeAgentTyping())
    }

    fun removeAgentTypingMessage() {
        messages.removeAll { it.typeId == MessageViewHolderFactory.MESSAGE_TYPE_AGENT_TYPING }
    }

    fun applyPaging(paging: PagingResponse?) {
        this.paging = paging
        dataSource?.set(ParleyKeyValueDataSource.KEY_PAGING, Gson().toJson(paging))
    }

    fun isCachingEnabled() = dataSource != null

    fun canLoadMore(): Boolean {
        return paging != null && !paging!!.before.isBlank()
    }

    fun getAvailableQuickReplies() = findMostRecentMessage()?.quickReplies.orEmpty()

    fun clear(clearDataSource: Boolean) {
        originalMessages.clear()
        messages.clear()
        welcomeMessage = null
        stickyMessage = null
        paging = null
        if (clearDataSource) {
            dataSource?.clear()
        }
        setDataSource(dataSource) // Reset cache
    }

    fun disableCaching() {
        dataSource?.clear()
        dataSource = null
    }

    fun updateRead(messageIds: MutableSet<Int?>) {
        for (message in originalMessages) {
            if (messageIds.contains(message.id)) {
                message.status = MessageStatus.Read
            }
        }
        formatMessages()
    }
}
