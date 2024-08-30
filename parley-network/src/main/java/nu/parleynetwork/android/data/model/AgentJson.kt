package nu.parleynetwork.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.model.Agent

class AgentJson(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("isTyping") val isTyping: Long = 0
) {
    fun toAgent() = Agent(id, name, isTyping, avatar)

    companion object {
        fun from(agent: Agent) = with(agent) { AgentJson(id, name, avatar, isTyping.time / 1000) }
    }
}