package nu.parley.android.data.net.response;

import androidx.annotation.Nullable;

public enum ParleyNotificationResponseType {

    MediaInvalidType,
    MediaMissing,
    MediaTooLarge,
    MediaCouldNotSave,
    ;

    @Nullable
    public static ParleyNotificationResponseType from(@Nullable String type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case "invalid_media_type":
                return MediaInvalidType;
            case "missing_media":
                return MediaMissing;
            case "media_too_large":
                return MediaTooLarge;
            case "could_not_save_media":
                return MediaCouldNotSave;
            default:
                return null;
        }
    }
}