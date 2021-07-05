package nu.parley.android.view.chat.carousel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nu.parley.android.data.model.Message;
import nu.parley.android.view.chat.MessageListener;
import nu.parley.android.view.chat.MessageViewHolderFactory;

public final class CarouselAdapter extends RecyclerView.Adapter<CarouselViewHolder> {

    private List<Message> messages;
    private MessageListener listener;

    public CarouselAdapter(List<Message> messages, MessageListener listener) {
        this.messages = messages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(MessageViewHolderFactory.getViewResource(viewType), parent, false);
        return new CarouselViewHolder(itemView, listener);
    }


    @Override
    public int getItemViewType(int position) {
        return MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT;
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        final Message message = messages.get(position);
        holder.show(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
