package nu.parley.android.data.net.response.base

import com.google.gson.annotations.SerializedName

internal data class CollectionResponse<T>(
    @SerializedName("data") val data: List<T>,
    @SerializedName("notifications") val notifications: List<NotificationResponse>,
    @SerializedName("status") val status: String,
    @SerializedName("metadata") val meta: MetaResponse,
)
