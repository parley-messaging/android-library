package nu.parley.android.data.net.response.message

import com.google.gson.annotations.SerializedName

internal data class AgentResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("isTyping") val typingSeconds: Long,
)
