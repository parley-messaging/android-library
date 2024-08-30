package nu.parleynetwork.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.model.Action
import nu.parley.android.data.net.response.ParleyResponsePostMessage

data class ParleyResponsePostMessageJson(
    @SerializedName("messageId") val messageId: String? = null
) {

    fun toParleyResponsePostMessage() = ParleyResponsePostMessage(messageId)

    companion object {
        fun from(parleyResponsePostMessage: ParleyResponsePostMessage) =
            ParleyResponsePostMessageJson(parleyResponsePostMessage.messageId?.toString())
    }
}