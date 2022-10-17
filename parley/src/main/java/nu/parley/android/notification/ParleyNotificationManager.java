package nu.parley.android.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import nu.parley.android.R;

public final class ParleyNotificationManager {

    @SuppressWarnings("WeakerAccess")
    public static final int REQUEST_CHAT_MESSAGE = 3000;
    @SuppressWarnings("WeakerAccess")
    public static final int NOTIFICATION_ID_CHAT_MESSAGE = 3000;
    @SuppressWarnings("WeakerAccess")
    public static final String NOTIFICATION_CHANNEL_CHAT_MESSAGES = "chat_messages";

    static void showChatMessage(Context context, String message, Intent intent) {
        createChannels(context);

        if (!isNotificationsEnabled(context)) {
            Log.e("ParleyNotificationManager", "Attempt to show notification without permission!");
            return;
        }

        PendingIntent contentIntent = PendingIntent.getActivity(context, REQUEST_CHAT_MESSAGE, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_CHAT_MESSAGES)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(Notification.DEFAULT_ALL);

        NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID_CHAT_MESSAGE,
                notificationBuilder.build()
        );
    }

    private static boolean isNotificationsEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    public static void cancelChatMessage(Context context) {
        NotificationManagerCompat.from(context).cancel(
                NOTIFICATION_ID_CHAT_MESSAGE
        );
    }

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_CHAT_MESSAGES,
                    context.getString(R.string.parley_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setShowBadge(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}