package nu.parleynetwork.android.data.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import nu.parley.android.data.model.Action;
import nu.parley.android.data.model.Agent;
import nu.parley.android.data.model.Media;
import nu.parley.android.data.model.Message;
import nu.parley.android.data.model.PushMessage;
import nu.parley.android.util.CompareUtil;
import nu.parley.android.view.chat.MessageViewHolderFactory;

/**
 * Message model for the Parley chat. Every item shown in the chat is a message.
 */
public final class MessageJson {

    public final static int SEND_STATUS_PENDING = 0;
    public final static int SEND_STATUS_SUCCESS = 1;
    public final static int SEND_STATUS_FAILED = 2;

    private UUID uuid = UUID.randomUUID();

    @SerializedName("id")
    @Nullable
    private Integer id;

    @SerializedName("time")
    @Nullable
    private Long timeStamp;

    @SerializedName("title")
    @Nullable
    private String title;

    @SerializedName("message")
    @Nullable
    private String message;

    /**
     * Not serialized from backend, but serialized by datasource when offline messaging is enabled.
     */
    @SerializedName("response_info_type")
    @Nullable
    private String responseInfoType;

    /**
     * <b>Deprecated</b>: Use `media` instead
     */
    @SerializedName("image")
    @Nullable
    @Deprecated
    private String image;

    @SerializedName("mediaLocal")
    @Nullable
    private String localUrl;

    @SerializedName("media")
    @Nullable
    private MediaJson media;

    @SerializedName("buttons")
    @Nullable
    private List<ActionJson> actions;

    @SerializedName("carousel")
    @Nullable
    private List<MessageJson> carousel;

    @SerializedName("quickReplies")
    @Nullable
    private List<String> quickReplies;

    @SerializedName("typeId")
    @Nullable
    private Integer typeId; // Inside carousels, this key doesn't exist and therefore is `null`

    @SerializedName("agent")
    @Nullable
    private AgentJson agent;

    @SerializedName("send_status")
    private int sendStatus = SEND_STATUS_SUCCESS;

    private MessageJson() {
        // Hide constructor
    }

    private MessageJson(UUID uuid, @Nullable Integer id, long timeStamp, @Nullable String message, @Nullable String localUrl, @Nullable MediaJson media, int typeId, @Nullable AgentJson agent, int sendStatus) {
        this.uuid = uuid;
        this.id = id;
        this.timeStamp = timeStamp;
        this.message = message;
        this.localUrl = localUrl;
        this.media = media;
        this.typeId = typeId;
        this.agent = agent;
        this.sendStatus = sendStatus;
    }

    public MessageJson(@Nullable Integer id, Long timeStamp, @Nullable String title, @Nullable String message, @Nullable String localUrl, @Nullable MediaJson media, Integer typeId, @Nullable AgentJson agent, int sendStatus, @Nullable List<ActionJson> actions, @Nullable List<MessageJson> carousel) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.title = title;
        this.localUrl = localUrl;
        this.image = localUrl;
        this.message = message;
        this.media = media;
        this.typeId = typeId;
        this.agent = agent;
        this.sendStatus = sendStatus;
        this.actions = actions;
        this.carousel = carousel;
    }

    private static MessageJson ofType(int typeId) {
        MessageJson message = new MessageJson();
        message.id = null;
        message.typeId = typeId;
        message.timeStamp = new Date().getTime() / 1000;
        return message;
    }

    public static MessageJson ofTypeInfo(String text) {
        MessageJson message = ofType(MessageViewHolderFactory.MESSAGE_TYPE_INFO);
        message.message = text;
        return message;
    }

    public static MessageJson ofTypeDate(Date date) {
        MessageJson message = ofType(MessageViewHolderFactory.MESSAGE_TYPE_DATE);
        message.message = date.toString();
        message.timeStamp = date.getTime() / 1000;
        return message;
    }

    public static MessageJson ofTypeAgentTyping() {
        return ofType(MessageViewHolderFactory.MESSAGE_TYPE_AGENT_TYPING);
    }

    public static MessageJson ofTypeOwnMessage(String text) {
        return ofTypeOwnMessage(text, false);
    }

    public static MessageJson ofTypeOwnMessage(String text, boolean silent) {
        MessageJson message = MessageJson.ofTypeOwnMessage(silent);
        message.message = text;
        return message;
    }

    public static MessageJson ofTypeOwnPendingMedia(File mediaFile) {
        MessageJson message = MessageJson.ofTypeOwnMessage(false);
        message.localUrl = mediaFile.getAbsolutePath();
        return message;
    }

    private static MessageJson ofTypeOwnMessage(boolean silent) {
        MessageJson message = ofType(silent ? MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_SYSTEM_USER : MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_OWN);
        message.sendStatus = SEND_STATUS_PENDING;
        return message;
    }

    public static MessageJson ofLoaderType() {
        return ofType(MessageViewHolderFactory.MESSAGE_TYPE_LOADER);
    }

    public static MessageJson from(PushMessage pushMessage) {
        MessageJson message = MessageJson.ofType(pushMessage.getTypeId());
        message.id = pushMessage.getId();
        message.message = pushMessage.getBody();
        message.timeStamp = new Date().getTime() / 1000;
        return message;
    }

    public static MessageJson from(Message message) {
        return new MessageJson(
                message.getId(),
                message.getTimeStamp(),
                message.getTitle(),
                message.getMessage(),
                message.getLocalUrl(),
                MediaJson.Companion.from(message.getMedia()),
                message.getTypeId(),
                AgentJson.Companion.from(message.getAgent()),
                message.getSendStatus(),
                actionListToActionJsonList(message.getActions()),
                messageListToMessageJsonList(message.getCarousel())
        );
    }

    private static List<MessageJson> messageListToMessageJsonList(List<Message> messageList) {
        List<MessageJson> messageJsonList = new ArrayList<>();
        for (Message carouselMessage : messageList) {
            messageJsonList.add(MessageJson.from(carouselMessage));
        }
        return messageJsonList;
    }

    private static List<Message> messageJsonListToMessageList(List<MessageJson> messageJsonList) {
        List<Message> messageList = new ArrayList<>();
        for (MessageJson carouselMessageJson : messageJsonList) {
            messageList.add(carouselMessageJson.toMessage());
        }
        return messageList;
    }

    private static List<ActionJson> actionListToActionJsonList(List<Action> actionList) {
        List<ActionJson> actionJsonList = new ArrayList<>();
        for (Action action : actionList) {
            actionJsonList.add(ActionJson.Companion.from(action));
        }
        return actionJsonList;
    }

    private static List<Action> actionJsonListToActionList(List<ActionJson> actionJsonList) {
        List<Action> actionList = new ArrayList<>();
        for (ActionJson actionJson : actionJsonList) {
            actionList.add(new Action(actionJson.getTitle(), actionJson.getPayload(), actionJson.getType()));
        }
        return actionList;
    }

    public static MessageJson withIdAndStatus(MessageJson sourceMessage, Integer id, int status) {
        return new MessageJson(sourceMessage.uuid, id, sourceMessage.timeStamp, sourceMessage.message, sourceMessage.localUrl, sourceMessage.media, sourceMessage.typeId, sourceMessage.agent, status);
    }

    public static MessageJson withMedia(MessageJson sourceMessage, MediaJson media) {
        return new MessageJson(sourceMessage.uuid, sourceMessage.id, sourceMessage.timeStamp, sourceMessage.message, null, media, sourceMessage.typeId, sourceMessage.agent, sourceMessage.sendStatus);
    }

    public static MessageJson withMessageAndDate(MessageJson sourceMessage, String message, Date date) {
        return new MessageJson(sourceMessage.uuid, sourceMessage.id, date.getTime() / 1000, message, sourceMessage.localUrl, sourceMessage.media, sourceMessage.typeId, sourceMessage.agent, sourceMessage.sendStatus);
    }

    /**
     * @return Unique uuid for this message.
     */
    public UUID getUuid() {
        return uuid;
    }

    @Nullable
    public Integer getId() {
        return id;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public String getResponseInfoType() {
        return responseInfoType;
    }

    @Nullable
    public List<ActionJson> getActions() {
        return actions;
    }

    @Nullable
    public List<MessageJson> getCarousel() {
        return carousel;
    }

    @Nullable
    public List<String> getQuickReplies() {
        return quickReplies;
    }

    @Nullable
    public Integer getTypeId() {
        return typeId;
    }

    @Nullable
    public AgentJson getAgent() {
        return agent;
    }

    @Nullable
    public Date getDate() {
        if (timeStamp == null) {
            return null;
        }
        return new Date(timeStamp * 1000);
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public Message toMessage() {
        return new Message(
                id,
                timeStamp,
                title,
                message,
                localUrl,
                media.toMedia(),
                typeId,
                agent.toAgent(),
                sendStatus,
                actionJsonListToActionList(actions),
                messageJsonListToMessageList(carousel)
        );
    }

    public boolean hasName() {
        return getAgent() != null && !agent.getName().trim().isEmpty();
    }

    public boolean hasTextContent() {
        return (title != null && !title.trim().isEmpty()) ||
                (message != null && !message.trim().isEmpty());
    }

    public boolean hasActionsContent() {
        return actions != null && !actions.isEmpty();
    }

    public boolean hasCarouselContent() {
        return carousel != null && !carousel.isEmpty();
    }

    public boolean isEqualVisually(MessageJson other) {
        if (uuid == other.uuid) {
            // Same message
            return CompareUtil.equals(message, other.message) &&
                    CompareUtil.equals(typeId, other.typeId) &&
                    CompareUtil.equals(agent, other.agent) &&
                    CompareUtil.equals(localUrl, other.localUrl) &&
                    CompareUtil.equals(image, other.image) &&
                    CompareUtil.equals(media, other.media) &&
                    CompareUtil.equals(timeStamp, other.timeStamp) &&
                    CompareUtil.equals(sendStatus, other.sendStatus) &&
                    CompareUtil.equals(actions, other.actions) &&
                    CompareUtil.equals(carousel, other.carousel) &&
                    CompareUtil.equals(quickReplies, other.quickReplies);
        } else {
            // It's another message
            return false;
        }
    }

    public void setResponseInfoType(@Nullable String responseInfoType) {
        this.responseInfoType = responseInfoType;
    }
}
