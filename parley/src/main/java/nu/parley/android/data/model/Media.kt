package nu.parley.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.net.Connectivity
import java.io.File

data class Media(
    @SerializedName("id") private val id: String,
    @SerializedName("fileName") private val fileName: String?,
    @SerializedName("mimeType") private val mimeType: String,
) {
    companion object {

        fun fromFile(file: File): Media {
            val fileName = file.name
            val extension = file.extension
            val mimeType = MimeType.fromExtension(extension)
            return Media(fileName, fileName, mimeType.value)
        }
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
}
