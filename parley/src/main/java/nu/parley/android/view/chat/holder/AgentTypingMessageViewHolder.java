package nu.parley.android.view.chat.holder;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import nu.parley.android.R;
import nu.parley.android.data.model.Message;
import nu.parley.android.util.StyleUtil;

public final class AgentTypingMessageViewHolder extends ParleyBaseViewHolder {

    private final AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.parley_ic_is_typing_animation);
    private ViewGroup contentLayout;
    private ImageView imageView;

    public AgentTypingMessageViewHolder(View itemView) {
        super(itemView);

        contentLayout = itemView.findViewById(R.id.content_layout);
        imageView = itemView.findViewById(R.id.image_view);
        setupView();
        setupDotsAnimation();
        applyStyle();
    }

    private void setupView() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(contentLayout.getLayoutParams());
        params.gravity = Gravity.START;
        contentLayout.setLayoutParams(params);
    }

    private void setupDotsAnimation() {
        if (animatedVectorDrawableCompat == null) {
            Log.e("AgentTypingMessage", "Animation is null, rendering typing balloon without image.");
            return;
        }
        animatedVectorDrawableCompat.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                animatedVectorDrawableCompat.start();
            }
        });
        animatedVectorDrawableCompat.start();
        imageView.setImageDrawable(animatedVectorDrawableCompat);
    }

    private void applyStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(R.style.ParleyMessageAgentTypingStyle, R.styleable.ParleyMessageAgentTyping);

        contentLayout.setBackground(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyMessageAgentTyping_parley_background));
        StyleUtil.Helper.applyBackgroundColor(contentLayout, ta, R.styleable.ParleyMessageAgentTyping_parley_background_tint_color);

        StyleUtil.StyleSpacing styleSpacingMargin = StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageAgentTyping_parley_margin, R.styleable.ParleyMessageAgentTyping_parley_margin_top, R.styleable.ParleyMessageAgentTyping_parley_margin_right, R.styleable.ParleyMessageAgentTyping_parley_margin_bottom, R.styleable.ParleyMessageAgentTyping_parley_margin_left);
        itemView.setPadding(styleSpacingMargin.left, styleSpacingMargin.top, styleSpacingMargin.right, styleSpacingMargin.bottom);
        StyleUtil.StyleSpacing styleSpacingPadding = StyleUtil.getSpacingData(ta, R.styleable.ParleyMessageAgentTyping_parley_content_padding, R.styleable.ParleyMessageAgentTyping_parley_content_padding_top, R.styleable.ParleyMessageAgentTyping_parley_content_padding_right, R.styleable.ParleyMessageAgentTyping_parley_content_padding_bottom, R.styleable.ParleyMessageAgentTyping_parley_content_padding_left);
        contentLayout.setPadding(styleSpacingPadding.left, styleSpacingPadding.top, styleSpacingPadding.right, styleSpacingPadding.bottom);

        if (animatedVectorDrawableCompat != null) {
            animatedVectorDrawableCompat.setTintList(StyleUtil.getColorStateList(ta, R.styleable.ParleyMessageAgentTyping_parley_dot_color));
        }

        ta.recycle();
    }

    @Override
    public void show(Message message) {
        // Intentionally blank: we don't need to render anything on show
    }
}
