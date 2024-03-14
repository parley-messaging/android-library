package nu.parley.fcm;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;

import nu.parley.android.Parley;
import nu.parley.ui.ChatActivity;

public final class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Parley.setPushToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        @SuppressWarnings("unused")
        boolean handledByParley = Parley.handle(this, remoteMessage.getData(), intent);
    }
}