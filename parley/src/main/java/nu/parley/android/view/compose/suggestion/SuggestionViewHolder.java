package nu.parley.android.view.compose.suggestion;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import nu.parley.android.R;
import nu.parley.android.util.StyleUtil;

public final class SuggestionViewHolder extends RecyclerView.ViewHolder {

    private Context getContext() {
        return itemView.getContext();
    }

    private TextView titleTextView;

    SuggestionViewHolder(View itemView) {
        super(itemView);

        titleTextView = itemView.findViewById(R.id.title_text_view);
        applyStyle();
    }

    public void show(String suggestion) {
        titleTextView.setText(suggestion);
        titleTextView.post(new Runnable() {
            @Override
            public void run() {
                if (titleTextView.getLineCount() > 1) {
                    titleTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                } else {
                    titleTextView.setGravity(Gravity.CENTER);
                }
            }
        });

        String contentDescription = titleTextView.getText() + ". " + getContext().getString(R.string.parley_accessibility_button) + ".";
        titleTextView.setContentDescription(contentDescription);
    }

    private void applyStyle() {
        TypedArray ta = getContext().obtainStyledAttributes(R.style.ParleySuggestionViewStyle, R.styleable.ParleySuggestionView);

        titleTextView.setBackground(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyComposeView_parley_background));
        StyleUtil.Helper.applyBackgroundColor(titleTextView, ta, R.styleable.ParleyComposeView_parley_background_tint_color);

        StyleUtil.StyleSpacing suggestionSpaceData = StyleUtil.getSpacingData(ta, R.styleable.ParleySuggestionView_parley_margin, R.styleable.ParleySuggestionView_parley_margin_top, R.styleable.ParleySuggestionView_parley_margin_right, R.styleable.ParleySuggestionView_parley_margin_bottom, R.styleable.ParleySuggestionView_parley_margin_left);
        itemView.setPadding(suggestionSpaceData.left, suggestionSpaceData.top, suggestionSpaceData.right, suggestionSpaceData.bottom);
        StyleUtil.StyleSpacing titleSpaceData = StyleUtil.getSpacingData(ta, R.styleable.ParleySuggestionView_parley_content_padding, R.styleable.ParleySuggestionView_parley_content_padding_top, R.styleable.ParleySuggestionView_parley_content_padding_right, R.styleable.ParleySuggestionView_parley_content_padding_bottom, R.styleable.ParleySuggestionView_parley_content_padding_left);
        titleTextView.setPadding(titleSpaceData.left, titleSpaceData.top, titleSpaceData.right, titleSpaceData.bottom);

        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleySuggestionView_parley_text_size));
        titleTextView.setTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleySuggestionView_parley_text_color));

        // Font
        Typeface font = StyleUtil.getFont(getContext(), ta, R.styleable.ParleySuggestionView_parley_font_family);
        int fontStyle = StyleUtil.getFontStyle(ta, R.styleable.ParleySuggestionView_parley_font_style);
        titleTextView.setTypeface(font, fontStyle);

        ta.recycle();
    }
}
