package nu.parley.android.view.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nu.parley.android.Parley;
import nu.parley.android.data.model.Message;
import nu.parley.android.view.chat.holder.ParleyBaseViewHolder;

public final class MessageAdapter extends RecyclerView.Adapter<ParleyBaseViewHolder> {

    private MessageListener listener;
    private List<Message> messages = new ArrayList<>();

    public MessageAdapter(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public ParleyBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int viewResource = MessageViewHolderFactory.getViewResource(viewType);
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewResource, parent, false);
        return MessageViewHolderFactory.getViewHolder(viewType, itemView, listener);
    }

    @Override
    public int getItemViewType(int position) {
        Integer type = MessageViewHolderFactory.getViewType(messages.get(position));
        if (type == null) {
            throw new IllegalStateException("Missing message type");
        }
        return type;
    }

    @Override
    public void onBindViewHolder(final ParleyBaseViewHolder holder, int position) {
        final Message message = messages.get(position);
        holder.show(message);

        if (message.getTypeId() != null && message.getTypeId() == MessageViewHolderFactory.MESSAGE_TYPE_LOADER) {
            Parley.getInstance().loadMoreMessages();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> messages, Boolean canLoadMore) {
        MessageDiffCallback callback = new MessageDiffCallback(new ArrayList<>(this.messages));

        this.messages.clear();
        this.messages.addAll(messages);

        if (canLoadMore) {
            this.messages.add(Message.ofLoaderType());
        }

        callback.setNewList(this.messages);
        DiffUtil.calculateDiff(callback).dispatchUpdatesTo(this);
    }
}
