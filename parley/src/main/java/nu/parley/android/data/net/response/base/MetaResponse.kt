package nu.parley.android.data.net.response.base

import com.google.gson.annotations.SerializedName

internal data class MetaResponse(
    @SerializedName("values") val values: Map<String, String>,
    @SerializedName("method") val method: String,
    @SerializedName("duration") val duration: Float,
)
