package nu.parley.android.data.net.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class ParleyErrorResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("notifications")
    private List<ParleyNotificationResponse> notifications;

    @SerializedName("metadata")
    private MetadataResponse metadata;

    @Nullable
    private ParleyNotificationResponse getNotification() {
        // NOTE: Remove notifications being nullable -- https://github.com/parley-messaging/android-library/pull/40
        if (notifications == null || notifications.isEmpty()) {
            return null;
        }
        return notifications.get(0);
    }

    @Nullable
    public String getMessage() {
        ParleyNotificationResponse notification = getNotification();
        if (notification == null) {
            return null;
        }
        return notification.getMessage();
    }

    @Nullable
    public ParleyNotificationResponseType getType() {
        ParleyNotificationResponse notification = getNotification();
        if (notification == null) {
            return null;
        }
        return notification.getType();
    }


    private static final class MetadataResponse {

        @SerializedName("method")
        private String method;

        @SerializedName("duration")
        private Double duration;
    }
}

