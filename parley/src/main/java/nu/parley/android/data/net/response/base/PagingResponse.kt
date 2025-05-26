package nu.parley.android.data.net.response.base

import com.google.gson.annotations.SerializedName

internal data class PagingResponse(
    @SerializedName("before") val before: String,
    @SerializedName("after") val after: String,
)
