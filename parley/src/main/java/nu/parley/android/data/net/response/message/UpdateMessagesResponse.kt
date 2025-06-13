package nu.parley.android.data.net.response.message

import com.google.gson.annotations.SerializedName

internal data class UpdateMessagesResponse(
    @SerializedName("messageIds") val messageIds: List<Int>,
)
