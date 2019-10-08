package nu.parley.android.view.chat.holder;

import android.content.res.TypedArray;
import android.view.View;

import nu.parley.android.R;
import nu.parley.android.util.StyleUtil;

public final class OwnMessageViewHolder extends MessageViewHolder {

    public OwnMessageViewHolder(View itemView) {
        super(itemView);
        applyStyle();
    }

    @Override
    boolean shouldShowName() {
        return false;
    }

    @Override
    boolean shouldShowStatus() {
        return true;
    }

    @Override
    boolean shouldAlignRight() {
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
