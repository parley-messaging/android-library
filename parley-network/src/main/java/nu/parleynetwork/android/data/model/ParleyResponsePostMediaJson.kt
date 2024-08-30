package nu.parleynetwork.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.net.response.ParleyResponsePostMedia
import nu.parley.android.data.net.response.ParleyResponsePostMessage

data class ParleyResponsePostMediaJson(
    @SerializedName("media") val mediaId: String? = null
) {

    fun toParleyResponsePostMedia() = ParleyResponsePostMedia(mediaId)

    companion object {
        fun from(parleyResponsePostMedia: ParleyResponsePostMedia) =
            ParleyResponsePostMediaJson(parleyResponsePostMedia.mediaId)
    }
}