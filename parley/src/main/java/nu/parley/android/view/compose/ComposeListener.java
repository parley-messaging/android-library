package nu.parley.android.view.compose;

import java.io.File;

public interface ComposeListener {

    void onSendMessage(String message);

    void onSendImage(File file);

    /**
     * Called when the user starts typing and when the user is still typing after the given startTypingTriggerInterval.
     */
    void onStartedTyping();

    void onStoppedTyping();
}