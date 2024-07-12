package nu.parley.android.view.chat;

import android.content.Context;
import android.view.View;

import nu.parley.android.data.model.Action;
import nu.parley.android.data.model.Message;

public interface MessageListener {

    void onRetryMessageClicked(Message message);

    void onMediaClicked(Context context, Message message);

    void onActionClicked(View view, Action action);
}