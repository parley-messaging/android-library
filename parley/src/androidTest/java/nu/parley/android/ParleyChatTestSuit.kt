package nu.parley.android

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import nu.parley.android.base.ParleyBaseViewTest
import nu.parley.android.data.model.Action
import nu.parley.android.data.model.Message
import nu.parley.android.data.model.MimeType
import nu.parley.android.util.mock.MockAction
import nu.parley.android.util.mock.MockAgent
import nu.parley.android.util.mock.MockMedia
import nu.parley.android.util.mock.MockMessage.agentCarousel
import nu.parley.android.util.mock.MockMessage.agentFull
import nu.parley.android.util.mock.MockMessage.agentFullImage
import nu.parley.android.util.mock.MockMessage.agentImage
import nu.parley.android.util.mock.MockMessage.agentMedia
import nu.parley.android.util.mock.MockMessage.agentMessageAndImage
import nu.parley.android.util.mock.MockMessage.agentMessageAndMedia
import nu.parley.android.util.mock.MockMessage.agentText
import nu.parley.android.util.mock.MockMessage.userImage
import nu.parley.android.util.mock.MockMessage.userMedia
import nu.parley.android.util.mock.MockMessage.userText
import nu.parley.android.util.mock.MockMessage.userTextAndImage
import nu.parley.android.util.mock.MockMessage.userTextAndMedia
import nu.parley.android.view.chat.MessageViewHolderFactory
import org.junit.AfterClass
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class ParleyChatTestSuit : ParleyBaseViewTest<View>() {

    companion object {
        private const val UrlParleyInvalid = "https://parley.jpg"
        private const val UrlParleyLogoColored =
            "https://www.tracebuzz.com/assets/images/parley-blog.jpg"
        private const val UrlParleyLogoLight =
            "https://media.licdn.com/dms/image/C560BAQGaitUb5D_v9Q/company-logo_200_200/0?e=2159024400&v=beta&t=zQCzNT4cnFEiEfzKkzBaBaGfK5rapGGXNKLFjZYFcH4&ext=.png"
        private const val UrlParleyImageTransparency =
            "https://images.prismic.io/endeavour-parley/48f62e1a-4450-469b-abb6-fa0e00acb68a_Chat.png?auto=compress,format&amp;h=500&ext=.png"
        private const val UrlParleyImageGif =
            "https://media0.giphy.com/media/kEKcOWl8RMLde/giphy.gif"
        private const val UrlParleyImagePerson =
            "https://images.prismic.io/endeavour-parley/05e079ec-2a7a-4069-9107-6518acb2879e_Scherm%C2%ADafbeelding+2022-12-16+om+11.14.43.png?auto=compress,format&ext=.png"
        private const val UrlParleyImageWeb =
            "https://www.tracebuzz.com/assets/images/parley_tab.png"
        private const val UrlParleyImageSocials =
            "https://images.prismic.io/endeavour-parley/72be8647-8b35-4b7e-ba6e-9e2c527d78f7_PARLEY_STILLS+-+SCENE_3_MESSAGING.jpg?auto=compress,format&h=500&ext=.png"

        @JvmStatic
        @AfterClass
        fun tearDown() {
            // Make a report doc
            val markdownText = StringBuilder(
                """
    
    Current | Updated
    -- | --
    
    """.trimIndent()
            )
            for (screenshot in screenshots) {
                markdownText.append("![Current](Current/")
                    .append(screenshot)
                    .append(".png) | ![Updated](Update/")
                    .append(screenshot)
                    .append(".png)\n")
            }
            Log.d("diff", markdownText.toString())
        }
    }

    override fun createView(activity: Activity): View {
        return LayoutInflater.from(activity).inflate(R.layout.item_message, null)
    }

    @Test
    fun userMessage_shortPending() {
        renderMessage(userText("Hello \uD83D\uDC4B", Message.SEND_STATUS_PENDING))
        makeScreenshot("UserMessage-ShortPending")
    }

    @Test
    fun userMessage_shortFailed() {
        renderMessage(userText("I have a question", Message.SEND_STATUS_FAILED))
        makeScreenshot("UserMessage-ShortFailed")
    }

    @Test
    fun userMessage_longSuccess() {
        renderMessage(
            userText(
                "Is it possible to change the styling? We want the chat to have the same styling as our app.\n\nAlso, does it support emoji like ✨✔️?",
                Message.SEND_STATUS_SUCCESS
            )
        )
        makeScreenshot("UserMessage-LongSuccess")
    }

    @Test
    fun userMessage_longSuccessWithMarkdown() {
        renderMessage(
            userText(
                "And does it support *Markdown* inside the **chat**? ***Checking it***",
                Message.SEND_STATUS_SUCCESS
            )
        )
        makeScreenshot("UserMessage-LongSuccessWithMarkdown")
    }

    //    @Test
    //    public void userMessage_imageLocal() {
    //        // TODO: Implement
    //        makeScreenshot("UserMessage-ImageLocal");
    //    }
    @Test
    fun userMessage_imageFailure() {
        renderMessage(
            userImage(
                UrlParleyInvalid,
                Message.SEND_STATUS_SUCCESS
            )
        )
        sleepForVisual(500)
        makeScreenshot("UserMessage-ImageFailure")
    }

    @Test
    fun userMessage_imageRemoteDark() {
        renderMessage(userImage(UrlParleyLogoColored, Message.SEND_STATUS_SUCCESS))
        sleepForVisual(500)
        makeScreenshot("UserMessage-ImageRemoteDark")
    }

    @Test
    fun userMessage_imageRemoteLight() {
        renderMessage(userImage(UrlParleyLogoLight, Message.SEND_STATUS_SUCCESS))
        sleepForVisual(500)
        makeScreenshot("UserMessage-ImageRemoteLight")
    }

    @Test
    fun userMessage_imageRemoteTransparency() {
        renderMessage(userImage(UrlParleyImageTransparency, Message.SEND_STATUS_SUCCESS))
        sleepForVisual(500)
        makeScreenshot("UserMessage-ImageRemoteTransparency")
    }

    @Test
    fun userMessage_imageRemoteGif() {
        renderMessage(userImage(UrlParleyImageGif, Message.SEND_STATUS_SUCCESS))
        sleepForVisual(500)
        makeScreenshot("UserMessage-ImageRemoteGif")
    }

    @Test
    fun userMessage_textAndImage() {
        renderMessage(
            userTextAndImage(
                "Look at this image",
                UrlParleyLogoColored,
                Message.SEND_STATUS_SUCCESS
            )
        )
        sleepForVisual(500)
        makeScreenshot("UserMessage-TextAndImage")
    }

    @Test
    fun userMessage_documentPending() {
        renderMessage(userImage("sample.pdf", Message.SEND_STATUS_SUCCESS))
        makeScreenshot("UserMessage-DocumentPending")
    }

    @Test
    fun userMessage_document() {
        val media = MockMedia.document("document.pdf", MimeType.ApplicationPdf)
        renderMessage(userMedia(media, Message.SEND_STATUS_SUCCESS))
        makeScreenshot("UserMessage-Document")
    }

    @Test
    fun userMessage_textAndDocument() {
        renderMessage(
            userTextAndMedia(
                "Look at this document",
                MockMedia.document("document.pdf", MimeType.ApplicationPdf),
                Message.SEND_STATUS_SUCCESS,
            )
        )
        makeScreenshot("UserMessage-TextAndDocument")
    }

    @Test
    fun agentMessage_shortWithoutName() {
        renderMessage(agentText(null, "We will respond shortly"))
        makeScreenshot("AgentMessage-ShortWithoutName")
    }

    @Test
    fun agentMessage_shortWithName() {
        renderMessage(agentText(MockAgent.Webuildapps, "Hello John"))
        makeScreenshot("AgentMessage-ShortWithName")
    }

    @Test
    fun agentMessage_shortWithTitle() {
        renderMessage(
            agentMessageAndMedia(
                MockAgent.Webuildapps,
                "Hello",
                "Welcome to *Parley* John",
                null
            )
        )
        makeScreenshot("AgentMessage-ShortWithTitle")
    }

    @Test
    fun agentMessage_long() {
        renderMessage(
            agentText(
                MockAgent.Webuildapps,
                "Yes, it is possible to **fully** change the styling. The *Parley* library provides a default style to get you started \uD83D\uDE80\n\nAs you already recognized, *Parley* supports *Markdown* in the chat \uD83D\uDC4D\n\nYou can also check out our [website](https://parley.nu)"
            )
        )
        makeScreenshot("AgentMessage-Long")
    }

    @Test
    fun agentMessage_imageFailure() {
        renderMessage(agentImage(MockAgent.Webuildapps, UrlParleyInvalid))
        sleepForVisual(500)
        makeScreenshot("AgentMessage-ImageFailure")
    }

    @Test
    fun agentMessage_imageRemoteDark() {
        renderMessage(agentImage(MockAgent.Webuildapps, UrlParleyLogoColored))
        sleepForVisual(500)
        makeScreenshot("AgentMessage-ImageRemoteDark")
    }

    @Test
    fun agentMessage_imageRemoteLight() {
        renderMessage(agentImage(MockAgent.Webuildapps, UrlParleyLogoLight))
        sleepForVisual(500)
        makeScreenshot("AgentMessage-ImageRemoteLight")
    }

    @Test
    fun agentMessage_imageRemoteTransparency() {
        renderMessage(agentImage(MockAgent.Webuildapps, UrlParleyImageTransparency))
        sleepForVisual(500)
        makeScreenshot("AgentMessage-ImageRemoteTransparency")
    }

    @Test
    fun agentMessage_imageRemoteGifWithoutName() {
        renderMessage(agentImage(null, UrlParleyImageGif))
        sleepForVisual(500)
        makeScreenshot("AgentMessage-ImageRemoteGifWithoutName")
    }

    @Test
    fun agentMessage_textAndImage() {
        renderMessage(
            agentMessageAndImage(
                MockAgent.Webuildapps,
                null,
                "This is an image of Parley",
                UrlParleyLogoColored
            )
        )
        sleepForVisual(500)
        makeScreenshot("AgentMessage-TextAndImage")
    }

    @Test
    fun agentMessage_document() {
        val media = MockMedia.document("document.pdf", MimeType.ApplicationPdf)
        renderMessage(agentMedia(MockAgent.Webuildapps, media))
        makeScreenshot("AgentMessage-Document")
    }

    @Test
    fun agentMessage_textAndDocument() {
        renderMessage(
            agentMessageAndMedia(
                MockAgent.Webuildapps,
                null,
                "Look at this document",
                MockMedia.document("document.pdf", MimeType.ApplicationPdf),
            )
        )
        makeScreenshot("AgentMessage-TextAndDocument")
    }

    @Test
    fun agentMessage_textAndDocumentAndActions() {
        val actions: MutableList<Action?> = ArrayList()
        actions.add(MockAction.create("Open app", "open-app://parley.nu"))
        actions.add(MockAction.create("Call us", "call://+31362022080"))
        renderMessage(
            agentFull(
                MockAgent.Webuildapps,
                null,
                "Here are some quick actions for more information about *Parley*",
                MockMedia.document("document.pdf", MimeType.ApplicationPdf),
                actions,
                null
            )
        )
        makeScreenshot("AgentMessage-TextAndDocumentAndActions")
    }

    @Test
    fun agentMessage_fullMessageWithTitle() {
        renderMessage(
            agentMessageAndImage(
                MockAgent.Webuildapps,
                "Welcome",
                "This is an image of Parley",
                UrlParleyImagePerson
            )
        )
        sleepForVisual(500)
        makeScreenshot("AgentMessage-FullMessageWithTitle")
    }

    @Test
    fun agentMessage_textWithActions() {
        val actions: MutableList<Action?> = ArrayList()
        actions.add(MockAction.create("Open app", "open-app://parley.nu"))
        actions.add(MockAction.create("Call us", "call://+31362022080"))
        renderMessage(
            agentFull(
                MockAgent.Webuildapps,
                null,
                "Here are some quick actions for more information about *Parley*",
                null,
                actions,
                null
            )
        )
        makeScreenshot("AgentMessage-TextWithActions")
    }

    @Test
    fun agentMessage_imageWithActions() {
        val actions: MutableList<Action?> = ArrayList()
        actions.add(
            MockAction.create(
                "Web documentation",
                "https://developers.parley.nu/docs/introduction"
            )
        )
        actions.add(
            MockAction.create(
                "Android documentation",
                "https://developers.parley.nu/docs/introduction-1"
            )
        )
        actions.add(
            MockAction.create(
                "iOS documentation",
                "https://developers.parley.nu/docs/introduction-2"
            )
        )
        renderMessage(
            agentFullImage(
                MockAgent.Webuildapps,
                null,
                null,
                UrlParleyImagePerson,
                actions,
                null
            )
        )
        sleepForVisual(500)
        makeScreenshot("AgentMessage-ImageWithActions")
    }

    @Test
    fun agentMessage_fullMessageWithActions() {
        val actions: MutableList<Action?> = ArrayList()
        actions.add(MockAction.create("Open app", "open-app://parley.nu"))
        actions.add(MockAction.create("Call us", "call://+31362022080"))
        actions.add(MockAction.create("Webuildapps", "https://webuildapps.com"))
        renderMessage(
            agentFullImage(
                MockAgent.Webuildapps,
                "Welcome",
                "Here are some quick actions for more information about *Parley*",
                UrlParleyLogoColored,
                actions,
                null
            )
        )
        sleepForVisual(500)
        makeScreenshot("AgentMessage-FullMessageWithActions")
    }

    @Test
    fun agentMessage_onlyActions() {
        val actions: MutableList<Action?> = ArrayList()
        actions.add(MockAction.create("Open app", "open-app://parley.nu"))
        actions.add(MockAction.create("Call us", "call://+31362022080"))
        actions.add(MockAction.create("Webuildapps", "https://webuildapps.com"))
        renderMessage(agentFull(null, null, null, null, actions, null))
        makeScreenshot("AgentMessage-OnlyActions")
    }

    @Test
    fun agentMessage_onlyActionsWithName() {
        val actions: MutableList<Action?> = ArrayList()
        actions.add(MockAction.create("Open app", "open-app://parley.nu"))
        actions.add(MockAction.create("Call us", "call://+31362022080"))
        actions.add(MockAction.create("Webuildapps", "https://webuildapps.com"))
        renderMessage(agentFull(MockAgent.Webuildapps, null, null, null, actions, null))
        makeScreenshot("AgentMessage-OnlyActionsWithName")
    }

    @Test
    fun agentMessage_messageWithCarouselFull() {
        val carousel: MutableList<Message?> = ArrayList()
        val actionsItem1: MutableList<Action?> = ArrayList()
        actionsItem1.add(
            MockAction.create(
                "Parley secure messaging",
                "https://www.parley.nu/en/secure-messaging"
            )
        )
        actionsItem1.add(
            MockAction.create(
                "Developer documentation",
                "https://developers.parley.nu"
            )
        )
        val actionsItem2: MutableList<Action?> = ArrayList()
        actionsItem2.add(MockAction.create("Parley", "https://parley.nu"))
        actionsItem2.add(MockAction.create("Webuildapps", "https://webuildapps.com"))
        actionsItem2.add(MockAction.create("Tracebuzz", "https://tracebuzz.com"))
        carousel.add(
            agentCarousel(
                "Parley - Redefining Customer Happiness",
                "Fast and secure messaging with *Parley*. The Android and iOS library are available which enables you to fully customise the chat.",
                UrlParleyLogoColored,
                actionsItem1
            )
        )
        carousel.add(
            agentCarousel(
                "More information",
                "Check out the following links",
                UrlParleyImageSocials,
                actionsItem2
            )
        )

        renderMessage(
            agentFull(
                MockAgent.Webuildapps,
                null,
                "Here are some quick actions for more information about *Parley*",
                null,
                null,
                carousel
            )
        )
        sleepForVisual(500)
        makeScreenshot("AgentMessage-MessageWithCarouselFull")
    }

    @Test
    fun agentMessage_messageWithCarouselSmall() {
        val actionsMessage: MutableList<Action?> = ArrayList()
        actionsMessage.add(MockAction.create("Home page", "https://www.parley.nu/"))

        val carousel: MutableList<Message?> = ArrayList()
        val actionsItem1: MutableList<Action?> = ArrayList()
        actionsItem1.add(
            MockAction.create(
                "Android SDK",
                "https://github.com/parley-messaging/android-library"
            )
        )
        actionsItem1.add(
            MockAction.create(
                "iOS SDK",
                "https://github.com/parley-messaging/ios-library"
            )
        )
        val actionsItem2: MutableList<Action?> = ArrayList()
        actionsItem2.add(
            MockAction.create(
                "Web documentation",
                "https://developers.parley.nu/docs/introduction"
            )
        )
        actionsItem2.add(
            MockAction.create(
                "Android documentation",
                "https://developers.parley.nu/docs/introduction-1"
            )
        )
        actionsItem2.add(
            MockAction.create(
                "iOS documentation",
                "https://developers.parley.nu/docs/introduction-2"
            )
        )
        carousel.add(
            agentCarousel(
                "Parley libraries",
                "Parley provides open source SDK's for the Web, Android and iOS to easily integrate it with any platform.\n\nThe chat is fully customisable.",
                null,
                actionsItem1
            )
        )
        carousel.add(agentCarousel(null, null, UrlParleyImageWeb, actionsItem2))

        renderMessage(
            agentFullImage(
                MockAgent.Webuildapps,
                null,
                "Here are some quick actions for more information about *Parley*",
                UrlParleyLogoColored,
                actionsMessage,
                carousel
            )
        )
        sleepForVisual(500)
        makeScreenshot("AgentMessage-MessageWithCarouselSmall")
    }

    @Test
    fun agentMessage_messageWithCarouselOnly() {
        val carousel: MutableList<Message?> = ArrayList()
        val actionsItem1: MutableList<Action?> = ArrayList()
        actionsItem1.add(
            MockAction.create(
                "Android SDK",
                "https://github.com/parley-messaging/android-library"
            )
        )
        actionsItem1.add(
            MockAction.create(
                "iOS SDK",
                "https://github.com/parley-messaging/ios-library"
            )
        )
        val actionsItem2: MutableList<Action?> = ArrayList()
        actionsItem2.add(
            MockAction.create(
                "Web documentation",
                "https://developers.parley.nu/docs/introduction"
            )
        )
        actionsItem2.add(
            MockAction.create(
                "Android documentation",
                "https://developers.parley.nu/docs/introduction-1"
            )
        )
        actionsItem2.add(
            MockAction.create(
                "iOS documentation",
                "https://developers.parley.nu/docs/introduction-2"
            )
        )
        carousel.add(
            agentCarousel(
                "Parley libraries",
                "Parley provides open source SDK's for the Web, Android and iOS to easily integrate it with any platform.\n\nThe chat is fully customisable.",
                null,
                actionsItem1
            )
        )
        carousel.add(agentCarousel(null, null, UrlParleyImageWeb, actionsItem2))

        renderMessage(agentFull(MockAgent.Webuildapps, null, null, null, null, carousel))
        sleepForVisual(500)
        makeScreenshot("AgentMessage-MessageWithCarouselOnly")
    }

    @Test
    fun agentMessage_messageWithCarouselImages() {
        val actions: MutableList<Action?> = ArrayList()
        actions.add(MockAction.create("Home page", "https://www.parley.nu/"))

        val carousel: MutableList<Message?> = ArrayList()
        carousel.add(agentCarousel(null, null, UrlParleyImageTransparency, null))
        carousel.add(agentCarousel(null, null, UrlParleyImageWeb, null))
        carousel.add(agentCarousel(null, null, UrlParleyImagePerson, null))
        carousel.add(agentCarousel(null, null, UrlParleyImageSocials, null))

        renderMessage(
            agentFullImage(
                MockAgent.Webuildapps,
                null,
                null,
                UrlParleyImagePerson,
                actions,
                carousel
            )
        )
        sleepForVisual(500)
        makeScreenshot("AgentMessage-MessageWithCarouselImages")
    }

    // Rendering order
    @Test
    fun userMessage_renderOrder_imageTextImage() {
        takeScreenshots = false
        userMessage_imageRemoteLight()
        userMessage_longSuccessWithMarkdown()
        userMessage_imageRemoteDark()
        takeScreenshots = true
        makeScreenshot("RenderOrder-ImageTextImage")
    }

    private fun renderMessage(message: Message) {
        getView { view: View? ->
            val viewHolder = MessageViewHolderFactory.getViewHolder(
                message.typeId!!, view, null
            )
            viewHolder.show(message)
        }
    }
}