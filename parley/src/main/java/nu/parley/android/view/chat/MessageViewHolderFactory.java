package nu.parley.android.view.chat;

import android.view.View;

import nu.parley.android.R;
import nu.parley.android.data.model.Message;
import nu.parley.android.view.chat.holder.AgentMessageViewHolder;
import nu.parley.android.view.chat.holder.AgentTypingMessageViewHolder;
import nu.parley.android.view.chat.holder.DateViewHolder;
import nu.parley.android.view.chat.holder.EmptyViewHolder;
import nu.parley.android.view.chat.holder.InfoViewHolder;
import nu.parley.android.view.chat.holder.LoaderViewHolder;
import nu.parley.android.view.chat.holder.OwnMessageViewHolder;
import nu.parley.android.view.chat.holder.ParleyBaseViewHolder;

public final class MessageViewHolderFactory {

    // Message type id's greater than 0 are id's from Parley.
    public final static int MESSAGE_TYPE_ACTION = -4;
    public final static int MESSAGE_TYPE_LOADER = -3;
    public final static int MESSAGE_TYPE_DATE = -2;
    public final static int MESSAGE_TYPE_AGENT_TYPING = -1;
    public final static int MESSAGE_TYPE_INFO = 0;
    public final static int MESSAGE_TYPE_MESSAGE_OWN = 1;
    public final static int MESSAGE_TYPE_MESSAGE_AGENT = 2;
    public final static int MESSAGE_TYPE_MESSAGE_AUTO = 3;

    static int getViewType(Message message) {
        return message.getTypeId();
    }

    public static ParleyBaseViewHolder getViewHolder(int viewType, View itemView, MessageListener listener) {
        switch (viewType) {
            case MESSAGE_TYPE_INFO:
                return new InfoViewHolder(itemView);
            case MESSAGE_TYPE_DATE:
                return new DateViewHolder(itemView);
            case MESSAGE_TYPE_AGENT_TYPING:
                return new AgentTypingMessageViewHolder(itemView);
            case MESSAGE_TYPE_MESSAGE_OWN:
                return new OwnMessageViewHolder(itemView, listener);
            case MESSAGE_TYPE_MESSAGE_AGENT:
                return new AgentMessageViewHolder(itemView, listener);
            case MESSAGE_TYPE_MESSAGE_AUTO:
                return new InfoViewHolder(itemView);
            case MESSAGE_TYPE_LOADER:
                return new LoaderViewHolder(itemView);
            default:
                return new EmptyViewHolder(itemView);
        }
    }

    public static int getViewResource(int viewType) {
        switch (viewType) {
            case MESSAGE_TYPE_INFO:
                return R.layout.item_info;
            case MESSAGE_TYPE_DATE:
                return R.layout.item_date;
            case MESSAGE_TYPE_AGENT_TYPING:
                return R.layout.item_typing;
            case MESSAGE_TYPE_MESSAGE_OWN:
                // fallthrough;
            case MESSAGE_TYPE_MESSAGE_AGENT:
                return R.layout.item_message;
            case MESSAGE_TYPE_MESSAGE_AUTO:
                return R.layout.item_info;
            case MESSAGE_TYPE_LOADER:
                return R.layout.item_loader;
            case MESSAGE_TYPE_ACTION:
                return R.layout.item_action;
            default:
                return R.layout.item_empty;
        }
    }
}
