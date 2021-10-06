package nu.parley.android.data.model;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.model.GlideUrl;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import nu.parley.android.data.net.Connectivity;
import nu.parley.android.util.CompareUtil;
import nu.parley.android.view.chat.MessageViewHolderFactory;

/**
 * Message model for the Parley chat. Every item shown in the chat is a message.
 */
public final class Message {

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
     * <b>Deprecated</b>: Use `media` instead
     */
    @SerializedName("image")
    @Nullable
    @Deprecated
    private String imageUrl;

    @SerializedName("media")
    @Nullable
    private Media media;

    @SerializedName("buttons")
    @Nullable
    private List<Action> actions;

    @SerializedName("carousel")
    @Nullable
    private List<Message> carousel;

    @SerializedName("quickReplies")
    @Nullable
    private List<String> quickReplies;

    @SerializedName("typeId")
    @Nullable
    private Integer typeId;

    @SerializedName("agent")
    @Nullable
    private Agent agent;

    @SerializedName("send_status")
    private int sendStatus = SEND_STATUS_SUCCESS;

    private Message() {
        // Hide constructor
    }

    private Message(UUID uuid, @Nullable Integer id, long timeStamp, @Nullable String message, @Nullable String imageUrl, @Nullable Media media, int typeId, @Nullable Agent agent, int sendStatus) {
        this.uuid = uuid;
        this.id = id;
        this.timeStamp = timeStamp;
        this.message = message;
        this.imageUrl = imageUrl;
        this.media = media;
        this.typeId = typeId;
        this.agent = agent;
        this.sendStatus = sendStatus;
    }

    Message(@Nullable Integer id, Long timeStamp, @Nullable String title, @Nullable String message, @Nullable String imageUrl, Integer typeId, @Nullable Agent agent, int sendStatus, @Nullable List<Action> actions, @Nullable List<Message> carousel) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
        this.typeId = typeId;
        this.agent = agent;
        this.sendStatus = sendStatus;
        this.actions = actions;
        this.carousel = carousel;
    }

    private static Message ofType(int typeId) {
        Message message = new Message();
        message.id = null;
        message.typeId = typeId;
        message.timeStamp = new Date().getTime() / 1000;
        return message;
    }

    public static Message ofTypeInfo(String text) {
        Message message = ofType(MessageViewHolderFactory.MESSAGE_TYPE_INFO);
        message.message = text;
        return message;
    }

    public static Message ofTypeDate(Date date) {
        Message message = ofType(MessageViewHolderFactory.MESSAGE_TYPE_DATE);
        message.message = date.toString();
        message.timeStamp = date.getTime() / 1000;
        return message;
    }

    public static Message ofTypeAgentTyping() {
        return ofType(MessageViewHolderFactory.MESSAGE_TYPE_AGENT_TYPING);
    }

    public static Message ofTypeOwnMessage(String text) {
        return ofTypeOwnMessage(text, false);
    }

    public static Message ofTypeOwnMessage(String text, boolean silent) {
        Message message = Message.ofTypeOwnMessage(silent);
        message.message = text;
        return message;
    }

    public static Message ofTypeOwnImage(String imageUrl) {
        Message message = Message.ofTypeOwnMessage(false);
        message.imageUrl = imageUrl;
        return message;
    }

    private static Message ofTypeOwnMessage(boolean silent) {
        Message message = ofType(silent ? MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_SYSTEM_USER : MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_OWN);
        message.sendStatus = SEND_STATUS_PENDING;
        return message;
    }

    public static Message ofLoaderType() {
        return ofType(MessageViewHolderFactory.MESSAGE_TYPE_LOADER);
    }

    public static Message from(PushMessage pushMessage) {
        Message message = Message.ofType(pushMessage.getTypeId());
        message.id = pushMessage.getId();
        message.message = pushMessage.getBody();
        message.timeStamp = new Date().getTime() / 1000;
        return message;
    }

    public static Message withIdAndStatus(Message sourceMessage, Integer id, int status) {
        return new Message(sourceMessage.uuid, id, sourceMessage.timeStamp, sourceMessage.message, sourceMessage.imageUrl, sourceMessage.media, sourceMessage.typeId, sourceMessage.agent, status);
    }

    public static Message withMedia(Message sourceMessage, String mediaId) {
        return new Message(sourceMessage.uuid, sourceMessage.id, sourceMessage.timeStamp, sourceMessage.message, null, new Media(mediaId), sourceMessage.typeId, sourceMessage.agent, sourceMessage.sendStatus);
    }

    public static Message withMessageAndDate(Message sourceMessage, String message, Date date) {
        return new Message(sourceMessage.uuid, sourceMessage.id, date.getTime() / 1000, message, sourceMessage.imageUrl, sourceMessage.media, sourceMessage.typeId, sourceMessage.agent, sourceMessage.sendStatus);
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
    public List<Action> getActions() {
        return actions;
    }

    @Nullable
    public List<Message> getCarousel() {
        return carousel;
    }

    @Nullable
    public List<String> getQuickReplies() {
        return quickReplies;
    }

    public int getTypeId() {
        return typeId;
    }

    @Nullable
    public Agent getAgent() {
        return agent;
    }

    public String getLegacyImageUrl() {
        return imageUrl;
    }

    @Nullable
    private GlideUrl getImageUrl() {
        if (imageUrl == null) {
            return null;
        }
        if (id == null) {
            return null;
        } else {
            return Connectivity.toGlideUrl(id);
        }
    }

    @Nullable
    public Media getMedia() {
        return media;
    }

    /**
     * Helper method to get the image as either the GlideUrl or String.
     *
     * @return The image url as GlideUrl if possible, otherwise as String. `null` if it has no image.
     */
    @Nullable
    public Object getImage() {
        if (media != null && media.getId() != null) {
            String mediaId = media.getIdForUrl();
            return Connectivity.toGlideUrlMedia(mediaId);
        }
        if (getLegacyImageUrl() != null) {
            return getLegacyImageUrl();
        }
        if (getImageUrl() != null) {
            return getImageUrl();
        }

        return null;
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

    /**
     * Determine whether this message consists of only an image. This indicates that it has no
     * additional data, such as the actions data.
     *
     * @return `true` if the message only consists of an image, `false` otherwise.
     */
    public boolean isImageOnly() {
        return isImageContentOnly() &&
                (actions == null || actions.isEmpty());
    }

    /**
     * Determine whether this message consists of only an image as content. However, the message
     * might still have actions or other content attached.
     *
     * @return `true` if the content of the message only consists of an image, `false` otherwise.
     */
    public boolean isImageContentOnly() {
        return hasImageContent() &&
                (title == null || title.trim().isEmpty()) &&
                (message == null || message.trim().isEmpty());
    }

    public boolean hasImageContent() {
        return getLegacyImageUrl() != null || media != null;
    }

    public boolean hasTextContent() {
        return (title != null && !title.trim().isEmpty()) ||
                (message != null && !message.trim().isEmpty());
    }

    public boolean hasOtherContent() {
        return (actions != null && !actions.isEmpty()) ||
                (carousel != null && !carousel.isEmpty());
    }

    public boolean hasContent() {
        return hasTextContent() ||
                hasImageContent() ||
                hasOtherContent();
    }

    public boolean isEqualVisually(Message other) {
        if (uuid == other.uuid) {
            // Same message
            return CompareUtil.equals(message, other.message) &&
                    CompareUtil.equals(typeId, other.typeId) &&
                    CompareUtil.equals(agent, other.agent) &&
                    CompareUtil.equals(imageUrl, other.imageUrl) &&
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
}
