package nu.parley.android.data.net.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public final class ParleyNotificationResponse {

    @SerializedName("type")
    private String type;

    @SerializedName("message")
    private String message;

    @Nullable
    ParleyNotificationResponseType getType() {
        return ParleyNotificationResponseType.from(type);
    }

    String getMessage() {
        return message;
    }
}
