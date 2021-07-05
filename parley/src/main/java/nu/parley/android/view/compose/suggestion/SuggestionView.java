package nu.parley.android.view.compose.suggestion;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nu.parley.android.R;

public final class SuggestionView extends FrameLayout {

    private RecyclerView recyclerView;
    private SuggestionListener listener;

    public SuggestionView(Context context) {
        super(context);
        init();
    }

    public SuggestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuggestionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_suggestion, this);

        recyclerView = findViewById(R.id.recycler_view);
    }

    public void setSuggestions(List<String> suggestions) {
        if (suggestions.isEmpty()) {
            clear();
        } else {
            recyclerView.setAdapter(new SuggestionAdapter(suggestions, listener));
        }
    }

    private void clear() {
        recyclerView.setAdapter(null);
    }

    public void setListener(SuggestionListener listener) {
        this.listener = listener;
    }
}
