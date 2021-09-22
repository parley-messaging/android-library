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

    private final Message carouselMessage;
    private final MessageListener listener;

    public CarouselAdapter(Message carouselMessage, MessageListener listener) {
        this.carouselMessage = carouselMessage;
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
        if (carouselMessage.getCarousel() == null) {
            return;
        }
        final Message message = carouselMessage.getCarousel().get(position);
        holder.show(message, carouselMessage.getDate());
    }

    @Override
    public int getItemCount() {
        if (carouselMessage.getCarousel() == null) {
            return 0;
        } else {
            return carouselMessage.getCarousel().size();
        }
    }
}
