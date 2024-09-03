package nu.parley.android.notification;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import nu.parley.android.Parley;
import nu.parley.android.data.model.Message;
import nu.parley.android.data.model.PushEventBody;
import nu.parley.android.data.model.PushMessage;

public final class PushNotificationHandler {

    public final static String EVENT_START_TYPING = "startTyping";
    public final static String EVENT_STOP_TYPING = "stopTyping";
    private final static String KEY_PARLEY = "parley";
    private final static String MESSAGE_KEY = "message";
    private final static String MESSAGE_ID = "id";
    private final static String NAME = "name";
    private final static String TYPE = "type";
    private final static String OBJECT = "object";
    private final static String TYPE_MESSAGE = "message";
    private final static String TYPE_EVENT = "event";

    public static void showNotification(Context context, Map<String, String> data, Intent intent) {
        String message = getMessage(data);
        ParleyNotificationManager.showChatMessage(context, message, intent);
    }

    public static boolean isParleyMessage(Map<String, String> data) {
        return data.containsKey(KEY_PARLEY);
    }

    public static boolean isMessage(Map<String, String> data) {
        return TYPE_MESSAGE.equals(getParleyObjectStringValue(data, TYPE));
    }

    public static Integer getMessageId(Map<String, String> data) {
        return getParleyPushMessageBody(data).getId();
    }

    public static String getMessage(Map<String, String> data) {
        return data.get(MESSAGE_KEY);
    }

    public static boolean isEvent(Map<String, String> data) {
        return TYPE_EVENT.equals(getParleyObjectStringValue(data, TYPE));
    }

    public static String getEventType(Map<String, String> data) {
        return getParleyPushEventBody(data).getName();
    }

    public static JSONObject getParleyObject(Map<String, String> data) throws JSONException {
        return new JSONObject(data.get(KEY_PARLEY));
    }

    private static PushEventBody getParleyPushEventBody(Map<String, String> data) {
        return Parley.getInstance().getNetwork().config.getJsonParser().getPushEventBody(data);
    }

    private static PushMessage getParleyPushMessageBody(Map<String, String> data) {
        return Parley.getInstance().getNetwork().config.getJsonParser().getPushMessageBody(data);
    }

    private static String getParleyObjectStringValue(Map<String, String> data, String key) {
        try {
            if (getParleyObject(data).has(key)) {
                return getParleyObject(data).getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Integer getParleyObjectIntValue(Map<String, String> data, String key) {
        try {
            if (getParleyObject(data).has(key)) {
                return getParleyObject(data).getInt(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Message attemptParseAsMessage(Map<String, String> data) {
        PushMessage pushMessage = getParleyPushMessageBody(data);
        if (pushMessage == null) {
            return null;
        }

        return Message.from(pushMessage);
    }
}
