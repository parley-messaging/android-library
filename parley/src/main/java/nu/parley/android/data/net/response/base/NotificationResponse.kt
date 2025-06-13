package nu.parley.android.data.net.response.base

import com.google.gson.annotations.SerializedName

internal data class NotificationResponse(
    @SerializedName("type") val type: String,
    @SerializedName("message") val message: String,
)
