package nu.parley.android.util.mock

import nu.parley.android.data.model.Media
import nu.parley.android.data.model.MimeType

internal object MockMedia {
    fun image(fileName: String, mimeType: MimeType): Media {
        return Media(Mock.uuid(), fileName, mimeType.key)
    }

    fun document(fileName: String, mimeType: MimeType): Media {
        return Media(Mock.uuid(), fileName, mimeType.key)
    }
}
