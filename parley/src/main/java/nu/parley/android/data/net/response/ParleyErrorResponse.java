package nu.parley.android.data.net.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class ParleyErrorResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("notifications")
    private List<NotificationResponse> notifications;

    @SerializedName("metadata")
    private MetadataResponse metadata;

    @Nullable
    public String getMessage() {
        if (notifications.isEmpty()) {
            return null;
        }
        NotificationResponse first = notifications.get(0);
        return first.message;
    }

    private static final class NotificationResponse {

        @SerializedName("type")
        private String type;

        @SerializedName("message")
        private String message;
    }

    private static final class MetadataResponse {

        @SerializedName("method")
        private String method;

        @SerializedName("duration")
        private Double duration;
    }
}

