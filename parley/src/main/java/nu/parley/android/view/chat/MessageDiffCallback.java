package nu.parley.android.view.chat;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import nu.parley.android.data.model.Message;

public final class MessageDiffCallback extends DiffUtil.Callback {

    private List<Message> oldList;
    private List<Message> newList;

    MessageDiffCallback(List<Message> oldList) {
        this.oldList = oldList;
    }

    void setNewList(List<Message> newList) {
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getUuid() == newList.get(newItemPosition).getUuid();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).isEqualVisually(newList.get(newItemPosition));
    }
}
