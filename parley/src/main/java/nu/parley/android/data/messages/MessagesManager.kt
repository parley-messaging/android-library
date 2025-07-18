package nu.parley.android.data.messages

import com.google.gson.Gson
import nu.parley.android.data.model.Message
import nu.parley.android.data.model.MessageStatus
import nu.parley.android.data.net.response.base.PagingResponse
import nu.parley.android.util.CompareUtil
import nu.parley.android.util.DateUtil
import nu.parley.android.util.ListUtil
import nu.parley.android.view.chat.MessageViewHolderFactory
import java.util.Collections

internal class MessagesManager {
    private val originalMessages: MutableList<Message> = ArrayList<Message>() // last = oldest
    @JvmField
    val messages: MutableList<Message> = ArrayList<Message>() // last = oldest

    private var welcomeMessage: String? = null
    var stickyMessage: String? = null
        private set
    var paging: PagingResponse? = null
        private set

    private var dataSource: ParleyDataSource? = null

    fun setDataSource(dataSource: ParleyDataSource?) {
        this.dataSource = dataSource
        this.originalMessages.clear()

        if (dataSource == null) {
            this.welcomeMessage = null
            this.paging = null
        } else {
            this.originalMessages.addAll(dataSource.getAll())
            this.welcomeMessage = dataSource.get(ParleyKeyValueDataSource.KEY_MESSAGE_INFO)

            val cachedPaging = dataSource.get(ParleyKeyValueDataSource.KEY_PAGING)
            if (cachedPaging != null) {
                this.paging =
                    Gson().fromJson<PagingResponse?>(cachedPaging, PagingResponse::class.java)
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
    fun getPendingMessages(oldestOnTop: Boolean): MutableList<Message> {
        val pendingMessages: MutableList<Message> = ArrayList<Message>()
        for (originalMessage in originalMessages) {
            if (originalMessage.getSendStatus() == Message.SEND_STATUS_PENDING) {
                pendingMessages.add(originalMessage)
            }
        }
        if (oldestOnTop) {
            Collections.reverse(pendingMessages)
        }
        return pendingMessages
    }

    fun begin(
        welcomeMessage: String?,
        stickyMessage: String?,
        messages: MutableList<Message>,
        paging: PagingResponse?
    ) {
        this.originalMessages.clear()
        this.originalMessages.addAll(messages)
        this.stickyMessage = stickyMessage
        this.applyWelcomeMessage(welcomeMessage)
        this.applyPaging(paging)

        formatMessages()

        if (this.isCachingEnabled) {
            dataSource!!.clear()
            dataSource!!.add(this.originalMessages)
        }
    }

    fun applyWelcomeMessage(welcomeMessage: String?) {
        this.welcomeMessage = welcomeMessage
        if (this.isCachingEnabled) {
            dataSource!!.set(ParleyKeyValueDataSource.KEY_MESSAGE_INFO, welcomeMessage)
        }
    }


    fun moreLoad(messages: MutableList<Message>) {
        originalMessages.addAll(messages)
        formatMessages()

        if (this.isCachingEnabled) {
            dataSource!!.add(messages)
        }
    }

    fun add(message: Message) {
        var addIndex = messages.indexOf(findLatestMessage())
        if (addIndex == -1) {
            addIndex = 0
        }
        // Check if we need to add a date
        if (messages.size == 1) {
            // Only welcome message, add it
            messages.add(addIndex, Message.ofTypeDate(message.getDate()))
        } else if (findLatestMessage() == null || !DateUtil.isSameDay(
                findLatestMessage()!!.getDate(),
                message.getDate()
            )
        ) {
            // Last message is of another day
            messages.add(addIndex, Message.ofTypeDate(message.getDate()))
        }
        // Add this message
        originalMessages.add(0, message)
        messages.add(addIndex, message)

        if (this.isCachingEnabled) {
            dataSource!!.add(0, message)
        }
    }

    /**
     * Adds only the new messages that we did not have in the dataset yet.
     *
     * @param messages A list of messages which may have messages that we have shown earlier
     * @return True if it did add messages, false if the messages where the same
     */
    fun addOnlyNew(messages: MutableList<Message>): Boolean {
        var didAddMessage = false
        Collections.reverse(messages)

        var pendingMessages = 0
        for (originalMessage in originalMessages) {
            if (originalMessage.getSendStatus() == Message.SEND_STATUS_PENDING) {
                pendingMessages += 1
            }
        }

        for (message in messages) {
            var index: Int? = null
            for (i in originalMessages.indices) {
                if (CompareUtil.equals(originalMessages.get(i).getId(), message.getId())) {
                    index = i
                }
            }

            if (index == null) {
                didAddMessage = true
                // Add them before the pending messages, as the pending messages will be later than the retrieved ones
                originalMessages.add(pendingMessages, message)
                if (this.isCachingEnabled) {
                    dataSource!!.add(pendingMessages, message)
                }
            }
        }
        if (didAddMessage) {
            formatMessages()
        }
        return didAddMessage
    }

    fun update(message: Message) {
        var messagesIndex: Int? = null
        for (i in messages.indices) {
            if (messages.get(i).getUuid() === message.getUuid()) {
                messagesIndex = i
            }
        }

        var originalIndex: Int? = null
        for (i in originalMessages.indices) {
            if (originalMessages.get(i).getUuid() === message.getUuid()) {
                originalIndex = i
            }
        }

        require(!(messagesIndex == null || originalIndex == null)) { "Given non-existing message to update!" }
        originalMessages.set(originalIndex, message)
        messages.set(messagesIndex, message)

        if (this.isCachingEnabled) {
            dataSource!!.update(message)
        }
    }

    private fun findLatestMessage(): Message? {
        if (messages.isEmpty()) {
            return null
        }
        val firstMessage = messages.get(0)
        if (firstMessage.getTypeId() == MessageViewHolderFactory.MESSAGE_TYPE_AGENT_TYPING && messages.size > 1) {
            return messages.get(1) // The onNext message
        } else {
            return firstMessage
        }
    }

    private fun formatMessages() {
        messages.clear()

        if (originalMessages.size == 0) {
            addInfoMessage()
            return
        }

        val firstDate = ListUtil.getLast<Message?>(originalMessages).getDate()

        var lastDayDate = firstDate

        for (message in originalMessages) {
            if (!DateUtil.isSameDay(lastDayDate, message.getDate())) {
                if (!DateUtil.isSameDay(lastDayDate, firstDate)) {
                    // Add date message
                    messages.add(Message.ofTypeDate(lastDayDate))
                }
                lastDayDate = message.getDate()
            }
            messages.add(message)
        }
        messages.add(Message.ofTypeDate(firstDate))

        if (!canLoadMore()) {
            addInfoMessage()
        }
    }

    private fun addInfoMessage() {
        if (welcomeMessage != null) {
            messages.add(Message.ofTypeInfo(welcomeMessage))
        }
    }

    fun addAgentTypingMessage() {
        messages.add(0, Message.ofTypeAgentTyping())
    }

    fun removeAgentTypingMessage() {
        if (messages.isEmpty()) {
            // Nothing to do
            return
        }
        if (messages[0].typeId == MessageViewHolderFactory.MESSAGE_TYPE_AGENT_TYPING) {
            messages.removeAt(0)
        }
    }

    fun applyPaging(paging: PagingResponse?) {
        this.paging = paging
        if (this.isCachingEnabled) {
            dataSource!!.set(ParleyKeyValueDataSource.KEY_PAGING, Gson().toJson(paging))
        }
    }

    val isCachingEnabled: Boolean
        get() = dataSource != null

    fun canLoadMore(): Boolean {
        return paging != null && !paging!!.before.isBlank()
    }

    val availableQuickReplies: MutableList<String?>?
        get() {
            if (findLatestMessage() == null || findLatestMessage()!!.getQuickReplies() == null) {
                return ArrayList<String?>()
            }
            return findLatestMessage()!!.getQuickReplies()
        }

    fun clear(clearDataSource: Boolean) {
        this.originalMessages.clear()
        this.messages.clear()
        this.welcomeMessage = null
        this.stickyMessage = null
        this.paging = null
        if (clearDataSource && dataSource != null) {
            dataSource!!.clear()
        }
        setDataSource(dataSource) // Reset cache
    }

    fun disableCaching() {
        if (this.dataSource != null) {
            this.dataSource!!.clear()
        }
        this.dataSource = null
    }

    fun updateRead(messageIds: MutableSet<Int?>) {
        for (message in originalMessages) {
            if (messageIds.contains(message.getId())) {
                message.setStatus(MessageStatus.Read)
            }
        }
        formatMessages()
    }
}
