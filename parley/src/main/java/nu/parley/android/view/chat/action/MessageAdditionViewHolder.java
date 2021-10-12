package nu.parley.android.view.chat.action;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.RecyclerView;

import nu.parley.android.R;
import nu.parley.android.data.model.Action;
import nu.parley.android.util.StyleUtil;

public final class MessageAdditionViewHolder extends RecyclerView.ViewHolder {

    private Context getContext() {
        return itemView.getContext();
    }

    @StyleRes
    private int currentStyle;
    private ViewGroup dividerTopLayout;
    private View dividerTopView;
    private ViewGroup dividerBottomLayout;
    private View dividerBottomView;
    private TextView titleTextView;

    MessageAdditionViewHolder(View itemView, @StyleRes int style) {
        super(itemView);

        currentStyle = style;
        dividerTopLayout = itemView.findViewById(R.id.divider_top_layout);
        dividerTopView = itemView.findViewById(R.id.divider_top_view);
        dividerBottomLayout = itemView.findViewById(R.id.divider_bottom_layout);
        dividerBottomView = itemView.findViewById(R.id.divider_bottom_view);
        titleTextView = itemView.findViewById(R.id.title_text_view);
        applyStyle();
    }

    public void show(Action action, boolean isFirst) {
        dividerTopLayout.setVisibility(isFirst ? View.VISIBLE : View.GONE);
        titleTextView.setText(action.getTitle());
    }

    private void applyStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(currentStyle, R.styleable.ParleyMessageAction);

        StyleUtil.StyleSpacing dividerSpaceData = StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageAction_parley_action_divider_margin, R.styleable.ParleyMessageAction_parley_action_divider_margin_top, R.styleable.ParleyMessageAction_parley_action_divider_margin_right, R.styleable.ParleyMessageAction_parley_action_divider_margin_bottom, R.styleable.ParleyMessageAction_parley_action_divider_margin_left);
        dividerTopLayout.setPadding(dividerSpaceData.left, dividerSpaceData.top, dividerSpaceData.right, dividerSpaceData.bottom);
        dividerBottomLayout.setPadding(dividerSpaceData.left, dividerSpaceData.top, dividerSpaceData.right, dividerSpaceData.bottom);
        @ColorInt @Nullable Integer backgroundColor = StyleUtil.getColor(ta, R.styleable.ParleyMessageAction_parley_action_divider_color);
        if (backgroundColor != null) {
            dividerTopView.setBackgroundColor(backgroundColor);
            dividerBottomView.setBackgroundColor(backgroundColor);
        }

        StyleUtil.StyleSpacing titleSpaceData = StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageAction_parley_action_padding, R.styleable.ParleyMessageAction_parley_action_padding_top, R.styleable.ParleyMessageAction_parley_action_padding_right, R.styleable.ParleyMessageAction_parley_action_padding_bottom, R.styleable.ParleyMessageAction_parley_action_padding_left);
        titleTextView.setPadding(titleSpaceData.left, titleSpaceData.top, titleSpaceData.right, titleSpaceData.bottom);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyMessageAction_parley_action_text_size));
        titleTextView.setTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageAction_parley_action_title_color));

        // Font
        Typeface font = StyleUtil.getFont(getContext(), ta, R.styleable.ParleyMessageAction_parley_action_font_family);
        int fontStyle = StyleUtil.getFontStyle(ta, R.styleable.ParleyMessageAction_parley_action_font_style);
        titleTextView.setTypeface(font, fontStyle);

        ta.recycle();
    }
}
