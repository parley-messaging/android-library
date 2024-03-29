package nu.parley.android.view;

import android.Manifest;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;

import nu.parley.android.R;
import nu.parley.android.util.ParleyPermissionUtil;
import nu.parley.android.util.StyleUtil;

public final class ParleyNotificationView extends FrameLayout {

    private View connectionLayout;
    private AppCompatImageView connectionIcon;
    private TextView connectionText;
    private View notificationsLayout;
    private AppCompatImageView notificationsIcon;
    private TextView notificationsText;

    private boolean connectionActive = true; // Whether Parley determines this view to show.
    private boolean notificationsActive = true; // Whether Parley determines this view to show.
    private boolean connectionShow = true; // Whether Styling wants to show this view.
    private boolean notificationsShow = true; // Whether Styling wants to show this view.

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

        connectionLayout = findViewById(R.id.connection_layout);
        connectionIcon = findViewById(R.id.connection_icon);
        connectionText = findViewById(R.id.connection_text);
        notificationsLayout = findViewById(R.id.notifications_layout);
        notificationsIcon = findViewById(R.id.notifications_icon);
        notificationsText = findViewById(R.id.notifications_text);

        applyStyle(context, attrs);
    }

    private void applyStyle(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(R.style.ParleyNotificationViewStyle, R.styleable.ParleyNotificationView);

            connectionShow = StyleUtil.getBoolean(ta, R.styleable.ParleyNotificationView_parley_show_connection, connectionShow);
            notificationsShow = StyleUtil.getBoolean(ta, R.styleable.ParleyNotificationView_parley_show_notifications, notificationsShow);
            renderVisibilityConnection();
            renderVisibilityNotifications();

            setBackground(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyNotificationView_parley_background));
            StyleUtil.Helper.applyBackgroundColor(this, ta, R.styleable.ParleyNotificationView_parley_background_tint_color);

            StyleUtil.StyleSpacing styleSpacingPadding = StyleUtil.getSpacingData(ta, R.styleable.ParleyNotificationView_parley_content_padding, R.styleable.ParleyNotificationView_parley_content_padding_top, R.styleable.ParleyNotificationView_parley_content_padding_right, R.styleable.ParleyNotificationView_parley_content_padding_bottom, R.styleable.ParleyNotificationView_parley_content_padding_left);
            connectionLayout.setPadding(styleSpacingPadding.left, styleSpacingPadding.top, styleSpacingPadding.right, styleSpacingPadding.bottom);
            notificationsLayout.setPadding(styleSpacingPadding.left, styleSpacingPadding.top, styleSpacingPadding.right, styleSpacingPadding.bottom);

            connectionIcon.setImageDrawable(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyNotificationView_parley_icon_connection));
            notificationsIcon.setImageDrawable(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyNotificationView_parley_icon_notifications));
            ColorStateList iconTint = StyleUtil.getColorStateList(ta, R.styleable.ParleyNotificationView_parley_icon_tint_color);
            ImageViewCompat.setImageTintList(connectionIcon, iconTint);
            ImageViewCompat.setImageTintList(notificationsIcon, iconTint);

            Typeface font = StyleUtil.getFont(getContext(), ta, R.styleable.ParleyNotificationView_parley_font_family);
            int fontStyle = StyleUtil.getFontStyle(ta, R.styleable.ParleyNotificationView_parley_font_style);
            connectionText.setTypeface(font, fontStyle);
            notificationsText.setTypeface(font, fontStyle);
            float textSize = StyleUtil.getDimension(ta, R.styleable.ParleyNotificationView_parley_text_size);
            connectionText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            notificationsText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            ColorStateList textColor = StyleUtil.getColorStateList(ta, R.styleable.ParleyNotificationView_parley_text_color);
            connectionText.setTextColor(textColor);
            notificationsText.setTextColor(textColor);

            ta.recycle();
        }
    }

    public void setOnline(boolean online) {
        connectionActive = !online;
        renderVisibilityConnection();
    }

    private void setNotifications(boolean enabled) {
        notificationsActive = !enabled;
        renderVisibilityNotifications();
    }

    private void renderVisibilityConnection() {
        connectionLayout.setVisibility(connectionActive && connectionShow ? View.VISIBLE : View.GONE);
    }

    private void renderVisibilityNotifications() {
        notificationsLayout.setVisibility(notificationsActive && notificationsShow ? View.VISIBLE : View.GONE);
    }

    public void checkNotifications() {
        if (Build.VERSION.SDK_INT >= 33) {
            setNotifications(ParleyPermissionUtil.hasPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS));
        } else {
            setNotifications(true);
        }
    }

    public int getVisibleHeight() {
        int height = 0;
        if (connectionLayout.getVisibility() == View.VISIBLE) {
            height += connectionLayout.getHeight();
        }
        if (notificationsLayout.getVisibility() == View.VISIBLE) {
            height += notificationsLayout.getHeight();
        }
        return height;
    }
}
