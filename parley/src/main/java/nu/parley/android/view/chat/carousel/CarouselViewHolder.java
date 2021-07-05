package nu.parley.android.view.chat.carousel;

import android.content.res.TypedArray;
import android.view.View;

import nu.parley.android.R;
import nu.parley.android.util.StyleUtil;
import nu.parley.android.view.chat.MessageListener;
import nu.parley.android.view.chat.holder.AgentMessageViewHolder;

public final class CarouselViewHolder extends AgentMessageViewHolder {

    private static final int CAROUSEL_ITEM_WITDH_DP = 250;

    CarouselViewHolder(View itemView, MessageListener listener) {
        super(itemView, listener);
        applyStyle();
    }

    @Override
    protected int getStyleTheme() {
        return R.style.ParleyMessageAgentCarouselStyle;
    }

    private void applyStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(getStyleTheme(), R.styleable.ParleyMessageCarousel);

        itemView.getLayoutParams().width = StyleUtil.dpToPx(CAROUSEL_ITEM_WITDH_DP);

        ta.recycle();
    }
}
