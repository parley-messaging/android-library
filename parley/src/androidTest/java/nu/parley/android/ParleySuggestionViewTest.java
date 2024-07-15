package nu.parley.android;

import android.app.Activity;
import android.util.Log;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import nu.parley.android.view.compose.suggestion.SuggestionView;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ParleySuggestionViewTest extends ParleyScreenBaseTest<SuggestionView> {

    @Override
    SuggestionView createView(Activity activity) {
        return new SuggestionView(activity);
    }

    @Test
    public void suggestions_small_shouldAlignRight() {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Yes");
        suggestions.add("No");

        renderSuggestions(suggestions);
        makeScreenshot("Suggestions-Small");
    }

    @Test
    public void suggestions_big_shouldScroll() {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Accept");
        suggestions.add("Decline");
        suggestions.add("Maybe later");
        suggestions.add("Cancel");
        suggestions.add("More information");

        renderSuggestions(suggestions);
        makeScreenshot("Suggestions-Full");
    }

    @Test
    public void suggestions_multiline() {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("What meassures are taken for secure messaging?");
        suggestions.add("What are the possible styling options when integrating Parley?");

        renderSuggestions(suggestions);
        makeScreenshot("Suggestions-Multiline");
    }

    @Test
    public void suggestions_differentSizes() {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("I would like more information before making a choice");
        suggestions.add("Yes");
        suggestions.add("No");

        renderSuggestions(suggestions);
        makeScreenshot("Suggestions-DifferentSizes");
    }

    private void renderSuggestions(final List<String> suggestions) {
        getView(view -> view.setSuggestions(suggestions));
    }

    @AfterClass
    public static void tearDown() {
        // Make a report doc
        StringBuilder markdownText = new StringBuilder("\n" +
                "Current | Updated\n" +
                "-- | --\n");
        for (String screenshot : screenshots) {
            markdownText.append("![Current](Current/")
                    .append(screenshot)
                    .append(".png) | ![Updated](Update/")
                    .append(screenshot)
                    .append(".png)\n");
        }
        Log.d("diff", markdownText.toString());
    }
}
