package nu.parley.android

import nu.parley.android.data.messages.MessagesManager
import nu.parley.android.data.model.Message
import org.junit.Assert
import org.junit.Test
import java.util.Collections
import java.util.Date

class MessagesManagerUnitTest {

    companion object {
        private const val MessageWelcomeText = "Welcome message"
        private const val MessageStickyText = "Sticky message"
        private val DateToday = Date()
        private val DateYesterday = Date(DateToday.time - 1000 * 60 * 60 * 24)
    }

    @Test
    fun stickyMessage_shouldMatch() {
        val messagesManager = MessagesManager()

        messagesManager.begin(
            MessageWelcomeText,
            MessageStickyText,
            listOf(),
            null,
        )
        Assert.assertEquals(MessageStickyText, messagesManager.getStickyMessage())
    }

    @Test
    fun emptyChat_containsWelcomeWithoutDate() {
        val messagesManager = MessagesManager()

        messagesManager.begin(
            MessageWelcomeText,
            MessageStickyText,
            listOf(),
            null
        )

        val initialMessages = messagesManager.getMessages()
        Assert.assertEquals("Only welcome message exists", 1, initialMessages.size.toLong())
        Assert.assertEquals(
            "First message is the welcome message", MessageWelcomeText, initialMessages[0].message
        )
    }

    @Test
    fun todayMessages_showsWelcomeAsFirstOnToday() {
        val messagesManager = MessagesManager()

        messagesManager.begin(
            MessageWelcomeText,
            MessageStickyText,
            listOf(),
            null
        )
        val firstMessageText = "Goodmorning"
        messagesManager.add(
            Message.withMessageAndDate(
                Message.ofTypeOwnMessage(firstMessageText),
                firstMessageText,
                DateToday
            )
        ) // A message of today

        val currentMessages = messagesManager.getMessages()
        Assert.assertEquals("3 messages exists", 3, messagesManager.getMessages().size)
        Assert.assertEquals(
            "1: The current date message", DateToday.toString(), currentMessages[0].message
        )
        Assert.assertEquals(
            "2: Welcome message",
            MessageWelcomeText,
            currentMessages[1].message
        )
        Assert.assertEquals(
            "3: The actual message",
            firstMessageText,
            currentMessages[2].message
        )
    }

    @Test
    fun history_showsWelcomeOnFirstOfToday() {
        val messagesManager = MessagesManager()

        val yesterdayMessageText = "Goodmorning"
        messagesManager.begin(
            MessageWelcomeText,
            MessageStickyText,
            listOf(
                Message.withMessageAndDate(
                    Message.ofTypeOwnMessage(yesterdayMessageText),
                    yesterdayMessageText,
                    DateYesterday
                )
            ),
            null
        )

        // Today's message
        val todayMessageText = "Hello!"
        messagesManager.add(
            Message.withMessageAndDate(
                Message.ofTypeOwnMessage(todayMessageText),
                todayMessageText,
                DateToday
            )
        )

        val currentMessages = messagesManager.getMessages()
        Assert.assertEquals("5 messages exists", 5, messagesManager.getMessages().size)
        Assert.assertEquals(
            "1: The yesterday date message", DateYesterday.toString(), currentMessages[0].message
        )
        Assert.assertEquals(
            "2: The yesterday message", yesterdayMessageText, currentMessages[1].message
        )
        Assert.assertEquals(
            "3: The today date message", DateToday.toString(), currentMessages[2].message
        )
        Assert.assertEquals(
            "4: Welcome message",
            MessageWelcomeText,
            currentMessages[3].message
        )
        Assert.assertEquals(
            "5: The today message",
            todayMessageText,
            currentMessages[4].message
        )
    }

    @Test
    fun history_showsWelcomeAtEnd() {
        val messagesManager = MessagesManager()

        val yesterdayMessageText = "Goodmorning"
        messagesManager.begin(
            MessageWelcomeText,
            MessageStickyText,
            listOf(
                Message.withMessageAndDate(
                    Message.ofTypeOwnMessage(yesterdayMessageText),
                    yesterdayMessageText,
                    DateYesterday
                )
            ),
            null
        )

        val currentMessages = messagesManager.getMessages()
        Assert.assertEquals("3 messages exists", 3, messagesManager.getMessages().size)
        Assert.assertEquals(
            "1: The yesterday date message", DateYesterday.toString(), currentMessages[0].message
        )
        Assert.assertEquals(
            "2: The yesterday message", yesterdayMessageText, currentMessages[1].message
        )
        Assert.assertEquals(
            "3: Welcome message",
            MessageWelcomeText,
            currentMessages[2].message
        )
    }
}