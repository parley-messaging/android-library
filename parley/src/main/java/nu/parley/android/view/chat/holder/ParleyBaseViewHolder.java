package nu.parley.android.view.chat.holder;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import nu.parley.android.data.model.Message;

public abstract class ParleyBaseViewHolder extends RecyclerView.ViewHolder {

    ParleyBaseViewHolder(View itemView) {
        super(itemView);
    }

    protected Context getContext() {
        return itemView.getContext();
    }

    public abstract void show(Message message);
}
