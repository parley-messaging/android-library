package nu.parley.android.data.messages;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import nu.parley.android.data.model.Message;
import nu.parley.android.data.net.response.ParleyPaging;
import nu.parley.android.util.CompareUtil;
import nu.parley.android.util.ListUtil;

import static nu.parley.android.util.DateUtil.isSameDay;
import static nu.parley.android.view.chat.MessageViewHolderFactory.MESSAGE_TYPE_AGENT_TYPING;

public final class MessagesManager {

    private final List<Message> originalMessages = new ArrayList<>(); // last = oldest
    private final List<Message> messages = new ArrayList<>(); // last = oldest

    private String welcomeMessage;
    private String stickyMessage;
    private ParleyPaging paging;

    private ParleyDataSource dataSource = null;

    public void setDataSource(@Nullable ParleyDataSource dataSource) {
        this.dataSource = dataSource;
        this.originalMessages.clear();

        if (dataSource == null) {
            this.welcomeMessage = null;
            this.paging = null;
        } else {
            this.originalMessages.addAll(dataSource.getAll());
            this.welcomeMessage = dataSource.get(ParleyKeyValueDataSource.KEY_MESSAGE_INFO);

            String cachedPaging = dataSource.get(ParleyKeyValueDataSource.KEY_PAGING);
            if (cachedPaging != null) {
                this.paging = new Gson().fromJson(cachedPaging, ParleyPaging.class);
            }
        }
        formatMessages();
    }

    /**
     * Returns the messages that are currently pending in a sorted way.
     *
     * @param oldestOnTop When `true`, oldest pending messages are on top of the returned list.
     * @return The pending messages
     */
    public List<Message> getPendingMessages(boolean oldestOnTop) {
        List<Message> pendingMessages = new ArrayList<>();
        for (Message originalMessage : originalMessages) {
            if (originalMessage.getSendStatus() == Message.SEND_STATUS_PENDING) {
                pendingMessages.add(originalMessage);
            }
        }
        if (oldestOnTop) {
            Collections.reverse(pendingMessages);
        }
        return pendingMessages;
    }

    public void begin(@Nullable String welcomeMessage, @Nullable String stickyMessage, List<Message> messages, ParleyPaging paging) {
        this.originalMessages.clear();
        this.originalMessages.addAll(messages);
        this.stickyMessage = stickyMessage;
        this.applyWelcomeMessage(welcomeMessage);
        this.applyPaging(paging);

        formatMessages();

        if (isCachingEnabled()) {
            dataSource.clear();
            dataSource.add(this.originalMessages);
        }
    }

    public void applyWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
        if (isCachingEnabled()) {
            dataSource.set(ParleyKeyValueDataSource.KEY_MESSAGE_INFO, welcomeMessage);
        }
    }

    public void moreLoad(List<Message> messages) {
        originalMessages.addAll(messages);
        formatMessages();

        if (isCachingEnabled()) {
            dataSource.add(messages);
        }
    }

    public void add(Message message) {
        int addIndex = messages.indexOf(getLatestMessage());
        if (addIndex == -1) {
            addIndex = 0;
        }
        // Check if we need to add a date
        if (messages.size() == 1) {
            // Only welcome message, add it
            messages.add(addIndex, Message.ofTypeDate(message.getDate()));
        } else if (getLatestMessage() == null || !isSameDay(getLatestMessage().getDate(), message.getDate())) {
            // Last message is of another day
            messages.add(addIndex, Message.ofTypeDate(message.getDate()));
        }
        // Add this message
        originalMessages.add(0, message);
        messages.add(addIndex, message);

        if (isCachingEnabled()) {
            dataSource.add(0, message);
        }
    }

    /**
     * Adds only the new messages that we did not have in the dataset yet.
     *
     * @param messages A list of messages which may have messages that we have shown earlier
     * @return True if it did add messages, false if the messages where the same
     */
    public boolean addOnlyNew(List<Message> messages) {
        boolean didAddMessage = false;
        Collections.reverse(messages);

        int pendingMessages = 0;
        for (Message originalMessage : originalMessages) {
            if (originalMessage.getSendStatus() == Message.SEND_STATUS_PENDING) {
                pendingMessages += 1;
            }
        }

        for (Message message : messages) {
            Integer index = null;
            for (int i = 0; i < originalMessages.size(); i++) {
                if (CompareUtil.equals(originalMessages.get(i).getId(), message.getId())) {
                    index = i;
                }
            }

            if (index == null) {
                didAddMessage = true;
                // Add them before the pending messages, as the pending messages will be later than the retrieved ones
                originalMessages.add(pendingMessages, message);
                if (isCachingEnabled()) {
                    dataSource.add(pendingMessages, message);
                }
            }
        }
        if (didAddMessage) {
            formatMessages();
        }
        return didAddMessage;
    }

    public void update(Message message) {
        Integer messagesIndex = null;
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getUuid() == message.getUuid()) {
                messagesIndex = i;
            }
        }

        Integer originalIndex = null;
        for (int i = 0; i < originalMessages.size(); i++) {
            if (originalMessages.get(i).getUuid() == message.getUuid()) {
                originalIndex = i;
            }
        }

        if (messagesIndex == null || originalIndex == null) {
            throw new IllegalArgumentException("Given non-existing message to update!");
        } else {
            originalMessages.set(originalIndex, message);
            messages.set(messagesIndex, message);

            if (isCachingEnabled()) {
                dataSource.update(message);
            }
        }
    }

    @Nullable
    private Message getLatestMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        Message firstMessage = messages.get(0);
        if (firstMessage.getTypeId() == MESSAGE_TYPE_AGENT_TYPING && messages.size() > 1) {
            return messages.get(1); // The onNext message
        } else {
            return firstMessage;
        }
    }

    private void formatMessages() {
        messages.clear();

        if (originalMessages.size() == 0) {
            addInfoMessage();
            return;
        }

        Date firstDate = ListUtil.getLast(originalMessages).getDate();

        Date lastDayDate = firstDate;

        for (Message message : originalMessages) {
            if (!isSameDay(lastDayDate, message.getDate())) {
                if (!isSameDay(lastDayDate, firstDate)) {
                    // Add date message
                    messages.add(Message.ofTypeDate(lastDayDate));
                }
                lastDayDate = message.getDate();
            }
            messages.add(message);
        }
        messages.add(Message.ofTypeDate(firstDate));

        if (!canLoadMore()) {
            addInfoMessage();
        }
    }

    private void addInfoMessage() {
        if (welcomeMessage != null) {
            messages.add(Message.ofTypeInfo(welcomeMessage));
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addAgentTypingMessage() {
        messages.add(0, Message.ofTypeAgentTyping());
    }

    public void removeAgentTypingMessage() {
        if (messages.isEmpty()) {
            // Nothing to do
            return;
        }
        if (messages.get(0).getTypeId() == MESSAGE_TYPE_AGENT_TYPING) {
            messages.remove(0);
        }
    }

    @Nullable
    public String getStickyMessage() {
        return stickyMessage;
    }

    @Nullable
    public ParleyPaging getPaging() {
        return paging;
    }

    public void applyPaging(ParleyPaging paging) {
        this.paging = paging;
        if (isCachingEnabled()) {
            dataSource.set(ParleyKeyValueDataSource.KEY_PAGING, new Gson().toJson(paging));
        }
    }

    public boolean isCachingEnabled() {
        return dataSource != null;
    }

    public boolean canLoadMore() {
        return paging != null && paging.getBefore() != null;
    }

    public List<String> getAvailableQuickReplies() {
        if (getLatestMessage() == null || getLatestMessage().getQuickReplies() == null) {
            return new ArrayList<>();
        }
        return getLatestMessage().getQuickReplies();
    }

    public void clear(boolean clearDataSource) {
        this.originalMessages.clear();
        this.messages.clear();
        this.welcomeMessage = null;
        this.stickyMessage = null;
        this.paging = null;
        if (clearDataSource && dataSource != null) {
            dataSource.clear();
        }
        setDataSource(dataSource); // Reset cache
    }

    public void disableCaching() {
        if(this.dataSource != null) {
            this.dataSource.clear();
        }
        this.dataSource = null;
    }
}
