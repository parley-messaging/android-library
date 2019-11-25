package nu.parley.android.view.chat.holder;

import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.View;

import nu.parley.android.R;
import nu.parley.android.util.StyleUtil;

public final class AgentMessageViewHolder extends MessageViewHolder {

    private boolean showAgentName = true; // Default `true`

    public AgentMessageViewHolder(View itemView) {
        super(itemView);
        applyStyle();
    }

    @Override
    boolean shouldShowName() {
        return showAgentName;
    }

    @Override
    boolean shouldShowStatus() {
        return false;
    }

    @Override
    boolean shouldAlignRight() {
        return false;
    }

    @Override
    int getStyleTheme() {
        return R.style.ParleyMessageAgentStyle;
    }

    private void applyStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(getStyleTheme(), R.styleable.ParleyMessageAgent);
        balloonView.setNamePadding(StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageAgent_parley_name_padding, R.styleable.ParleyMessageAgent_parley_name_padding_top, R.styleable.ParleyMessageAgent_parley_name_padding_right, R.styleable.ParleyMessageAgent_parley_name_padding_bottom, R.styleable.ParleyMessageAgent_parley_name_padding_left));
        showAgentName = StyleUtil.getBoolean(ta, R.styleable.ParleyMessageAgent_parley_show_name, showAgentName);
        balloonView.setNameColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageAgent_parley_name_color));
        balloonView.setNameFont(StyleUtil.getFont(getContext(), ta, R.styleable.ParleyMessageAgent_parley_name_font_family), StyleUtil.getFontStyle(ta, R.styleable.ParleyMessageAgent_parley_name_font_style));
        balloonView.setNameTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyMessageAgent_parley_name_text_size));
        ta.recycle();
    }
}
