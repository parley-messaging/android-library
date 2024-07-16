package nu.parley.android

import nu.parley.android.data.model.Message
import nu.parley.android.data.model.MimeType
import nu.parley.android.util.mock.MockMedia
import nu.parley.android.util.mock.MockMessage
import org.junit.Assert
import org.junit.Test

class MessageUnitTest {
    companion object {
        private const val UrlParleyLogoColored =
            "https://www.tracebuzz.com/assets/images/parley-blog.jpg"
    }

    /**
     * Versions < 1.6 have their urls within the message instead of the media object.
     */
    @Test
    fun messageUrlLegacy() {
        val message = MockMessage.userImage(UrlParleyLogoColored, Message.SEND_STATUS_SUCCESS)
        Assert.assertEquals(UrlParleyLogoColored, message.getImageUrl())
    }

    @Test
    fun messageUrl() {
        val media = MockMedia.image("Image", MimeType.ImageJpeg)
        val message = MockMessage.userMedia(media, Message.SEND_STATUS_SUCCESS)
        val network = Parley.getInstance().network
        val expected = "${network.baseUrl}media/${media.getIdForUrl()}"
        Assert.assertEquals(expected, message.getImageUrl())
    }
}