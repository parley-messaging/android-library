package nu.parley.android.view.chat.holder;

import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import nu.parley.android.R;
import nu.parley.android.data.model.Message;
import nu.parley.android.util.AccessibilityUtil;
import nu.parley.android.util.MarkdownUtil;
import nu.parley.android.util.StyleUtil;

public final class InfoViewHolder extends ParleyBaseViewHolder {

    private TextView messageTextView;

    public InfoViewHolder(View itemView) {
        super(itemView);

        messageTextView = itemView.findViewById(R.id.message_text_view);

        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());

        applyStyle();
    }

    private void applyStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(R.style.ParleyMessageInfoStyle, R.styleable.ParleyInfoMessage);

        StyleUtil.StyleSpacing spaceData = StyleUtil.getSpacingData(ta, R.styleable.ParleyInfoMessage_parley_content_padding, R.styleable.ParleyInfoMessage_parley_content_padding_top, R.styleable.ParleyInfoMessage_parley_content_padding_right, R.styleable.ParleyInfoMessage_parley_content_padding_bottom, R.styleable.ParleyInfoMessage_parley_content_padding_left);
        messageTextView.setPadding(spaceData.left, spaceData.top, spaceData.right, spaceData.bottom);

        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyInfoMessage_parley_text_size));
        messageTextView.setTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyInfoMessage_parley_text_color));
        messageTextView.setLinkTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyInfoMessage_parley_text_tint_color));

        // Font
        Typeface font = StyleUtil.getFont(getContext(), ta, R.styleable.ParleyInfoMessage_parley_font_family);
        int fontStyle = StyleUtil.getFontStyle(ta, R.styleable.ParleyInfoMessage_parley_font_style);
        messageTextView.setTypeface(font, fontStyle);

        ta.recycle();
    }

    @Override
    public void show(Message message) {
        messageTextView.setText(MarkdownUtil.formatText(getContext(), message.getMessage()));

        // Accessibility
        itemView.setContentDescription(AccessibilityUtil.getContentDescription(itemView, message));
    }
}
