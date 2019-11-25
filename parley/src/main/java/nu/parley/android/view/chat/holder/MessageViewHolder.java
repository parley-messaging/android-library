package nu.parley.android.view.chat.holder;

import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.StyleRes;

import nu.parley.android.R;
import nu.parley.android.data.model.Message;
import nu.parley.android.util.StyleUtil;
import nu.parley.android.view.BalloonView;

public abstract class MessageViewHolder extends ParleyBaseViewHolder {

    BalloonView balloonView;

    MessageViewHolder(View itemView) {
        super(itemView);

        balloonView = itemView.findViewById(R.id.balloon_view);
        applyBaseStyle();
    }

    abstract boolean shouldShowName();

    abstract boolean shouldShowStatus();

    abstract boolean shouldAlignRight();

    @StyleRes
    abstract int getStyleTheme();

    protected void applyBaseStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(getStyleTheme(), R.styleable.ParleyMessageBase);
        balloonView.setBackground(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyMessageBase_parley_background));
        StyleUtil.Helper.applyBackgroundColor(balloonView, ta, R.styleable.ParleyMessageBase_parley_background_tint_color);

        StyleUtil.StyleSpacing styleSpacingMargin = StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageBase_parley_margin, R.styleable.ParleyMessageBase_parley_margin_top, R.styleable.ParleyMessageBase_parley_margin_right, R.styleable.ParleyMessageBase_parley_margin_bottom, R.styleable.ParleyMessageBase_parley_margin_left);
        itemView.setPadding(styleSpacingMargin.left, styleSpacingMargin.top, styleSpacingMargin.right, styleSpacingMargin.bottom);
        balloonView.setMessageContentPadding(StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageBase_parley_message_content_padding, R.styleable.ParleyMessageBase_parley_message_content_padding_top, R.styleable.ParleyMessageBase_parley_message_content_padding_right, R.styleable.ParleyMessageBase_parley_message_content_padding_bottom, R.styleable.ParleyMessageBase_parley_message_content_padding_left));
        balloonView.setImageContentPadding(StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageBase_parley_image_content_padding, R.styleable.ParleyMessageBase_parley_image_content_padding_top, R.styleable.ParleyMessageBase_parley_image_content_padding_right, R.styleable.ParleyMessageBase_parley_image_content_padding_bottom, R.styleable.ParleyMessageBase_parley_image_content_padding_left));
        balloonView.setMetaPadding(StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageBase_parley_meta_padding, R.styleable.ParleyMessageBase_parley_meta_padding_top, R.styleable.ParleyMessageBase_parley_meta_padding_right, R.styleable.ParleyMessageBase_parley_meta_padding_bottom, R.styleable.ParleyMessageBase_parley_meta_padding_left));

        balloonView.setImageCornerRadius(StyleUtil.getDimension(ta, R.styleable.ParleyMessageBase_parley_image_corner_radius));
        balloonView.setImagePlaceholder(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyMessageBase_parley_image_placeholder));
        balloonView.setImageLoadingTintColor(StyleUtil.getColor(ta, R.styleable.ParleyMessageBase_parley_image_loader_tint_color));

        balloonView.setTextFont(StyleUtil.getFont(getContext(), ta, R.styleable.ParleyMessageBase_parley_font_family), StyleUtil.getFontStyle(ta, R.styleable.ParleyMessageBase_parley_font_style));
        balloonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyMessageBase_parley_text_size));
        balloonView.setTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_text_color));
        balloonView.setTintColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_tint_color));

        balloonView.setTimeFont(StyleUtil.getFont(getContext(), ta, R.styleable.ParleyMessageBase_parley_time_font_family), StyleUtil.getFontStyle(ta, R.styleable.ParleyMessageBase_parley_time_font_style));
        balloonView.setTimeTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyMessageBase_parley_time_text_size));
        balloonView.setTimeColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_message_time_color), StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_image_time_color));

        ta.recycle();
    }

    public void show(Message message) {
        balloonView.setLayoutGravity(shouldAlignRight() ? Gravity.END : Gravity.START);

        balloonView.refreshStyle(message.isImageOnly());
        // Agent name
        boolean showAgentName = shouldShowName() && message.getAgent() != null;
        boolean hasImage = message.getImage() != null;
        if (showAgentName) {
            balloonView.setName(message.getAgent().getName(), hasImage);
        } else {
            balloonView.setName(null, hasImage);
        }

         // Content: A message has either an image or some text
        balloonView.setImage(message.getImage());
        balloonView.setTitle(message.getTitle());
        balloonView.setText(message.getMessage());

        // Meta
        balloonView.setTime(message.getDate());
        balloonView.setStatus(message.getSendStatus());
        balloonView.setStatusVisible(shouldShowStatus());
    }
}
