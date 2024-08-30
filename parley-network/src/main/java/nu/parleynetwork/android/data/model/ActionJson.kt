package nu.parleynetwork.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.model.ButtonType

data class ActionJson(
    @SerializedName("title") val title: String,
    @SerializedName("payload") val payload: String,
    @SerializedName("type") val type: ButtonType? = null
)