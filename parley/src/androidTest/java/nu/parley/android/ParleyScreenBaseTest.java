package nu.parley.android;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import java.util.ArrayList;
import java.util.List;

import nu.parley.android.util.TestActivity;
import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;

public abstract class ParleyScreenBaseTest<T extends View> {

    @Rule
    public ActivityScenarioRule<TestActivity> activityScenarioRule =
            new ActivityScenarioRule<>(TestActivity.class);

    static List<String> screenshots;
    static boolean takeScreenshots = true;

    private T view;

    abstract T createView(Activity activity);

    protected void getView(Listener<T> listener) {
        if (view == null) {
            activityScenarioRule.getScenario().onActivity(activity -> {
                view = createView(activity);
                activity.setContentView(view);
                view.post(() -> {
                    listener.callback(view);
                });
            });
        } else {
            view.post(() -> {
                listener.callback(view);
            });
        }
    }

    protected interface Listener<T> {
        void callback(T view);
    }

    @BeforeClass
    public static void setup() {
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
        screenshots = new ArrayList<>();
    }

    @Before
    public void setInitialState() {
        takeScreenshots = true;
    }

    void sleepForVisual(int duration) {
        try {
            sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted");
        }
    }

    void makeScreenshot(final String name) {
        if (!takeScreenshots) {
            return;
        }

        sleepForVisual(600);
        Screengrab.screenshot(name);
        if (screenshots.contains(name)) {
            throw new IllegalArgumentException("This screenshot name is already taken!");
        }
        screenshots.add(name);
    }
}
