package nu.parley.android.view.compose;

import java.io.File;

import nu.parley.android.Parley;

import static nu.parley.android.notification.PushNotificationHandler.EVENT_START_TYPING;
import static nu.parley.android.notification.PushNotificationHandler.EVENT_STOP_TYPING;

public final class ParleyComposeListener implements ComposeListener {

    @Override
    public void onSendMessage(String message) {
        Parley.getInstance().sendMessage(message);
    }

    @Override
    public void onSendMedia(File file) {
        Parley.getInstance().sendMediaMessage(file);
    }

    @Override
    public void onStartedTyping() {
        fireEvent(EVENT_START_TYPING);
    }

    @Override
    public void onStoppedTyping() {
        fireEvent(EVENT_STOP_TYPING);
    }

    private void fireEvent(String event) {
        Parley.getInstance().getNetwork().config.getRepositories().getEventRepository().fire(event);
    }
}
