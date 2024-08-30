package nu.parleynetwork.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.model.Agent
import nu.parley.android.data.net.response.ParleyPaging

class ParleyPagingJson(
    @SerializedName("before") private var before: String?,
    @SerializedName("after") private val after: String?
) {
    fun toParleyPaging() = ParleyPaging(before, after)

    companion object {
        fun from(parleyPaging: ParleyPaging) = with(parleyPaging) {
            ParleyPagingJson(before, after)
        }
    }
}