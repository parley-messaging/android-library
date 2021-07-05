package nu.parley.android.view.chat.holder;

import android.content.res.TypedArray;
import android.view.View;

import nu.parley.android.R;
import nu.parley.android.util.StyleUtil;
import nu.parley.android.view.chat.MessageListener;

public final class OwnMessageViewHolder extends MessageViewHolder {

    public OwnMessageViewHolder(View itemView, MessageListener listener) {
        super(itemView, listener);
        applyStyle();
    }

    @Override
    protected boolean shouldShowName() {
        return false;
    }

    @Override
    protected boolean shouldShowStatus() {
        return true;
    }

    @Override
    protected boolean shouldAlignRight() {
        return true;
    }

    @Override
    int getStyleTheme() {
        return R.style.ParleyMessageUserStyle;
    }

    private void applyStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(getStyleTheme(), R.styleable.ParleyMessageUser);
        balloonView.setMessageStatusColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageUser_parley_message_status_color));
        balloonView.setImageStatusColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageUser_parley_image_status_color));
        ta.recycle();
    }
}
