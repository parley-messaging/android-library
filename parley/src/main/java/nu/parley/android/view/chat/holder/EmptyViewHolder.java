package nu.parley.android.view.chat.holder;

import android.view.View;

import nu.parley.android.data.model.Message;

public final class EmptyViewHolder extends ParleyBaseViewHolder {

    public EmptyViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void show(Message message) {
        //
    }
}
