package nu.parley.android.view.chat;

import android.view.View;

import nu.parley.android.data.model.Action;
import nu.parley.android.data.model.Message;

public interface MessageListener {

    void onRetryMessageClicked(Message message);

    void onMediaClicked(View view, Message message);

    void onActionClicked(View view, Action action);
}