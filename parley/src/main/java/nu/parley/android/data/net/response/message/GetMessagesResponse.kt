package nu.parley.android.data.net.response.message

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.model.Message
import nu.parley.android.data.net.response.base.MetaResponse
import nu.parley.android.data.net.response.base.NotificationResponse
import nu.parley.android.data.net.response.base.PagingResponse

internal data class GetMessagesResponse(
    @SerializedName("data") val data: List<Message>,
    @SerializedName("notifications") val notifications: List<NotificationResponse>,
    @SerializedName("status") val status: String,
    @SerializedName("metadata") val meta: MetaResponse,
    @SerializedName("paging") val paging: PagingResponse,

    @SerializedName("agent") val agent: AgentResponse,
    @SerializedName("stickyMessage") val stickyMessage: String,
    @SerializedName("welcomeMessage") val welcomeMessage: String,
)
