package nu.parley.android.data.net.response.message

import com.google.gson.annotations.SerializedName

internal data class GetUnseenResponse(
    @SerializedName("messageIds") val messageIds: List<Int>,
    @SerializedName("count") val count: Int,
)
