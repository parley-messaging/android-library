package nu.parley.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import nu.parley.android.R;
import nu.parley.android.util.StyleUtil;

public final class ParleyNotificationView extends FrameLayout {

    private AppCompatImageView iconImageView;
    private TextView messageTextView;

    public ParleyNotificationView(Context context) {
        super(context);
        init(context, null);
    }

    public ParleyNotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ParleyNotificationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.view_notification, this);

        iconImageView = findViewById(R.id.icon_image_view);
        messageTextView = findViewById(R.id.message_text_view);

        applyStyle(context, attrs);
    }

    private void applyStyle(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(R.style.ParleyNotificationViewStyle, R.styleable.ParleyNotificationView);

            setBackground(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyNotificationView_parley_background));
            StyleUtil.Helper.applyBackgroundColor(this, ta, R.styleable.ParleyNotificationView_parley_background_tint_color);

            StyleUtil.StyleSpacing styleSpacingPadding = StyleUtil.getSpacingData(ta, R.styleable.ParleyNotificationView_parley_content_padding, R.styleable.ParleyNotificationView_parley_content_padding_top, R.styleable.ParleyNotificationView_parley_content_padding_right, R.styleable.ParleyNotificationView_parley_content_padding_bottom, R.styleable.ParleyNotificationView_parley_content_padding_left);
            setPadding(styleSpacingPadding.left, styleSpacingPadding.top, styleSpacingPadding.right, styleSpacingPadding.bottom);

            iconImageView.setImageDrawable(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyNotificationView_parley_icon));
            iconImageView.setSupportImageTintList(StyleUtil.getColorStateList(ta, R.styleable.ParleyNotificationView_parley_icon_tint_color));

            Typeface font = StyleUtil.getFont(getContext(), ta, R.styleable.ParleyNotificationView_parley_font_family);
            int fontStyle = StyleUtil.getFontStyle(ta, R.styleable.ParleyNotificationView_parley_font_style);
            messageTextView.setTypeface(font, fontStyle);
            messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyNotificationView_parley_text_size));
            messageTextView.setTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyNotificationView_parley_text_color));

            ta.recycle();
        }
    }

    public void setMessage(String text) {
        messageTextView.setText(text);
    }
}
