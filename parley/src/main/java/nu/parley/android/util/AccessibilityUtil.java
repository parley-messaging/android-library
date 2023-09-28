package nu.parley.android.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import nu.parley.android.R;
import nu.parley.android.data.model.Message;
import nu.parley.android.view.chat.MessageViewHolderFactory;

public class AccessibilityUtil {

    @Nullable
    public static String getContentDescription(View view, Message message) {
        return getContentDescription(view.getContext(), message);
    }

    @Nullable
    private static String getContentDescription(Context context, Message message) {
        switch (message.getTypeId()) {
            case MessageViewHolderFactory.MESSAGE_TYPE_INFO:
            case MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AUTO:
                return context.getString(R.string.parley_accessibility_message_informational);
            case MessageViewHolderFactory.MESSAGE_TYPE_DATE:
                return DateUtil.formatDate(message.getDate());
            default:
                break;
        }

        StringBuilder builder = new StringBuilder();
        @Nullable String intro = getContentDescriptionIntroduction(context, message);
        if (intro != null) {
            builder.append(intro);
        }
        @Nullable String body = getContentDescriptionBody(context, message);
        if (body != null) {
            builder.append(body);
        }
        @Nullable String ending = getContentDescriptionEnding(context, message);
        if (ending != null) {
            builder.append(ending);
        }
        if (builder.length() == 0) {
            return null;
        } else {
            return builder.toString();
        }
    }

    @Nullable
    private static String getContentDescriptionIntroduction(Context context, Message message) {
        switch (message.getTypeId()) {
            case MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT:
            case MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_SYSTEM_AGENT:
                if (message.getAgent() != null && message.getAgent().getName() != null) {
                    return context.getString(R.string.parley_accessibility_message_from_agent_x, message.getAgent().getName());
                } else {
                    return context.getString(R.string.parley_accessibility_message_from_agent);
                }
            case MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_OWN:
            case MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_SYSTEM_USER:
                return context.getString(R.string.parley_accessibility_message_from_you);
            case MessageViewHolderFactory.MESSAGE_TYPE_LOADER:
                return context.getString(R.string.parley_accessibility_message_loading);
            case MessageViewHolderFactory.MESSAGE_TYPE_AGENT_TYPING:
                return context.getString(R.string.parley_accessibility_message_agent_typing);
            case MessageViewHolderFactory.MESSAGE_TYPE_DATE:
            case MessageViewHolderFactory.MESSAGE_TYPE_INFO:
            case MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AUTO:
            default:
                return null;
        }
    }

    @Nullable
    private static String getContentDescriptionBody(Context context, Message message) {
        StringBuilder builder = new StringBuilder();
        // Title
        if (!TextUtils.isEmpty(message.getTitle())) {
            builder.append(message.getTitle());
            appendDotIfNeeded(builder);
        }
        // Message
        if (!TextUtils.isEmpty(message.getMessage())) {
            builder.append(message.getMessage());
            appendDotIfNeeded(builder);
        }
        // Media
        if (message.hasImageContent()) {
            builder.append(context.getString(R.string.parley_accessibility_message_media_attached));
        }
        if (builder.length() == 0) {
            return null;
        } else {
            return builder.toString();
        }
    }

    @Nullable
    private static String getContentDescriptionEnding(Context context, Message message) {
        StringBuilder builder = new StringBuilder();
        switch (message.getSendStatus()) {
            case Message.SEND_STATUS_FAILED:
                builder.append(context.getString(R.string.parley_accessibility_message_failed));
            case Message.SEND_STATUS_PENDING:
                builder.append(context.getString(R.string.parley_accessibility_message_pending));
            case Message.SEND_STATUS_SUCCESS:
            default:
                break;
        }
        if (message.getDate() != null) {
            builder.append(context.getString(R.string.parley_accessibility_message_time));
            builder.append(" ");
            builder.append(DateUtil.formatTime(message.getDate()));
        }
        if (builder.length() == 0) {
            return null;
        } else {
            return builder.toString();
        }
    }

    private static final List<String> endChars = Arrays.asList(".", "?", "!");

    private static void appendDotIfNeeded(StringBuilder builder) {
        char last = builder.charAt(builder.length() - 1);
        if (!endChars.contains(last)) {
            builder.append(".");
        }
    }
}
