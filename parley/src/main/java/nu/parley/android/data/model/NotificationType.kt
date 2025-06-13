package nu.parley.android.data.model

internal enum class NotificationType(
    val key: String,
) {
    MediaInvalidType("invalid_media_type"),
    MediaMissing("missing_media"),
    MediaTooLarge("media_too_large"),
    MediaCouldNotSave("could_not_save_media"),
    ;

    companion object {
        fun from(key: String?) = entries.firstOrNull { it.key == key }
    }
}