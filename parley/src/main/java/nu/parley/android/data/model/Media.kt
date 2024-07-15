package nu.parley.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.net.Connectivity

data class Media(
    @SerializedName("id") private val id: String,
    @SerializedName("fileName") private val fileName: String?,
    @SerializedName("mimeType") private val mimeType: String,
) {
    companion object {

        fun fromUrl(url: String): Media {
            val fileName = url.split("/").last()
            val mimeType = when (fileName.split(".").last()) {
                "gif" -> MimeType.ImageGif
                "png" -> MimeType.ImagePng
                "jpeg", "jpg" -> MimeType.ImageJpeg
                "pdf" -> MimeType.ApplicationPdf
                else -> MimeType.Unknown
            }
            return Media(fileName, fileName, mimeType.key)
        }
    }

    fun getFileName(): String {
        return fileName ?: id.split("/").last()
    }

    fun getMimeType() = MimeType.from(mimeType)

    fun getIdForUrl(): String {
        return id
            .split("/")
            .drop(2)
            .joinToString(separator = "/")
    }

    fun getUrl(): String = Connectivity.toMediaUrl(getIdForUrl())
}
