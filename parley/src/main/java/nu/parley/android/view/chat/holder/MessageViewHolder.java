package nu.parley.android.view.chat.holder;

import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.model.GlideUrl;

import java.util.Date;

import nu.parley.android.R;
import nu.parley.android.data.model.Action;
import nu.parley.android.data.model.Message;
import nu.parley.android.data.net.Connectivity;
import nu.parley.android.util.AccessibilityMonitor;
import nu.parley.android.util.AccessibilityUtil;
import nu.parley.android.util.StyleUtil;
import nu.parley.android.view.BalloonView;
import nu.parley.android.view.chat.MessageListener;
import nu.parley.android.view.chat.MessageViewHolderFactory;
import nu.parley.android.view.chat.action.MessageAdditionAdapter;
import nu.parley.android.view.chat.action.MessageAdditionListener;
import nu.parley.android.view.chat.carousel.CarouselAdapter;

public abstract class MessageViewHolder extends ParleyBaseViewHolder {

    protected ViewGroup balloonLayout;
    BalloonView balloonView;
    private RecyclerView carouselRecyclerView;
    MessageListener listener;

    MessageViewHolder(View itemView, MessageListener listener) {
        super(itemView);

        balloonLayout = itemView.findViewById(R.id.balloon_layout);
        balloonView = itemView.findViewById(R.id.balloon_view);
        carouselRecyclerView = itemView.findViewById(R.id.carousel_recycler_view);
        this.listener = listener;
        applyBaseStyle();
    }

    protected abstract boolean shouldShowName();

    protected abstract boolean shouldShowStatus();

    protected abstract boolean shouldAlignRight();

    @StyleRes
    abstract int getStyleTheme();

    private void applyBaseStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(getStyleTheme(), R.styleable.ParleyMessageBase);
        balloonView.setBackground(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyMessageBase_parley_background));
        StyleUtil.Helper.applyBackgroundColor(balloonView, ta, R.styleable.ParleyMessageBase_parley_background_tint_color);

        StyleUtil.StyleSpacing styleSpacingMargin = StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageBase_parley_margin, R.styleable.ParleyMessageBase_parley_margin_top, R.styleable.ParleyMessageBase_parley_margin_right, R.styleable.ParleyMessageBase_parley_margin_bottom, R.styleable.ParleyMessageBase_parley_margin_left);
        balloonLayout.setPadding(styleSpacingMargin.left, styleSpacingMargin.top, styleSpacingMargin.right, styleSpacingMargin.bottom);
        balloonView.setMessageContentPadding(StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageBase_parley_message_content_padding, R.styleable.ParleyMessageBase_parley_message_content_padding_top, R.styleable.ParleyMessageBase_parley_message_content_padding_right, R.styleable.ParleyMessageBase_parley_message_content_padding_bottom, R.styleable.ParleyMessageBase_parley_message_content_padding_left));
        balloonView.setImageContentPadding(StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageBase_parley_image_content_padding, R.styleable.ParleyMessageBase_parley_image_content_padding_top, R.styleable.ParleyMessageBase_parley_image_content_padding_right, R.styleable.ParleyMessageBase_parley_image_content_padding_bottom, R.styleable.ParleyMessageBase_parley_image_content_padding_left));
        balloonView.setMetaPadding(StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageBase_parley_meta_padding, R.styleable.ParleyMessageBase_parley_meta_padding_top, R.styleable.ParleyMessageBase_parley_meta_padding_right, R.styleable.ParleyMessageBase_parley_meta_padding_bottom, R.styleable.ParleyMessageBase_parley_meta_padding_left));

        balloonView.setImageCornerRadius(StyleUtil.getDimension(ta, R.styleable.ParleyMessageBase_parley_image_corner_radius));
        balloonView.setImagePlaceholder(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyMessageBase_parley_image_placeholder));
        balloonView.setImagePlaceholderTintColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_image_placeholder_tint_color));
        balloonView.setImageLoadingTintColor(StyleUtil.getColor(ta, R.styleable.ParleyMessageBase_parley_image_loader_tint_color));

        balloonView.style(getStyleTheme());

        balloonView.setTextFont(StyleUtil.getFont(getContext(), ta, R.styleable.ParleyMessageBase_parley_font_family), StyleUtil.getFontStyle(ta, R.styleable.ParleyMessageBase_parley_font_style));
        balloonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyMessageBase_parley_text_size));
        balloonView.setTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_text_color));
        balloonView.setTintColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_tint_color));

        balloonView.setTimeFont(StyleUtil.getFont(getContext(), ta, R.styleable.ParleyMessageBase_parley_time_font_family), StyleUtil.getFontStyle(ta, R.styleable.ParleyMessageBase_parley_time_font_style));
        balloonView.setTimeTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyMessageBase_parley_time_text_size));
        balloonView.setTimeColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_message_time_color), StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageBase_parley_image_time_color));

        ta.recycle();
    }

    public void show(final Message message) {
        show(message, message.getDate());
    }

    public void show(final Message message, final Date messageTime) {
        balloonView.setLayoutGravity(shouldAlignRight() ? Gravity.END : Gravity.START);

        if (message.hasContent()) {
            balloonLayout.setVisibility(View.VISIBLE);
        } else {
            balloonLayout.setVisibility(View.GONE);
        }

        balloonView.refreshStyle(message.isContentImageOnly());
        // Agent name
        boolean showAgentName = shouldShowName() && message.getAgent() != null;
        boolean hasImage = message.hasImageContent();
        if (showAgentName) {
            balloonView.setName(message.getAgent().getName(), hasImage, !message.hasTextContent());
        } else {
            balloonView.setName(null, hasImage, !message.hasTextContent());
        }

        // Media: A message has either an image or a file, never both.
        balloonView.setImage(message.getImageUrl(), message.isImageOnly());
        boolean showFileDividerTop = message.hasName() || message.hasTextContent();
        balloonView.setFile(message.getMedia(), showFileDividerTop, !message.hasActionsContent());

        balloonView.setHasTextContent(message.hasTextContent());
        balloonView.setTitle(message.getTitle());
        balloonView.setText(message.getMessage());

        // Additional data
        if (message.getActions() == null) {
            balloonView.setAddition(null);
        } else {
            MessageAdditionAdapter messageAdditionAdapter = new MessageAdditionAdapter(
                    message.getActions(),
                    showAgentName || message.hasTextContent(),
                    new MessageAdditionListener() {
                        @Override
                        public void onActionClicked(View view, Action action) {
                            listener.onActionClicked(view, action);
                        }
                    },
                    getStyleTheme()
            );
            balloonView.setAddition(messageAdditionAdapter);
        }

        // Meta
        balloonView.setTextMetaSpace(!(message.hasFileContent() || message.hasActionsContent()));
        balloonView.setBottomMetaSpace(message.hasFileContent() || message.hasActionsContent());
        balloonView.setInfo(message.getResponseInfoType());
        balloonView.setTime(messageTime);
        balloonView.setStatus(message.getSendStatus());
        balloonView.setStatusVisible(shouldShowStatus());

        balloonView.setOnContentClickListener(getContentClickListener(message));

        handleCarousel(message);

        // Accessibility
        balloonView.setContentDescription(AccessibilityUtil.getContentDescription(itemView, message));
    }

    @Nullable
    private View.OnClickListener getContentClickListener(final Message message) {
        boolean talkback = AccessibilityMonitor.isTalkbackEnabled(itemView.getContext());
        final boolean retry = message.getTypeId() != null && message.getTypeId() == MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_OWN && message.getSendStatus() == Message.SEND_STATUS_FAILED;
        final boolean image = message.hasImageContent();
        if (retry || (image && !talkback)) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (retry) {
                        listener.onRetryMessageClicked(message);
                    } else if (image) {
                        listener.onMediaClicked(itemView.getContext(), message);
                    }
                }
            };
        } else {
            return null;
        }
    }

    private void handleCarousel(Message message) {
        if (message.getCarousel() == null || message.getCarousel().isEmpty()) {
            carouselRecyclerView.setVisibility(View.GONE);
        } else {
            carouselRecyclerView.setVisibility(View.VISIBLE);
        }
        carouselRecyclerView.setAdapter(new CarouselAdapter(message, listener));
    }
}
