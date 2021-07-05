package nu.parley.android;


import android.util.Log;
import android.widget.LinearLayout;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.novoda.espresso.ViewTestRule;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import nu.parley.android.data.model.Action;
import nu.parley.android.data.model.Message;
import nu.parley.android.data.model.MockAction;
import nu.parley.android.data.model.MockAgent;
import nu.parley.android.data.model.MockMessage;
import nu.parley.android.view.chat.MessageViewHolderFactory;
import nu.parley.android.view.chat.holder.ParleyBaseViewHolder;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ParleyChatTestSuit extends ParleyScreenBaseTest {

    private static final String URL_PARLEY_INVALID = "https://parley";
    private static final String URL_PARLEY_LOGO_COLORED = "https://www.tracebuzz.com/assets/images/parley-blog.jpg";
    private static final String URL_PARLEY_LOGO_LIGHT = "https://media.licdn.com/dms/image/C560BAQGaitUb5D_v9Q/company-logo_200_200/0?e=2159024400&v=beta&t=zQCzNT4cnFEiEfzKkzBaBaGfK5rapGGXNKLFjZYFcH4";
    private static final String URL_PARLEY_IMAGE_TRANSPARENCY = "https://www.parley.nu/images/tab2.png";
    private static final String URL_PARLEY_IMAGE_GIF = "https://media0.giphy.com/media/kEKcOWl8RMLde/giphy.gif";
    private static final String URL_PARLEY_IMAGE_PLATFORMS = "https://parley.nu/images/tab6.png";
    private static final String URL_PARLEY_IMAGE_WEB = "https://www.parley.nu/images/tab1_mobile.png";
    private static final String URL_PARLEY_IMAGE_PLATFORMS_TB = "http://www.socialmediatoolvergelijken.nl/tools/tracebuzz/img/tracebuzz_1.png";

    @Rule
    public ViewTestRule<LinearLayout> rule = new ViewTestRule<>(R.layout.item_message);

    @Test
    public void userMessage_shortPending() {
        renderMessage(MockMessage.textOfUser("Hello \uD83D\uDC4B", Message.SEND_STATUS_PENDING));
        makeScreenshot("UserMessage-ShortPending");
    }

    @Test
    public void userMessage_shortFailed() {
        renderMessage(MockMessage.textOfUser("I have a question", Message.SEND_STATUS_FAILED));
        makeScreenshot("UserMessage-ShortFailed");
    }

    @Test
    public void userMessage_longSuccess() {
        renderMessage(MockMessage.textOfUser("Is it possible to change the styling? We want the chat to have the same styling as our app.\n\nAlso, does it support emoji like ✨✔️?", Message.SEND_STATUS_SUCCESS));
        makeScreenshot("UserMessage-LongSuccess");
    }

    @Test
    public void userMessage_longSuccessWithMarkdown() {
        renderMessage(MockMessage.textOfUser("And does it support *Markdown* inside the **chat**? ***Checking it***", Message.SEND_STATUS_SUCCESS));
        makeScreenshot("UserMessage-LongSuccessWithMarkdown");
    }

//    @Test
//    public void userMessage_imageLocal() {
//        // TODO: Implement
//        makeScreenshot("UserMessage-ImageLocal");
//    }

    @Test
    public void userMessage_imageFailure() {
        renderMessage(MockMessage.imageOfUser(URL_PARLEY_INVALID, Message.SEND_STATUS_SUCCESS));
        makeScreenshot("UserMessage-ImageFailure");
    }

    @Test
    public void userMessage_imageRemoteDark() {
        renderMessage(MockMessage.imageOfUser(URL_PARLEY_LOGO_COLORED, Message.SEND_STATUS_SUCCESS));
        makeScreenshot("UserMessage-ImageRemoteDark");
    }

    @Test
    public void userMessage_imageRemoteLight() {
        renderMessage(MockMessage.imageOfUser(URL_PARLEY_LOGO_LIGHT, Message.SEND_STATUS_SUCCESS));
        makeScreenshot("UserMessage-ImageRemoteLight");
    }

    @Test
    public void userMessage_imageRemoteTransparency() {
        renderMessage(MockMessage.imageOfUser(URL_PARLEY_IMAGE_TRANSPARENCY, Message.SEND_STATUS_SUCCESS));
        makeScreenshot("UserMessage-ImageRemoteTransparency");
    }

    @Test
    public void userMessage_imageRemoteGif() {
        renderMessage(MockMessage.imageOfUser(URL_PARLEY_IMAGE_GIF, Message.SEND_STATUS_SUCCESS));
        makeScreenshot("UserMessage-ImageRemoteGif");
    }

    @Test
    public void userMessage_textAndImage() {
        renderMessage(MockMessage.messageOfUser("Look at this image", URL_PARLEY_LOGO_COLORED, Message.SEND_STATUS_SUCCESS));
        makeScreenshot("UserMessage-TextAndImage");
    }

    @Test
    public void agentMessage_shortWithoutName() {
        renderMessage(MockMessage.textOfAgent(null, "We will respond shortly"));
        makeScreenshot("AgentMessage-ShortWithoutName");
    }

    @Test
    public void agentMessage_shortWithName() {
        renderMessage(MockMessage.textOfAgent(MockAgent.Webuildapps, "Hello John"));
        makeScreenshot("AgentMessage-ShortWithName");
    }

    @Test
    public void agentMessage_shortWithTitle() {
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, "Hello", "Welcome to *Parley* John", null));
        makeScreenshot("AgentMessage-ShortWithTitle");
    }

    @Test
    public void agentMessage_long() {
        renderMessage(MockMessage.textOfAgent(MockAgent.Webuildapps, "Yes, it is possible to **fully** change the styling. The *Parley* library provides a default style to get you started \uD83D\uDE80\n\nAs you already recognized, *Parley* supports *Markdown* in the chat \uD83D\uDC4D\n\nYou can also check out our [website](https://parley.nu)"));
        makeScreenshot("AgentMessage-Long");
    }

    @Test
    public void agentMessage_imageFailure() {
        renderMessage(MockMessage.imageOfAgent(MockAgent.Webuildapps, URL_PARLEY_INVALID));
        makeScreenshot("AgentMessage-ImageFailure");
    }

    @Test
    public void agentMessage_imageRemoteDark() {
        renderMessage(MockMessage.imageOfAgent(MockAgent.Webuildapps, URL_PARLEY_LOGO_COLORED));
        makeScreenshot("AgentMessage-ImageRemoteDark");
    }

    @Test
    public void agentMessage_imageRemoteLight() {
        renderMessage(MockMessage.imageOfAgent(MockAgent.Webuildapps, URL_PARLEY_LOGO_LIGHT));
        makeScreenshot("AgentMessage-ImageRemoteLight");
    }

    @Test
    public void agentMessage_imageRemoteTransparency() {
        renderMessage(MockMessage.imageOfAgent(MockAgent.Webuildapps, URL_PARLEY_IMAGE_TRANSPARENCY));
        makeScreenshot("AgentMessage-ImageRemoteTransparency");
    }

    @Test
    public void agentMessage_imageRemoteGifWithoutName() {
        renderMessage(MockMessage.imageOfAgent(null, URL_PARLEY_IMAGE_GIF));
        makeScreenshot("AgentMessage-ImageRemoteGifWithoutName");
    }

    @Test
    public void agentMessage_textAndImage() {
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, "This is an image of Parley", URL_PARLEY_LOGO_COLORED));
        makeScreenshot("AgentMessage-TextAndImage");
    }

    @Test
    public void agentMessage_fullMessageWithTitle() {
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, "Welcome", "This is an image of Parley", URL_PARLEY_IMAGE_PLATFORMS));
        makeScreenshot("AgentMessage-FullMessageWithTitle");
    }

    @Test
    public void agentMessage_simpleMessageWithActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(MockAction.create("Open app", "open-app://parley.nu"));
        actions.add(MockAction.create("Call us", "call://+31362022080"));
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, "Here are some quick actions for more information about *Parley*", null, actions, null));
        makeScreenshot("AgentMessage-TextWithActions");
    }

    @Test
    public void agentMessage_imageWithActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(MockAction.create("Web documentation", "https://developers.parley.nu/docs/introduction"));
        actions.add(MockAction.create("Android documentation", "https://developers.parley.nu/docs/introduction-1"));
        actions.add(MockAction.create("iOS documentation", "https://developers.parley.nu/docs/introduction-2"));
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, null, URL_PARLEY_IMAGE_PLATFORMS, actions, null));
        makeScreenshot("AgentMessage-ImageWithActions");
    }

    @Test
    public void agentMessage_fullMessageWithActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(MockAction.create("Open app", "open-app://parley.nu"));
        actions.add(MockAction.create("Call us", "call://+31362022080"));
        actions.add(MockAction.create("Webuildapps", "https://webuildapps.com"));
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, "Welcome", "Here are some quick actions for more information about *Parley*", URL_PARLEY_LOGO_COLORED, actions, null));
        makeScreenshot("AgentMessage-FullMessageWithActions");
    }

    @Test
    public void agentMessage_messageWithCarouselFull() {
        List<Message> carousel = new ArrayList<>();
        List<Action> actionsItem1 = new ArrayList<>();
        actionsItem1.add(MockAction.create("Parley secure messaging", "https://www.parley.nu/en/secure-messaging"));
        actionsItem1.add(MockAction.create("Developer documentation", "https://developers.parley.nu"));
        List<Action> actionsItem2 = new ArrayList<>();
        actionsItem2.add(MockAction.create("Parley", "https://parley.nu"));
        actionsItem2.add(MockAction.create("Webuildapps", "https://webuildapps.com"));
        actionsItem2.add(MockAction.create("Tracebuzz", "https://tracebuzz.com"));
        carousel.add(MockMessage.ofCarousel("Parley - Redefining Customer Happiness", "Fast and secure messaging with *Parley*. The Android and iOS library are available which enables you to fully customise the chat.", URL_PARLEY_LOGO_COLORED, actionsItem1));
        carousel.add(MockMessage.ofCarousel("More information", "Check out the following links", URL_PARLEY_IMAGE_PLATFORMS_TB, actionsItem2));

        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, "Here are some quick actions for more information about *Parley*", null, null, carousel));
        makeScreenshot("AgentMessage-MessageWithCarouselFull");
    }

    @Test
    public void agentMessage_messageWithCarouselSmall() {
        List<Action> actionsMessage = new ArrayList<>();
        actionsMessage.add(MockAction.create("Home page", "https://www.parley.nu/"));

        List<Message> carousel = new ArrayList<>();
        List<Action> actionsItem1 = new ArrayList<>();
        actionsItem1.add(MockAction.create("Android SDK", "https://github.com/parley-messaging/android-library"));
        actionsItem1.add(MockAction.create("iOS SDK", "https://github.com/parley-messaging/ios-library"));
        List<Action> actionsItem2 = new ArrayList<>();
        actionsItem2.add(MockAction.create("Web documentation", "https://developers.parley.nu/docs/introduction"));
        actionsItem2.add(MockAction.create("Android documentation", "https://developers.parley.nu/docs/introduction-1"));
        actionsItem2.add(MockAction.create("iOS documentation", "https://developers.parley.nu/docs/introduction-2"));
        carousel.add(MockMessage.ofCarousel("Parley libraries", "Parley provides open source SDK's for the Web, Android and iOS to easily integrate it with any platform.\n\nThe chat is fully customisable.", null, actionsItem1));
        carousel.add(MockMessage.ofCarousel(null, null, URL_PARLEY_IMAGE_WEB, actionsItem2));

        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, "Here are some quick actions for more information about *Parley*", URL_PARLEY_LOGO_COLORED, actionsMessage, carousel));
        makeScreenshot("AgentMessage-MessageWithCarouselSmall");
    }

    @Test
    public void agentMessage_messageWithCarouselImages() {
        List<Action> actions = new ArrayList<>();
        actions.add(MockAction.create("Home page", "https://www.parley.nu/"));

        List<Message> carousel = new ArrayList<>();
        carousel.add(MockMessage.ofCarousel(null, null, URL_PARLEY_IMAGE_TRANSPARENCY, null));
        carousel.add(MockMessage.ofCarousel(null, null, URL_PARLEY_IMAGE_WEB, null));
        carousel.add(MockMessage.ofCarousel(null, null, URL_PARLEY_IMAGE_PLATFORMS, null));
        carousel.add(MockMessage.ofCarousel(null, null, URL_PARLEY_IMAGE_PLATFORMS_TB, null));

        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, null, URL_PARLEY_IMAGE_PLATFORMS, actions, carousel));
        makeScreenshot("AgentMessage-MessageWithCarouselImages");
    }

    // Rendering order
    @Test
    public void userMessage_renderOrder_imageTextImage() {
        takeScreenshots = false;
        userMessage_imageRemoteLight();
        userMessage_longSuccessWithMarkdown();
        userMessage_imageRemoteDark();
        takeScreenshots = true;
        makeScreenshot("RenderOrder-ImageTextImage");
    }

    @AfterClass
    public static void tearDown() {
        // Make a report doc
        StringBuilder markdownText = new StringBuilder("\n" +
                "Current | Updated\n" +
                "-- | --\n");
        for (String screenshot : screenshots) {
            markdownText.append("![Current](Current/")
                    .append(screenshot)
                    .append(".png) | ![Updated](Update/")
                    .append(screenshot)
                    .append(".png)\n");
        }
        Log.d("diff", markdownText.toString());
    }

    private void renderMessage(final Message message) {
        rule.runOnMainSynchronously(new ViewTestRule.Runner<LinearLayout>() {
            @Override
            public void run(LinearLayout view) {
                final ParleyBaseViewHolder viewHolder = MessageViewHolderFactory.getViewHolder(message.getTypeId(), rule.getView(), null);
                viewHolder.show(message);
            }
        });
        sleepForVisual(0);
//        sleepForVisual(1000);
//        sleepForVisual(5000);
//        sleepForVisual(60000);
    }
}