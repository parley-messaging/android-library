package nu.parley.android;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.AfterClass;
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
public class ParleyChatTestSuit extends ParleyScreenBaseTest<View> {

    private static final String URL_PARLEY_INVALID = "https://parley";
    private static final String URL_PARLEY_LOGO_COLORED = "https://www.tracebuzz.com/assets/images/parley-blog.jpg";
    private static final String URL_PARLEY_LOGO_LIGHT = "https://media.licdn.com/dms/image/C560BAQGaitUb5D_v9Q/company-logo_200_200/0?e=2159024400&v=beta&t=zQCzNT4cnFEiEfzKkzBaBaGfK5rapGGXNKLFjZYFcH4";
    private static final String URL_PARLEY_IMAGE_TRANSPARENCY = "https://images.prismic.io/endeavour-parley/48f62e1a-4450-469b-abb6-fa0e00acb68a_Chat.png?auto=compress,format&amp;h=500";
    private static final String URL_PARLEY_IMAGE_GIF = "https://media0.giphy.com/media/kEKcOWl8RMLde/giphy.gif";
    private static final String URL_PARLEY_IMAGE_PERSON = "https://images.prismic.io/endeavour-parley/05e079ec-2a7a-4069-9107-6518acb2879e_Scherm%C2%ADafbeelding+2022-12-16+om+11.14.43.png?auto=compress,format";
    private static final String URL_PARLEY_IMAGE_WEB = "https://www.tracebuzz.com/assets/images/parley_tab.png";
    private static final String URL_PARLEY_IMAGE_SOCIALS = "https://images.prismic.io/endeavour-parley/72be8647-8b35-4b7e-ba6e-9e2c527d78f7_PARLEY_STILLS+-+SCENE_3_MESSAGING.jpg?auto=compress,format&h=500";

    @Override
    View createView(Activity activity) {
        return LayoutInflater.from(activity).inflate(R.layout.item_message, null);
    }

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
        sleepForVisual(500);
        makeScreenshot("UserMessage-ImageFailure");
    }

    @Test
    public void userMessage_imageRemoteDark() {
        renderMessage(MockMessage.imageOfUser(URL_PARLEY_LOGO_COLORED, Message.SEND_STATUS_SUCCESS));
        sleepForVisual(500);
        makeScreenshot("UserMessage-ImageRemoteDark");
    }

    @Test
    public void userMessage_imageRemoteLight() {
        renderMessage(MockMessage.imageOfUser(URL_PARLEY_LOGO_LIGHT, Message.SEND_STATUS_SUCCESS));
        sleepForVisual(500);
        makeScreenshot("UserMessage-ImageRemoteLight");
    }

    @Test
    public void userMessage_imageRemoteTransparency() {
        renderMessage(MockMessage.imageOfUser(URL_PARLEY_IMAGE_TRANSPARENCY, Message.SEND_STATUS_SUCCESS));
        sleepForVisual(500);
        makeScreenshot("UserMessage-ImageRemoteTransparency");
    }

    @Test
    public void userMessage_imageRemoteGif() {
        renderMessage(MockMessage.imageOfUser(URL_PARLEY_IMAGE_GIF, Message.SEND_STATUS_SUCCESS));
        sleepForVisual(500);
        makeScreenshot("UserMessage-ImageRemoteGif");
    }

    @Test
    public void userMessage_textAndImage() {
        renderMessage(MockMessage.messageOfUser("Look at this image", URL_PARLEY_LOGO_COLORED, Message.SEND_STATUS_SUCCESS));
        sleepForVisual(500);
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
        sleepForVisual(500);
        makeScreenshot("AgentMessage-ImageFailure");
    }

    @Test
    public void agentMessage_imageRemoteDark() {
        renderMessage(MockMessage.imageOfAgent(MockAgent.Webuildapps, URL_PARLEY_LOGO_COLORED));
        sleepForVisual(500);
        makeScreenshot("AgentMessage-ImageRemoteDark");
    }

    @Test
    public void agentMessage_imageRemoteLight() {
        renderMessage(MockMessage.imageOfAgent(MockAgent.Webuildapps, URL_PARLEY_LOGO_LIGHT));
        sleepForVisual(500);
        makeScreenshot("AgentMessage-ImageRemoteLight");
    }

    @Test
    public void agentMessage_imageRemoteTransparency() {
        renderMessage(MockMessage.imageOfAgent(MockAgent.Webuildapps, URL_PARLEY_IMAGE_TRANSPARENCY));
        sleepForVisual(500);
        makeScreenshot("AgentMessage-ImageRemoteTransparency");
    }

    @Test
    public void agentMessage_imageRemoteGifWithoutName() {
        renderMessage(MockMessage.imageOfAgent(null, URL_PARLEY_IMAGE_GIF));
        sleepForVisual(500);
        makeScreenshot("AgentMessage-ImageRemoteGifWithoutName");
    }

    @Test
    public void agentMessage_textAndImage() {
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, "This is an image of Parley", URL_PARLEY_LOGO_COLORED));
        sleepForVisual(500);
        makeScreenshot("AgentMessage-TextAndImage");
    }

    @Test
    public void agentMessage_fullMessageWithTitle() {
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, "Welcome", "This is an image of Parley", URL_PARLEY_IMAGE_PERSON));
        sleepForVisual(500);
        makeScreenshot("AgentMessage-FullMessageWithTitle");
    }

    @Test
    public void agentMessage_textWithActions() {
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
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, null, URL_PARLEY_IMAGE_PERSON, actions, null));
        sleepForVisual(500);
        makeScreenshot("AgentMessage-ImageWithActions");
    }

    @Test
    public void agentMessage_fullMessageWithActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(MockAction.create("Open app", "open-app://parley.nu"));
        actions.add(MockAction.create("Call us", "call://+31362022080"));
        actions.add(MockAction.create("Webuildapps", "https://webuildapps.com"));
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, "Welcome", "Here are some quick actions for more information about *Parley*", URL_PARLEY_LOGO_COLORED, actions, null));
        sleepForVisual(500);
        makeScreenshot("AgentMessage-FullMessageWithActions");
    }

    @Test
    public void agentMessage_onlyActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(MockAction.create("Open app", "open-app://parley.nu"));
        actions.add(MockAction.create("Call us", "call://+31362022080"));
        actions.add(MockAction.create("Webuildapps", "https://webuildapps.com"));
        renderMessage(MockMessage.messageOfAgent(null, null, null, null, actions, null));
        makeScreenshot("AgentMessage-OnlyActions");
    }

    @Test
    public void agentMessage_onlyActionsWithName() {
        List<Action> actions = new ArrayList<>();
        actions.add(MockAction.create("Open app", "open-app://parley.nu"));
        actions.add(MockAction.create("Call us", "call://+31362022080"));
        actions.add(MockAction.create("Webuildapps", "https://webuildapps.com"));
        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, null, null, actions, null));
        makeScreenshot("AgentMessage-OnlyActionsWithName");
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
        carousel.add(MockMessage.ofCarousel("More information", "Check out the following links", URL_PARLEY_IMAGE_SOCIALS, actionsItem2));

        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, "Here are some quick actions for more information about *Parley*", null, null, carousel));
        sleepForVisual(500);
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
        sleepForVisual(500);
        makeScreenshot("AgentMessage-MessageWithCarouselSmall");
    }

    @Test
    public void agentMessage_messageWithCarouselOnly() {
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

        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, null, null, null, carousel));
        sleepForVisual(500);
        makeScreenshot("AgentMessage-MessageWithCarouselOnly");
    }

    @Test
    public void agentMessage_messageWithCarouselImages() {
        List<Action> actions = new ArrayList<>();
        actions.add(MockAction.create("Home page", "https://www.parley.nu/"));

        List<Message> carousel = new ArrayList<>();
        carousel.add(MockMessage.ofCarousel(null, null, URL_PARLEY_IMAGE_TRANSPARENCY, null));
        carousel.add(MockMessage.ofCarousel(null, null, URL_PARLEY_IMAGE_WEB, null));
        carousel.add(MockMessage.ofCarousel(null, null, URL_PARLEY_IMAGE_PERSON, null));
        carousel.add(MockMessage.ofCarousel(null, null, URL_PARLEY_IMAGE_SOCIALS, null));

        renderMessage(MockMessage.messageOfAgent(MockAgent.Webuildapps, null, null, URL_PARLEY_IMAGE_PERSON, actions, carousel));
        sleepForVisual(500);
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
        getView(view -> {
            final ParleyBaseViewHolder viewHolder = MessageViewHolderFactory.getViewHolder(message.getTypeId(), view, null);
            viewHolder.show(message);
        });
    }
}