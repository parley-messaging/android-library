package nu.parley.android;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import nu.parley.android.data.messages.MessagesManager;
import nu.parley.android.data.model.Message;

import static org.junit.Assert.assertEquals;

public class MessagesManagerUnitTest {

    private static final String MESSAGE_WELCOME_TEXT = "Welcome message";
    private static final String MESSAGE_STICKY_TEXT = "Sticky message";

    @Test
    public void messagesManager_stickyMessage() {
        MessagesManager messagesManager = new MessagesManager();

        messagesManager.begin(MESSAGE_WELCOME_TEXT, MESSAGE_STICKY_TEXT, new ArrayList<Message>(), null);
        assertEquals(MESSAGE_STICKY_TEXT, messagesManager.getStickyMessage());
    }

    @Test
    public void messagesManager_emptyChat() {
        MessagesManager messagesManager = new MessagesManager();

        messagesManager.begin(MESSAGE_WELCOME_TEXT, MESSAGE_STICKY_TEXT, new ArrayList<Message>(), null);

        List<Message> initialMessages = messagesManager.getMessages();
        assertEquals("Only welcome message exists", 1, initialMessages.size());
        assertEquals("First message is the welcome message", MESSAGE_WELCOME_TEXT, initialMessages.get(0).getMessage());
    }

    @Test
    public void messagesManager_dateMessages_today() {
        MessagesManager messagesManager = new MessagesManager();

        messagesManager.begin(MESSAGE_WELCOME_TEXT, MESSAGE_STICKY_TEXT, new ArrayList<Message>(), null);
        Date currentDate = new Date();
        String firstMessageText = "Goodmorning";
        messagesManager.add(Message.withMessageAndDate(Message.ofTypeOwnMessage(firstMessageText), firstMessageText, currentDate)); // A message of today

        List<Message> currentMessages = messagesManager.getMessages();
        Collections.reverse(currentMessages); // For easier testing, format it the other way around
        assertEquals("1: Welcome message", MESSAGE_WELCOME_TEXT, currentMessages.get(0).getMessage());
        assertEquals("2: The current date message", currentDate.toString(), currentMessages.get(1).getMessage());
        assertEquals("3: The actual message", firstMessageText, currentMessages.get(2).getMessage());
    }

    @Test
    public void messagesManager_dateMessages_earlier() {
        MessagesManager messagesManager = new MessagesManager();

        final Date yesterdayDate = new Date(new Date().getTime() - 1000 * 60 * 60 * 24);
        final String yesterdayMessageText = "Goodmorning";
        messagesManager.begin(MESSAGE_WELCOME_TEXT, MESSAGE_STICKY_TEXT, new ArrayList<Message>() {{
            // Existing messages
            add(Message.withMessageAndDate(Message.ofTypeOwnMessage(yesterdayMessageText), yesterdayMessageText, yesterdayDate));
        }}, null);

        // Today's message
        Date todayDate = new Date();
        String todayMessageText = "Hello!";
        messagesManager.add(Message.withMessageAndDate(Message.ofTypeOwnMessage(todayMessageText), todayMessageText, todayDate));

        List<Message> currentMessages = messagesManager.getMessages();
        Collections.reverse(currentMessages); // For easier testing, format it the other way around
        assertEquals("1: Welcome message", MESSAGE_WELCOME_TEXT, currentMessages.get(0).getMessage());
        assertEquals("2: The yesterday date message", yesterdayDate.toString(), currentMessages.get(1).getMessage());
        assertEquals("3: The yesterday message", yesterdayMessageText, currentMessages.get(2).getMessage());
        assertEquals("4: The today date message", todayDate.toString(), currentMessages.get(3).getMessage());
        assertEquals("5: The today message", todayMessageText, currentMessages.get(4).getMessage());
    }
}