package nu.parleynetwork.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.model.Agent
import nu.parley.android.data.net.response.ParleyPaging
import nu.parley.android.data.net.response.ParleyResponse

class ParleyResponseJson<T>(
    @SerializedName("data") val data: T,
    @SerializedName("agent") val agent: AgentJson?,
    @SerializedName("paging") val paging: ParleyPagingJson,
    @SerializedName("stickyMessage") val stickyMessage: String?,
    @SerializedName("welcomeMessage") val welcomeMessage: String?
) {

    fun toParleyResponse() = ParleyResponse<T>(data ?: this.data, agent?.toAgent(), paging.toParleyPaging(), stickyMessage, welcomeMessage)

    fun <X>toParleyResponse(data: X) = ParleyResponse<X>(data, agent?.toAgent(), paging.toParleyPaging(), stickyMessage, welcomeMessage)

    companion object {
        fun <T>from(parleyResponse: ParleyResponse<T>) = with(parleyResponse) {
            ParleyResponseJson(data, agent?.let { AgentJson.from(it) }, ParleyPagingJson.from(paging), stickyMessage, welcomeMessage)
        }
    }
}