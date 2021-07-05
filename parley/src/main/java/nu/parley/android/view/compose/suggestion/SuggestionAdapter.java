package nu.parley.android.view.compose.suggestion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nu.parley.android.R;

public final class SuggestionAdapter extends RecyclerView.Adapter<SuggestionViewHolder> {

    private List<String> suggestions;
    private SuggestionListener listener;

    SuggestionAdapter(List<String> suggestions, SuggestionListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        return new SuggestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        final String suggestion = suggestions.get(position);
        holder.show(suggestion);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSuggestionClicked(suggestion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }
}
