package nu.parley.android.data.net.response.base

import com.google.gson.annotations.SerializedName

internal data class DataResponse<T>(
    @SerializedName("data") val data: T,
    @SerializedName("notifications") val notifications: List<NotificationResponse>,
    @SerializedName("status") val status: String,
    @SerializedName("metadata") val meta: MetaResponse,
)
