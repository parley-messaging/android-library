package nu.parley.android;

import android.util.Log;
import android.widget.FrameLayout;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.novoda.espresso.ViewTestRule;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import nu.parley.android.view.compose.suggestion.SuggestionView;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ParleySuggestionViewTest extends ParleyScreenBaseTest {

    @Rule
    public ViewTestRule<FrameLayout> rule = new ViewTestRule<>(nu.parley.android.test.R.layout.view_suggestions_test);

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
        rule.runOnMainSynchronously(new ViewTestRule.Runner<FrameLayout>() {
            @Override
            public void run(FrameLayout view) {
                SuggestionView suggestionView = rule.getView().findViewById(R.id.suggestion_view);
                suggestionView.setSuggestions(suggestions);
            }
        });
//        sleepForVisual(0);
//        sleepForVisual(1000);
//        sleepForVisual(5000);
//        sleepForVisual(60000);
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
