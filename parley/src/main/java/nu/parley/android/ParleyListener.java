package nu.parley.android;

import android.content.Intent;

import androidx.annotation.Nullable;

import java.util.List;

import nu.parley.android.data.model.Message;

public interface ParleyListener {

    void onStateChanged(Parley.State state);

    void onReceivedMoreMessages(List<Message> messages);

    void onNewMessage(Message message);

    void onMessageSent();

    void onUpdateMessage(Message message);

    void onReceivedLatestMessages();

    void onAgentStartTyping();

    void onAgentStopTyping();

    boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}