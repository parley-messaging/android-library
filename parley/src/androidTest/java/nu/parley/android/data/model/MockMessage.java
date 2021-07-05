package nu.parley.android.data.model;

import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Random;

import nu.parley.android.view.chat.MessageViewHolderFactory;

public final class MockMessage {

    private static int generateRandomId() {
        return new Random().nextInt();
    }

    private static long getCurrentTimeStamp() {
        return new Date().getTime() / 1000;
    }

    public static Message textOfUser(String text, int sendStatus) {
        return messageOfUser(text, null, sendStatus);
    }

    public static Message imageOfUser(String url, int sendStatus) {
        return messageOfUser(null, url, sendStatus);
    }

    public static Message textOfAgent(Agent agent, String text) {
        return messageOfAgent(agent, null, text, null);
    }

    public static Message imageOfAgent(Agent agent, String url) {
        return messageOfAgent(agent, null, null, url);
    }

    public static Message messageOfUser(String text, String imageUrl, int sendStatus) {
        return new Message(generateRandomId(), getCurrentTimeStamp(), null, text, imageUrl, MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_OWN, null, sendStatus, null, null);
    }

    public static Message messageOfAgent(Agent agent, @Nullable String title, String text, String imageUrl) {
        return new Message(generateRandomId(), getCurrentTimeStamp(), title, text, imageUrl, MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT, agent, Message.SEND_STATUS_SUCCESS, null, null);
    }

    public static Message messageOfAgent(Agent agent, @Nullable String title, String text, String imageUrl, List<Action> actions, List<Message> carousel) {
        return new Message(generateRandomId(), getCurrentTimeStamp(), title, text, imageUrl, MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT, agent, Message.SEND_STATUS_SUCCESS, actions, carousel);
    }

    public static Message create(@Nullable Integer id, @Nullable Long timeStamp, @Nullable String title, String message, String imageUrl, @Nullable Integer typeId, Agent agent, int sendStatus, List<Message> carousel) {
        return new Message(id, timeStamp, title, message, imageUrl, typeId, agent, sendStatus, null, carousel);
    }

    public static Message ofCarousel(String title, String text, String url, List<Action> actions) {
        return new Message(null, null, title, text, url, null, null, Message.SEND_STATUS_SUCCESS, actions, null);
    }
}
