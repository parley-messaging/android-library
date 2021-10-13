package nu.parley.android.data.net.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public final class ParleyResponsePostMessage {

    @SerializedName("messageId")
    @Nullable
    private String messageId;

    public ParleyResponsePostMessage(@Nullable String messageId) {
        this.messageId = messageId;
    }

    @Nullable
    public Integer getMessageId() {
        if (messageId == null) {
            return null;
        } else {
            return Integer.parseInt(messageId);
        }
    }
}
