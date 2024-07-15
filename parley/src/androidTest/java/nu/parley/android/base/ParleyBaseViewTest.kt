package nu.parley.android.base

import android.app.Activity
import android.view.View
import androidx.test.ext.junit.rules.ActivityScenarioRule
import nu.parley.android.util.TestActivity
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy

abstract class ParleyBaseViewTest<T : View> {
    @JvmField
    @Rule
    var activityScenarioRule: ActivityScenarioRule<TestActivity> = ActivityScenarioRule(
        TestActivity::class.java
    )

    private var view: T? = null

    abstract fun createView(activity: Activity): T

    protected fun getView(callback: (T) -> Unit) {
        if (view == null) {
            activityScenarioRule.scenario.onActivity { activity: TestActivity ->
                view = createView(activity)
                activity.setContentView(view)
                view!!.post {
                    callback(requireNotNull(view))
                }
            }
        } else {
            view!!.post {
                callback(requireNotNull(view))
            }
        }
    }

    @Before
    fun setInitialState() {
        takeScreenshots = true
    }

    fun sleepForVisual(duration: Int) {
        try {
            Thread.sleep(duration.toLong())
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted")
        }
    }

    fun makeScreenshot(name: String) {
        if (!takeScreenshots) {
            return
        }

        sleepForVisual(600)
        Screengrab.screenshot(name)
        require(!screenshots!!.contains(name)) { "This screenshot name is already taken!" }
        screenshots!!.add(name)
    }

    companion object {
        var screenshots = mutableListOf<String>()
        var takeScreenshots = true

        @JvmStatic
        @BeforeClass
        fun setup() {
            Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
            screenshots = ArrayList()
        }
    }
}
