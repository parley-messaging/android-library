package nu.parley.android.view.chat.holder;

import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nu.parley.android.R;
import nu.parley.android.data.model.Message;
import nu.parley.android.util.AccessibilityUtil;
import nu.parley.android.util.StyleUtil;

import static nu.parley.android.util.DateUtil.formatDate;

import androidx.core.view.ViewCompat;

public final class DateViewHolder extends ParleyBaseViewHolder {

    private ViewGroup dateContentView;
    private TextView dateTextView;

    public DateViewHolder(View itemView) {
        super(itemView);

        dateContentView = itemView.findViewById(R.id.date_content_layout);
        dateTextView = itemView.findViewById(R.id.date_text_view);
        applyStyle();
    }

    @Override
    public void show(Message message) {
        dateTextView.setText(formatDate(message.getDate()));

        // Accessibility
        itemView.setContentDescription(AccessibilityUtil.getContentDescription(itemView, message));
    }

    private void applyStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(R.style.ParleyMessageDateStyle, R.styleable.ParleyDateMessage);

        dateContentView.setBackground(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyDateMessage_parley_background));
        StyleUtil.Helper.applyBackgroundColor(dateContentView, ta, R.styleable.ParleyDateMessage_parley_background_tint_color);

        StyleUtil.StyleSpacing spaceData = StyleUtil.getSpacingData(ta, R.styleable.ParleyDateMessage_parley_content_padding, R.styleable.ParleyDateMessage_parley_content_padding_top, R.styleable.ParleyDateMessage_parley_content_padding_right, R.styleable.ParleyDateMessage_parley_content_padding_bottom, R.styleable.ParleyDateMessage_parley_content_padding_left);
        dateContentView.setPadding(spaceData.left, spaceData.top, spaceData.right, spaceData.bottom);

        dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyDateMessage_parley_text_size));
        dateTextView.setTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyDateMessage_parley_text_color));

        // Font
        Typeface font = StyleUtil.getFont(getContext(), ta, R.styleable.ParleyDateMessage_parley_font_family);
        int fontStyle = StyleUtil.getFontStyle(ta, R.styleable.ParleyDateMessage_parley_font_style);
        dateTextView.setTypeface(font, fontStyle);

        ViewCompat.setAccessibilityHeading(dateTextView, true);

        ta.recycle();
    }
}
