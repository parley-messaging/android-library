package nu.parley.android.view;

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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Date;

import nu.parley.android.R;
import nu.parley.android.util.MarkdownUtil;
import nu.parley.android.util.StyleUtil;

import static nu.parley.android.data.model.Message.SEND_STATUS_FAILED;
import static nu.parley.android.data.model.Message.SEND_STATUS_PENDING;
import static nu.parley.android.data.model.Message.SEND_STATUS_SUCCESS;
import static nu.parley.android.util.DateUtil.formatTime;

public final class BalloonView extends FrameLayout {

    public final static int NAME_SHADOW_EXTRA_WIDTH = 50;

    private TextView nameTextView;
    private TextView contentMessageTextView;
    private ViewGroup imageLayout;
    private ImageView contentImageView;
    private ProgressBar imageLoader;
    private View nameShadowView;
    private View metaShadowView;

    private ViewGroup metaLayout;
    private TextView timeTextView;
    private ViewGroup statusLayout;
    private AppCompatImageView statusImageView;

    // Styling
    private StyleUtil.StyleSpacing messageContentPadding;
    private StyleUtil.StyleSpacing messageMetaPadding;
    private StyleUtil.StyleSpacing imageContentPadding;
    private StyleUtil.StyleSpacing imageMetaPadding;
    private StyleUtil.StyleSpacing imageNamePadding;
    private int imageCornerRadius;
    private ColorStateList messageTimeColor;
    private ColorStateList messageStatusColor;
    private ColorStateList imageTimeColor;
    private ColorStateList imageStatusColor;
    private Drawable imagePlaceholder;

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

        nameTextView = findViewById(R.id.name_text_view);
        contentMessageTextView = findViewById(R.id.message_text_view);
        imageLayout = findViewById(R.id.image_layout);
        contentImageView = findViewById(R.id.image_view);
        imageLoader = findViewById(R.id.image_loader);
        nameShadowView = findViewById(R.id.name_shadow_view);
        metaShadowView = findViewById(R.id.meta_shadow_view);

        metaLayout = findViewById(R.id.meta_layout);
        timeTextView = findViewById(R.id.time_text_view);
        statusLayout = findViewById(R.id.status_layout);
        statusImageView = findViewById(R.id.status_image_view);

        contentMessageTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setName(@Nullable String text, boolean isImage) {
        if (text != null) {
            nameTextView.setText(text);

            if (isImage) {
                nameTextView.setPadding(imageNamePadding.left, imageNamePadding.top, imageNamePadding.right, imageNamePadding.bottom);
            } else {
                nameTextView.setPadding(0, 0, 0, 0);
            }
        }
        nameTextView.setVisibility(text != null ? View.VISIBLE : View.GONE);
    }

    public void setText(@Nullable String text) {
        contentMessageTextView.setVisibility(text != null ? View.VISIBLE : View.GONE);
        if (text != null) {
            contentMessageTextView.setText(MarkdownUtil.convert(getContext(), text));
        }
    }

    public void setImage(@Nullable Object imageUrl) {
        if (!(imageUrl instanceof GlideUrl) && !(imageUrl instanceof String)) {
            Log.d(getClass().toString(), "setImage :: Detected invalid image url");
            imageUrl = null;
        }

        imageLayout.setVisibility(imageUrl == null ? View.GONE : View.VISIBLE);
        imageLoader.setVisibility(imageUrl == null ? View.GONE : View.VISIBLE);

        boolean isNameEmpty = nameTextView.getText().toString().isEmpty();
        renderImageShadows(imageUrl == null, isNameEmpty);

        Glide.with(this).clear(contentImageView);

        if (imageUrl != null) {
            //noinspection Convert2Diamond // Preventing 'unchecked' warning when compiling
            Glide.with(this)
                    .load(imageUrl)
                    .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(imageCornerRadius)))
                    .placeholder(imagePlaceholder)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            imageLoader.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            imageLoader.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(contentImageView);
        }
    }

    public void clearImage() {
        imageLayout.setVisibility(View.GONE);
        imageLoader.setVisibility(View.GONE);
        setImagePlaceholder(imagePlaceholder);
    }

    private void renderImageShadows(boolean hideShadows, boolean hideName) {
        final int visibility = hideShadows ? View.GONE : View.VISIBLE;
        if (hideShadows) {
            // Hide the gradient stuff
        } else {
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
        }
        nameShadowView.setVisibility(hideName ? View.GONE : visibility);
        metaShadowView.setVisibility(visibility);
    }

    public void setTime(Date date) {
        timeTextView.setText(formatTime(date));
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

    // Styling

    public void refreshStyle(boolean isImage) {
        timeTextView.setTextColor(isImage ? imageTimeColor : messageTimeColor);
        statusImageView.setSupportImageTintList(isImage ? imageStatusColor : messageStatusColor);

        StyleUtil.Helper.applySpacing(this, isImage ? imageContentPadding : messageContentPadding);
        if (isImage) {
            contentImageView.setPadding(0, 0, imageContentPadding.right, 0);
        }
        StyleUtil.Helper.applySpacing(metaLayout, isImage ? imageMetaPadding : messageMetaPadding);
    }

    public void setImageNamePadding(StyleUtil.StyleSpacing data) {
        this.imageNamePadding = data;
    }

    public void setNameColor(ColorStateList color) {
        nameTextView.setTextColor(color);
    }

    public void setNameFont(Typeface font, int style) {
        nameTextView.setTypeface(font, style);
    }

    public void setTextFont(Typeface font, int style) {
        contentMessageTextView.setTypeface(font, style);
    }

    public void setTextColor(ColorStateList color) {
        contentMessageTextView.setTextColor(color);
    }

    public void setTintColor(ColorStateList color) {
        contentMessageTextView.setLinkTextColor(color);
    }

    public void setTimeColor(ColorStateList messageTimeColor, ColorStateList imageTimeColor) {
        this.messageTimeColor = messageTimeColor;
        this.imageTimeColor = imageTimeColor;
    }

    public void setTimeFont(Typeface font, int style) {
        timeTextView.setTypeface(font, style);
    }

    public void setMessageStatusColor(ColorStateList color) {
        messageStatusColor = color;
    }

    public void setImageStatusColor(ColorStateList color) {
        imageStatusColor = color;
    }

    public void setMessageContentPadding(StyleUtil.StyleSpacing data) {
        messageContentPadding = data;
    }

    public void setMessageMetaPadding(StyleUtil.StyleSpacing data) {
        messageMetaPadding = data;
    }

    public void setImageContentPadding(StyleUtil.StyleSpacing data) {
        imageContentPadding = data;
    }

    public void setImageMetaPadding(StyleUtil.StyleSpacing data) {
        imageMetaPadding = data;
    }

    public void setImageCornerRadius(int radius) {
        imageCornerRadius = radius;

        // Apply corner radius to shadow
        StyleUtil.Helper.applyCornerRadius((GradientDrawable) nameShadowView.getBackground(), imageCornerRadius, 0, 0, 0);
        StyleUtil.Helper.applyCornerRadius((GradientDrawable) metaShadowView.getBackground(), imageCornerRadius, 0, 0, 0);
    }

    public void setImagePlaceholder(Drawable drawable) {
        this.imagePlaceholder = drawable;
        contentImageView.setImageDrawable(drawable);
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

    public void setTextSize(int complexUnit, int dimension) {
        contentMessageTextView.setTextSize(complexUnit, dimension);
    }

    public void setTimeTextSize(int complexUnit, int dimension) {
        timeTextView.setTextSize(complexUnit, dimension);
    }
}
