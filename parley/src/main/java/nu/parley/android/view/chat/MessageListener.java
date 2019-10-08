package nu.parley.android.view.chat;

import android.content.Context;

import nu.parley.android.data.model.Message;

public interface MessageListener {

    void onRetryMessageClicked(Message message);

    void onImageClicked(Context context, Message message);
}