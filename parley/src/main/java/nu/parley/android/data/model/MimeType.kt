package nu.parley.android.data.model

import java.io.File

enum class MimeType(
    val value: String,
    val extensions: List<String>,
) {
    ImageJpeg("image/jpeg", listOf("jpg", "jpeg")),
    ImagePng("image/png", listOf("png")),
    ImageGif("image/gif", listOf("gif")),
    ApplicationPdf("application/pdf", listOf("pdf")),
    Other("*/*", listOf());

    fun isImage() = when (this) {
        ImageJpeg,
        ImagePng,
        ImageGif -> true

        ApplicationPdf,
        Other -> false
    }

    fun isDocument() = when (this) {
        ImageJpeg,
        ImagePng,
        ImageGif -> false

        ApplicationPdf -> true
        Other -> false
    }

    fun isFile() = when (this) {
        ImageJpeg,
        ImagePng,
        ImageGif -> false

        ApplicationPdf,
        Other -> true
    }

    fun getExtension() = extensions.first()

    companion object {
        fun fromUrl(url: String) = fromExtension(File(url).extension)
        fun fromValue(value: String) = entries.firstOrNull { it.value == value } ?: Other
        fun fromExtension(extension: String) =
            entries.firstOrNull { it.extensions.contains(extension) } ?: Other

        val images = entries.filter { it.isImage() }
        val documents = entries.filter { it.isDocument() }
    }
}
