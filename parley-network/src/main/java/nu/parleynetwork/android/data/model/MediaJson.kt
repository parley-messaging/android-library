package nu.parleynetwork.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.model.Media
import nu.parley.android.data.model.MimeType
import nu.parley.android.data.net.Connectivity
import java.io.File

data class MediaJson(
    @SerializedName("id") private val id: String,
    /* `filename` since clientApi 1.8 */
    @SerializedName("filename") private val fileName: String?,
    @SerializedName("mimeType") private val mimeType: String,
) {
    companion object {

        fun fromFile(file: File): MediaJson {
            val fileName = file.name
            val extension = file.extension
            val mimeType = MimeType.fromExtension(extension)
            return MediaJson(fileName, fileName, mimeType.value)
        }

        fun from(media: Media) = with(media) { MediaJson(id, getRawFileName(), getRawMimeType()) }
    }

    fun getFileName(): String {
        return fileName ?: id.split("/").last()
    }

    fun getMimeType() = MimeType.fromValue(mimeType)

    fun getIdForUrl(): String {
        return id
            .split("/")
            .drop(2)
            .joinToString(separator = "/")
    }

    fun getUrl(): String = Connectivity.toMediaUrl(getIdForUrl())

    fun toMedia() = Media(id, fileName, mimeType)
}
