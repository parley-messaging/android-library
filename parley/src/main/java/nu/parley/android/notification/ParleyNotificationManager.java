package nu.parley.android.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

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

        PendingIntent contentIntent = PendingIntent.getActivity(context, REQUEST_CHAT_MESSAGE, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_CHAT_MESSAGES)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(android.app.Notification.DEFAULT_ALL);

        NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID_CHAT_MESSAGE,
                notificationBuilder.build()
        );
    }

    public static void cancelChatMessage(Context context) {
        NotificationManagerCompat.from(context).cancel(
                NOTIFICATION_ID_CHAT_MESSAGE
        );
    }

    private static void createChannels(Context context) {
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