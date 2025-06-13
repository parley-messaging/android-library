package nu.parley.android.data.net.response.message

import com.google.gson.annotations.SerializedName

internal data class CreateMediaResponse(
    @SerializedName("media") val id: String,
)
