package nu.parley.android.view;

import static nu.parley.android.data.model.Message.SEND_STATUS_FAILED;
import static nu.parley.android.data.model.Message.SEND_STATUS_PENDING;
import static nu.parley.android.data.model.Message.SEND_STATUS_SUCCESS;
import static nu.parley.android.util.DateUtil.formatTime;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nu.parley.android.R;
import nu.parley.android.data.model.Action;
import nu.parley.android.data.model.Media;
import nu.parley.android.data.net.response.ParleyNotificationResponseType;
import nu.parley.android.util.MarkdownUtil;
import nu.parley.android.util.StyleUtil;
import nu.parley.android.view.balloon.BalloonFileView;
import nu.parley.android.view.chat.action.MessageAdditionAdapter;

public final class BalloonView extends FrameLayout {

    public final static int NAME_SHADOW_EXTRA_WIDTH = 50;

    private ViewGroup contentLayout;
    private TextView nameTextView;
    private View nameSpaceView;
    private ViewGroup imageLayout;
    private View nameShadowView;
    private View infoShadowView;
    private View metaShadowView;
    private ViewGroup messageLayout;
    private TextView titleTextView;
    private TextView messageTextView;
    private TextView infoTextView;
    private View messageMetaSpace;
    private ImageView contentImageView;
    private AppCompatImageView contentImagePlaceholderView;
    private ProgressBar imageLoader;
    private BalloonFileView fileView;

    private ViewGroup metaLayout;
    private TextView timeTextView;
    private ViewGroup statusLayout;
    private AppCompatImageView statusImageView;

    private RecyclerView actionsRecyclerView;
    private View actionsMetaSpace;

    // Styling
    private int imageCornerRadius;
    private ColorStateList messageTimeColor;
    private ColorStateList messageStatusColor;
    private ColorStateList imageTimeColor;
    private ColorStateList imageStatusColor;

    public BalloonView(Context context) {
        super(context);
        init();
    }

    public BalloonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BalloonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_balloon, this);

        contentLayout = findViewById(R.id.content_layout);
        nameTextView = findViewById(R.id.name_text_view);
        nameSpaceView = findViewById(R.id.name_space_view);
        imageLayout = findViewById(R.id.image_layout);
        nameShadowView = findViewById(R.id.name_shadow_view);
        infoShadowView = findViewById(R.id.info_shadow_view);
        metaShadowView = findViewById(R.id.meta_shadow_view);
        messageLayout = findViewById(R.id.message_layout);
        titleTextView = findViewById(R.id.title_text_view);
        messageTextView = findViewById(R.id.message_text_view);
        infoTextView = findViewById(R.id.info_text_view);
        messageMetaSpace = findViewById(R.id.message_meta_space);
        contentImageView = findViewById(R.id.image_view);
        contentImagePlaceholderView = findViewById(R.id.image_placeholder_view);
        imageLoader = findViewById(R.id.image_loader);
        fileView = findViewById(R.id.file_view);

        metaLayout = findViewById(R.id.meta_layout);
        timeTextView = findViewById(R.id.time_text_view);
        statusLayout = findViewById(R.id.status_layout);
        statusImageView = findViewById(R.id.status_image_view);

        actionsRecyclerView = findViewById(R.id.actions_recycler_view);
        actionsMetaSpace = findViewById(R.id.actions_meta_space);

        titleTextView.setMovementMethod(LinkMovementMethod.getInstance());
        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void style(@StyleRes int messageStyle) {
        fileView.style(messageStyle);
    }

    public void setName(@Nullable String text, boolean hasImage, boolean useBottomMargin) {
        nameTextView.setText(text);
        nameSpaceView.setVisibility(useBottomMargin ? View.VISIBLE : View.GONE);

        nameTextView.setVisibility(text == null ? View.GONE : View.VISIBLE);
        imageLayout.setVisibility(text == null && !hasImage ? View.GONE : View.VISIBLE);
    }

    public void setTitle(@Nullable String text) {
        titleTextView.setVisibility(text == null ? View.GONE : View.VISIBLE);
        if (text != null) {
            titleTextView.setText(MarkdownUtil.convert(getContext(), text));
        }
    }

    public void setText(@Nullable String text) {
        messageTextView.setVisibility(text == null ? View.GONE : View.VISIBLE);
        if (text != null) {
            messageTextView.setText(MarkdownUtil.convert(getContext(), text));
        }
    }

    public void setInfo(@Nullable String text) {
        ParleyNotificationResponseType type = ParleyNotificationResponseType.from(text);
        infoTextView.setVisibility(type == null ? View.GONE : View.VISIBLE);
        infoShadowView.setVisibility(type == null ? View.GONE : View.VISIBLE);
        if (type != null) {
            switch (type) {
                case MediaInvalidType:
                case MediaMissing:
                case MediaCouldNotSave:
                    infoTextView.setText(R.string.parley_message_meta_failed_to_send);
                    break;
                case MediaTooLarge:
                    infoTextView.setText(R.string.parley_message_meta_media_too_large);
                    break;
                default:
                    infoTextView.setText(null);
                    break;
            }
        } else {
            infoTextView.setText(null);
        }
    }

    public void setHasTextContent(boolean hasTextContent) {
        messageLayout.setVisibility(hasTextContent ? View.VISIBLE : View.GONE);
    }

    public void setImage(@Nullable Object imageUrl, boolean applyBottomCornerRadius) {
        Glide.with(this).clear(contentImageView);

        if (imageUrl == null) {
            clearImage();
            return;
        }

        if (!(imageUrl instanceof GlideUrl) && !(imageUrl instanceof String)) {
            Log.d(getClass().toString(), "setImage :: Detected invalid image url");
            imageUrl = null;
        }

        imageLayout.setVisibility(imageUrl == null ? View.GONE : View.VISIBLE);
        imageLoader.setVisibility(imageUrl == null ? View.GONE : View.VISIBLE);

        boolean isNameEmpty = nameTextView.getText().toString().isEmpty();
        renderImageShadows(isNameEmpty, applyBottomCornerRadius);

        contentImageView.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(imageUrl)
                .transform(getImageTransformations(applyBottomCornerRadius))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageLoader.setVisibility(View.GONE);
                        contentImagePlaceholderView.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageLoader.setVisibility(View.GONE);
                        contentImagePlaceholderView.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(contentImageView);
    }

    public void setFile(@Nullable Media media, boolean showDividerTop, boolean showDividerBottom) {
        fileView.render(media, showDividerTop, showDividerBottom);
    }

    private Transformation<Bitmap> getImageTransformations(boolean applyBottomCornerRadius) {
        List<Transformation<Bitmap>> transformations = new ArrayList<>();
        transformations.add(new CenterCrop()); // Always CenterCrop
        if (imageCornerRadius != 0) {
            int bottomCornerRadius = applyBottomCornerRadius ? imageCornerRadius : 0;
            transformations.add(new GranularRoundedCorners(imageCornerRadius, imageCornerRadius, bottomCornerRadius, bottomCornerRadius));
        }
        return new MultiTransformation<>(transformations);
    }

    private void renderImageShadows(boolean hideName, boolean applyBottomCornerRadius) {
        // Apply corner radius to shadow
        int bottomCornerRadius = applyBottomCornerRadius ? imageCornerRadius : 0;
        StyleUtil.Helper.applyCornerRadius((GradientDrawable) nameShadowView.getBackground().mutate(), imageCornerRadius, 0, 0, 0);
        StyleUtil.Helper.applyCornerRadius((GradientDrawable) infoShadowView.getBackground().mutate(), 0, 0, bottomCornerRadius, bottomCornerRadius);
        StyleUtil.Helper.applyCornerRadius((GradientDrawable) metaShadowView.getBackground().mutate(), 0, 0, bottomCornerRadius, 0);

        // Show / update them
        post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams nameLayoutParams = new FrameLayout.LayoutParams(nameShadowView.getLayoutParams());
                nameLayoutParams.width = nameTextView.getWidth() + StyleUtil.dpToPx(NAME_SHADOW_EXTRA_WIDTH);
                //noinspection SuspiciousNameCombination // It should be squared
                nameLayoutParams.height = nameLayoutParams.width;
                nameShadowView.setLayoutParams(nameLayoutParams);
            }
        });
        nameShadowView.setVisibility(hideName ? View.GONE : View.VISIBLE);
    }

    private void clearImage() {
        contentImageView.setVisibility(View.GONE);
        imageLoader.setVisibility(View.GONE);
        contentImagePlaceholderView.setVisibility(View.GONE);
        nameShadowView.setVisibility(View.GONE);
        metaShadowView.setVisibility(View.GONE);
    }

    public void setTime(@Nullable Date date) {
        timeTextView.setVisibility(date == null ? View.GONE : View.VISIBLE);
        if (date == null) {
            // Remove the meta shadow in case it was shown
            metaShadowView.setVisibility(View.GONE);
        } else {
            timeTextView.setText(formatTime(date));
        }
    }

    public void setLayoutGravity(int gravity) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getLayoutParams());
        params.gravity = gravity;
        this.setLayoutParams(params);
    }

    public void setStatus(int sendStatus) {
        switch (sendStatus) {
            case SEND_STATUS_PENDING:
                statusImageView.setImageResource(R.drawable.parley_ic_clock);
                break;
            case SEND_STATUS_SUCCESS:
                statusImageView.setImageResource(R.drawable.parley_ic_check);
                break;
            case SEND_STATUS_FAILED:
                statusImageView.setImageResource(R.drawable.parley_ic_close);
                break;
            default:
                throw new IllegalArgumentException("Unknown statues to render: " + sendStatus);
        }
    }

    public void setStatusVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        statusLayout.setVisibility(visibility);
        statusImageView.setVisibility(visibility);
    }

    public void setAddition(@Nullable final MessageAdditionAdapter adapter) {
        actionsRecyclerView.setAdapter(adapter);

        boolean hasAdditions = adapter != null && adapter.getItemCount() > 0;
        actionsRecyclerView.setVisibility(hasAdditions ? View.VISIBLE : View.GONE);

        if (adapter != null) {
            for (final Action action : adapter.getActions()) {
                ViewCompat.addAccessibilityAction(contentLayout, action.getTitle(), new AccessibilityViewCommand() {
                    @Override
                    public boolean perform(@NonNull View view, @Nullable CommandArguments arguments) {
                        adapter.onActionClicked(view, action);
                        return true;
                    }
                });
            }
        }
    }

    public void setTextMetaSpace(boolean visible) {
        messageMetaSpace.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setBottomMetaSpace(boolean visible) {
        actionsMetaSpace.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setOnContentClickListener(@Nullable View.OnClickListener clickListener) {
        contentLayout.setOnClickListener(clickListener);
        contentLayout.setClickable(clickListener != null);
    }

    // Styling

    public void refreshStyle(boolean isMetaOnImage) {
        timeTextView.setTextColor(isMetaOnImage ? imageTimeColor : messageTimeColor);
        ImageViewCompat.setImageTintList(statusImageView, isMetaOnImage ? imageStatusColor : messageStatusColor);

        metaShadowView.setVisibility(isMetaOnImage ? View.VISIBLE : View.GONE);
    }

    public void setNamePadding(StyleUtil.StyleSpacing data) {
        nameTextView.setPadding(data.left, data.top, data.right, data.bottom);
    }

    public void setNameColor(ColorStateList color) {
        nameTextView.setTextColor(color);
    }

    public void setNameFont(Typeface font, int style) {
        nameTextView.setTypeface(font, style);
    }

    public void setTitleFont(Typeface font, int style) {
        titleTextView.setTypeface(font, style);
    }

    public void setTextFont(Typeface font, int style) {
        messageTextView.setTypeface(font, style);
    }

    public void setTitleColor(ColorStateList color) {
        titleTextView.setTextColor(color);
    }

    public void setTextColor(ColorStateList color) {
        messageTextView.setTextColor(color);
    }

    public void setTintColor(ColorStateList color) {
        messageTextView.setLinkTextColor(color);
    }

    public void setTimeColor(ColorStateList messageTimeColor, ColorStateList imageTimeColor) {
        this.messageTimeColor = messageTimeColor;
        this.imageTimeColor = imageTimeColor;
        this.infoTextView.setTextColor(imageTimeColor);
    }

    public void setTimeFont(Typeface font, int style) {
        timeTextView.setTypeface(font, style);
        infoTextView.setTypeface(font, style);
    }

    public void setMessageStatusColor(ColorStateList color) {
        messageStatusColor = color;
    }

    public void setImageStatusColor(ColorStateList color) {
        imageStatusColor = color;
    }

    public void setMessageContentPadding(StyleUtil.StyleSpacing data) {
        StyleUtil.Helper.applySpacing(messageLayout, data);
    }

    public void setImageContentPadding(StyleUtil.StyleSpacing data) {
        StyleUtil.Helper.applySpacing(imageLayout, data);
    }

    public void setMetaPadding(StyleUtil.StyleSpacing data) {
        StyleUtil.Helper.applySpacing(metaLayout, data);
    }

    public void setImageCornerRadius(int radius) {
        imageCornerRadius = radius;
    }

    public void setImagePlaceholder(Drawable drawable) {
        contentImagePlaceholderView.setImageDrawable(drawable);
    }

    public void setImagePlaceholderTintColor(ColorStateList color) {
        ImageViewCompat.setImageTintList(contentImagePlaceholderView, color);
    }

    public void setImageLoadingTintColor(@ColorInt @Nullable Integer color) {
        if (color == null) {
            return;
        }
        StyleUtil.Helper.applyLoaderTint(imageLoader, color);
    }

    public void setNameTextSize(int complexUnit, int dimension) {
        nameTextView.setTextSize(complexUnit, dimension);
    }

    public void setTitleTextSize(int complexUnit, int dimension) {
        titleTextView.setTextSize(complexUnit, dimension);
    }

    public void setTextSize(int complexUnit, int dimension) {
        messageTextView.setTextSize(complexUnit, dimension);
    }

    public void setTimeTextSize(int complexUnit, int dimension) {
        timeTextView.setTextSize(complexUnit, dimension);
        infoTextView.setTextSize(complexUnit, dimension);
    }

    @Override
    public void setContentDescription(CharSequence contentDescription) {
        contentLayout.setContentDescription(contentDescription);
    }
}
