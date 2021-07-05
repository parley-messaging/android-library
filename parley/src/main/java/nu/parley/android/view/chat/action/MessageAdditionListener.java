package nu.parley.android.view.chat.action;

import android.view.View;

import nu.parley.android.data.model.Action;

public interface MessageAdditionListener {

    void onActionClicked(View view, Action action);
}