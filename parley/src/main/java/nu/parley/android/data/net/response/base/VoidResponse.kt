package nu.parley.android.data.net.response.base

import com.google.gson.annotations.SerializedName

internal data class VoidResponse(
    @SerializedName("notifications") val notifications: List<NotificationResponse>,
    @SerializedName("status") val status: String,
    @SerializedName("metadata") val meta: MetaResponse,
)
