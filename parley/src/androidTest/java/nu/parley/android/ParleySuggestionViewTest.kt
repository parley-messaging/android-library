package nu.parley.android

import android.app.Activity
import android.util.Log
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import nu.parley.android.base.ParleyBaseViewTest
import nu.parley.android.view.compose.suggestion.SuggestionView
import org.junit.AfterClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ParleySuggestionViewTest : ParleyBaseViewTest<SuggestionView>() {
    override fun createView(activity: Activity): SuggestionView {
        return SuggestionView(activity)
    }

    @Test
    fun suggestions_small_shouldAlignRight() {
        val suggestions: MutableList<String> = ArrayList()
        suggestions.add("Yes")
        suggestions.add("No")

        renderSuggestions(suggestions)
        makeScreenshot("Suggestions-Small")
    }

    @Test
    fun suggestions_big_shouldScroll() {
        val suggestions: MutableList<String> = ArrayList()
        suggestions.add("Accept")
        suggestions.add("Decline")
        suggestions.add("Maybe later")
        suggestions.add("Cancel")
        suggestions.add("More information")

        renderSuggestions(suggestions)
        makeScreenshot("Suggestions-Full")
    }

    @Test
    fun suggestions_multiline() {
        val suggestions: MutableList<String> = ArrayList()
        suggestions.add("What meassures are taken for secure messaging?")
        suggestions.add("What are the possible styling options when integrating Parley?")

        renderSuggestions(suggestions)
        makeScreenshot("Suggestions-Multiline")
    }

    @Test
    fun suggestions_differentSizes() {
        val suggestions: MutableList<String> = ArrayList()
        suggestions.add("I would like more information before making a choice")
        suggestions.add("Yes")
        suggestions.add("No")

        renderSuggestions(suggestions)
        makeScreenshot("Suggestions-DifferentSizes")
    }

    private fun renderSuggestions(suggestions: List<String>) {
        getView { view -> view.setSuggestions(suggestions) }
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun tearDown() {
            // Make a report doc
            val markdownText = StringBuilder(
                """
    
    Current | Updated
    -- | --
    
    """.trimIndent()
            )
            for (screenshot in screenshots) {
                markdownText.append("![Current](Current/")
                    .append(screenshot)
                    .append(".png) | ![Updated](Update/")
                    .append(screenshot)
                    .append(".png)\n")
            }
            Log.d("diff", markdownText.toString())
        }
    }
}
