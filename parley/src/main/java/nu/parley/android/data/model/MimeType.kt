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
    Unknown("*/*", listOf());

    fun isImage() = when (this) {
        ImageJpeg,
        ImagePng,
        ImageGif -> true

        ApplicationPdf,
        Unknown -> false
    }

    fun isDocument() = when (this) {
        ImageJpeg,
        ImagePng,
        ImageGif -> false

        ApplicationPdf -> true
        Unknown -> false
    }

    fun isFile() = when (this) {
        ImageJpeg,
        ImagePng,
        ImageGif -> false

        ApplicationPdf,
        Unknown -> true
    }

    fun getExtension() = extensions.first()

    companion object {
        fun fromUrl(url: String) = fromExtension(File(url).extension)
        fun fromValue(value: String) = entries.firstOrNull { it.value == value } ?: Unknown
        fun fromExtension(extension: String) =
            entries.firstOrNull { it.extensions.contains(extension) } ?: Unknown

        val images = entries.filter { it.isImage() }
        val documents = entries.filter { it.isDocument() }
    }
}
