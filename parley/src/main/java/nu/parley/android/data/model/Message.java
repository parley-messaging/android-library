package nu.parley.android.data.model;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.model.GlideUrl;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
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
    private long timeStamp;

    @SerializedName("title")
    @Nullable
    private String title;

    @SerializedName("message")
    @Nullable
    private String message;

    @SerializedName("image")
    @Nullable
    private String imageUrl;

    @SerializedName("typeId")
    private int typeId;

    @SerializedName("agent")
    @Nullable
    private Agent agent;

    @SerializedName("send_status")
    private int sendStatus = SEND_STATUS_SUCCESS;

    private Message() {
        // Hide constructor
    }

    private Message(UUID uuid, @Nullable Integer id, long timeStamp, @Nullable String message, @Nullable String imageUrl, int typeId, @Nullable Agent agent, int sendStatus) {
        this.uuid = uuid;
        this.id = id;
        this.timeStamp = timeStamp;
        this.message = message;
        this.imageUrl = imageUrl;
        this.typeId = typeId;
        this.agent = agent;
        this.sendStatus = sendStatus;
    }

    Message(@Nullable Integer id, long timeStamp, @Nullable String message, @Nullable String imageUrl, int typeId, @Nullable Agent agent, int sendStatus) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.message = message;
        this.imageUrl = imageUrl;
        this.typeId = typeId;
        this.agent = agent;
        this.sendStatus = sendStatus;
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
        Message message = Message.ofTypeOwnMessage();
        message.message = text;
        return message;
    }

    public static Message ofTypeOwnImage(String imageUrl) {
        Message message = Message.ofTypeOwnMessage();
        message.imageUrl = imageUrl;
        return message;
    }

    private static Message ofTypeOwnMessage() {
        Message message = ofType(MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_OWN);
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
        return new Message(sourceMessage.uuid, id, sourceMessage.timeStamp, sourceMessage.message, sourceMessage.imageUrl, sourceMessage.typeId, sourceMessage.agent, status);
    }

    public static Message withMessageAndDate(Message sourceMessage, String message, Date date) {
        return new Message(sourceMessage.uuid, sourceMessage.id, date.getTime() / 1000, message, sourceMessage.imageUrl, sourceMessage.typeId, sourceMessage.agent, sourceMessage.sendStatus);
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

    public int getTypeId() {
        return typeId;
    }

    @Nullable
    public Agent getAgent() {
        return agent;
    }

    private String getImageUrlString() {
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

    /**
     * Helper method to get the image as either the GlideUrl or String.
     *
     * @return The image url as GlideUrl if possible, otherwise as String. `null` if it has no image.
     */
    @Nullable
    public Object getImage() {
        if (getImageUrlString() != null) {
            return getImageUrlString();
        }
        if (getImageUrl() != null) {
            return getImageUrl();
        }

        return null;
    }

    public Date getDate() {
        return new Date(timeStamp * 1000);
    }

    public int getSendStatus() {
        return sendStatus;
    }

    /**
     * Determine whether this message consists of only an image as body. However, the message might still have
     * buttons or other content attached.
     *
     * @return `true` if it only consists of an image, `false` otherwise.
     */
    public boolean isImageOnly() {
        return imageUrl != null &&
                (title == null || title.trim().isEmpty()) &&
                (message == null || message.trim().isEmpty());
    }

    public boolean isEqualVisually(Message other) {
        if (uuid == other.uuid) {
            // Same message
            return CompareUtil.equals(message, other.message) &&
                    CompareUtil.equals(typeId, other.typeId) &&
                    CompareUtil.equals(agent, other.agent) &&
                    CompareUtil.equals(imageUrl, other.imageUrl) &&
                    CompareUtil.equals(timeStamp, other.timeStamp) &&
                    CompareUtil.equals(sendStatus, other.sendStatus);
        } else {
            // It's another message
            return false;
        }
    }
}
