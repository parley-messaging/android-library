package nu.parley.android.view.chat.holder;

import android.content.res.TypedArray;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import nu.parley.android.R;
import nu.parley.android.data.model.Message;
import nu.parley.android.util.AccessibilityUtil;
import nu.parley.android.util.StyleUtil;

public final class LoaderViewHolder extends ParleyBaseViewHolder {

    private ProgressBar loader;

    public LoaderViewHolder(View itemView) {
        super(itemView);

        loader = itemView.findViewById(R.id.loader);
        applyStyle();
    }

    private void applyStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(R.style.ParleyMessageLoadingStyle, R.styleable.ParleyLoadingMessage);

        StyleUtil.StyleSpacing spaceData = StyleUtil.getSpacingData(ta, R.styleable.ParleyLoadingMessage_parley_content_padding, R.styleable.ParleyLoadingMessage_parley_content_padding_top, R.styleable.ParleyLoadingMessage_parley_content_padding_right, R.styleable.ParleyLoadingMessage_parley_content_padding_bottom, R.styleable.ParleyLoadingMessage_parley_content_padding_left);
        itemView.setPadding(spaceData.left, spaceData.top, spaceData.right, spaceData.bottom);

        @ColorInt @Nullable Integer loaderTintColor = StyleUtil.getColor(ta, R.styleable.ParleyLoadingMessage_parley_tint_color);
        if (loaderTintColor != null) {
            StyleUtil.Helper.applyLoaderTint(loader, loaderTintColor);
        }

        ta.recycle();
    }

    @Override
    public void show(Message message) {
        // Accessibility
        itemView.setContentDescription(AccessibilityUtil.getContentDescription(itemView, message));
    }
}
