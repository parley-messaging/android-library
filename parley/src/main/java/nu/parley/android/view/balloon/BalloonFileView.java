package nu.parley.android.view.balloon;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatImageView;

import nu.parley.android.R;
import nu.parley.android.data.model.Media;
import nu.parley.android.data.model.MimeType;
import nu.parley.android.util.StyleUtil;

public final class BalloonFileView extends FrameLayout {

    private LinearLayout rootLayout;
    private View dividerTop;
    private AppCompatImageView icon;
    private LinearLayout contentLayout;
    private TextView name;
    private TextView action;
    private View dividerBottom;

    public BalloonFileView(Context context) {
        super(context);
        init(context, null);
    }

    public BalloonFileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BalloonFileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.view_balloon_file, this);

        rootLayout = findViewById(R.id.root);
        dividerTop = findViewById(R.id.divider_top);
        dividerBottom = findViewById(R.id.divider_bottom);
        contentLayout = findViewById(R.id.content_layout);
        icon = findViewById(R.id.icon);
        name = findViewById(R.id.name);
        action = findViewById(R.id.action);
    }

    public void style(@StyleRes int messageStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(messageStyle, R.styleable.ParleyMessageBase);

        setContentPadding(StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageBase_parley_file_content_padding, R.styleable.ParleyMessageBase_parley_file_content_padding_top, R.styleable.ParleyMessageBase_parley_file_content_padding_right, R.styleable.ParleyMessageBase_parley_file_content_padding_bottom, R.styleable.ParleyMessageBase_parley_file_content_padding_left));
        @ColorInt @Nullable Integer dividerColor = StyleUtil.getColor(ta, R.styleable.ParleyMessageBase_parley_divider_color);
        if (dividerColor != null) {
            setDividerColor(dividerColor);
        }
        setIconTint(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_file_icon_tint_color));
//        setNameFont(StyleUtil.getFont(getContext(), ta, R.styleable.ParleyMessageBase_parley_file_name_font_family), StyleUtil.getFontStyle(ta, R.styleable.ParleyMessageBase_parley_file_name_font_style));
        setNameSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyMessageBase_parley_file_name_text_size));
        setNameColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_file_name_text_color));
//        setActionFont(StyleUtil.getFont(getContext(), ta, R.styleable.ParleyMessageBase_parley_file_action_font_family), StyleUtil.getFontStyle(ta, R.styleable.ParleyMessageBase_parley_file_action_font_style));
        setActionSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyMessageBase_parley_file_action_text_size));
        setActionColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_file_action_text_color));

        ta.recycle();
    }

    public void setContentPadding(StyleUtil.StyleSpacing data) {
        contentLayout.setPadding(data.left, data.top, data.right, data.bottom);
    }

    public void setDividerColor(@ColorInt int color) {
        dividerTop.setBackgroundColor(color);
        dividerBottom.setBackgroundColor(color);
    }

    public void setIconTint(ColorStateList color) {
        icon.setImageTintList(color);
    }

    public void setNameFont(Typeface font, int style) {
        name.setTypeface(font, style);
    }

    public void setNameSize(int complexUnit, int dimension) {
        name.setTextSize(complexUnit, dimension);
    }

    public void setNameColor(ColorStateList color) {
        name.setTextColor(color);
    }

    public void setActionFont(Typeface font, int style) {
        action.setTypeface(font, style);
    }

    public void setActionSize(int complexUnit, int dimension) {
        action.setTextSize(complexUnit, dimension);
    }

    public void setActionColor(ColorStateList color) {
        action.setTextColor(color);
    }

    public void render(@Nullable Media media, boolean showDividerTop, boolean showDividerBottom) {
        boolean visible = media != null && media.getMimeType().isFile();
        rootLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        dividerTop.setVisibility(showDividerTop ? View.VISIBLE : View.GONE);
        dividerBottom.setVisibility(showDividerBottom ? View.VISIBLE : View.GONE);
        if (media != null) {
            MimeType mimeType = media.getMimeType();
            switch (mimeType) {
                case ApplicationPdf:
                    icon.setImageResource(R.drawable.parley_ic_file_pdf);
                    break;
                default:
                    icon.setImageResource(R.drawable.parley_ic_file_unknown);
                    break;
            }
            name.setText(media.getFileName());
        }
    }
}
