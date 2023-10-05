package nu.parley.android.view.chat.action;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nu.parley.android.data.model.Action;
import nu.parley.android.view.chat.MessageViewHolderFactory;

public final class MessageAdditionAdapter extends RecyclerView.Adapter<MessageAdditionViewHolder> {

    private List<Action> actions;
    private boolean showTopDivider;
    private MessageAdditionListener listener;
    @StyleRes
    private int currentStyle;

    public MessageAdditionAdapter(List<Action> actions, boolean showTopDivider, MessageAdditionListener listener, @StyleRes int style) {
        this.actions = actions;
        this.showTopDivider = showTopDivider;
        this.listener = listener;
        this.currentStyle = style;
    }

    @NonNull
    @Override
    public MessageAdditionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(MessageViewHolderFactory.getViewResource(viewType), parent, false);
        return new MessageAdditionViewHolder(itemView, currentStyle);
    }

    @Override
    public int getItemViewType(int position) {
        return MessageViewHolderFactory.MESSAGE_TYPE_ACTION;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdditionViewHolder holder, int position) {
        final Action action = actions.get(position);
        holder.show(action, action == actions.get(0) && showTopDivider);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionClicked(v, action);
            }
        });
        holder.itemView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public List<Action> getActions() {
        return actions;
    }

    public void onActionClicked(View view, Action action) {
        listener.onActionClicked(view, action);
    }
}
