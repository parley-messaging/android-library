package nu.parley.android.data.net.response.base

import com.google.gson.annotations.SerializedName

internal class ErrorResponse(
    @SerializedName("notifications") val notifications: List<NotificationResponse>,
    @SerializedName("status") val status: String,
    @SerializedName("metadata") val meta: MetaResponse,
) {

    fun getMessage(): String? {
        return notifications.firstOrNull()?.message
    }
}
