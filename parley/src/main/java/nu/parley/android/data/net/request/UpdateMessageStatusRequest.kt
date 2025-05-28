package nu.parley.android.data.net.request

import com.google.gson.annotations.SerializedName

data class UpdateMessageStatusRequest(
    @SerializedName("messageIds") val messageIds: Set<Int>,
)