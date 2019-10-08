package nu.parley.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import nu.parley.android.R;
import nu.parley.android.util.MarkdownUtil;
import nu.parley.android.util.StyleUtil;

public final class ParleyStickyView extends FrameLayout {

    private AppCompatImageView iconImageView;
    private TextView messageTextView;

    public ParleyStickyView(Context context) {
        super(context);
        init(context, null);
    }

    public ParleyStickyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ParleyStickyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.view_sticky, this);

        iconImageView = findViewById(R.id.icon_image_view);
        messageTextView = findViewById(R.id.message_text_view);

        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());

        applyStyle(context, attrs);
    }

    private void applyStyle(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(R.style.ParleyStickyViewStyle, R.styleable.ParleyStickyView);

            setBackground(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyStickyView_parley_background));
            StyleUtil.Helper.applyBackgroundColor(this, ta, R.styleable.ParleyStickyView_parley_background_tint_color);

            StyleUtil.StyleSpacing styleSpacingPadding = StyleUtil.getSpacingData(ta, R.styleable.ParleyStickyView_parley_content_padding, R.styleable.ParleyStickyView_parley_content_padding_top, R.styleable.ParleyStickyView_parley_content_padding_right, R.styleable.ParleyStickyView_parley_content_padding_bottom, R.styleable.ParleyStickyView_parley_content_padding_left);
            setPadding(styleSpacingPadding.left, styleSpacingPadding.top, styleSpacingPadding.right, styleSpacingPadding.bottom);

            iconImageView.setImageDrawable(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyStickyView_parley_icon));
            iconImageView.setSupportImageTintList(StyleUtil.getColorStateList(ta, R.styleable.ParleyStickyView_parley_icon_tint_color));

            Typeface font = StyleUtil.getFont(getContext(), ta, R.styleable.ParleyStickyView_parley_font_family);
            int fontStyle = StyleUtil.getFontStyle(ta, R.styleable.ParleyStickyView_parley_font_style);
            messageTextView.setTypeface(font, fontStyle);
            messageTextView.setTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyStickyView_parley_text_color));
            messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyStickyView_parley_text_size));
            messageTextView.setLinkTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyStickyView_parley_text_tint_color));

            ta.recycle();
        }
    }

    public void setMessage(String text) {
        setVisibility(text == null ? View.GONE : View.VISIBLE);
        if (text != null) {
            messageTextView.setText(MarkdownUtil.convert(getContext(), text));
        }
    }
}
