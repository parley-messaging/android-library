package nu.parley.android;

import org.junit.Before;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;

import static java.lang.Thread.sleep;

public abstract class ParleyScreenBaseTest {

    static List<String> screenshots;
    static boolean takeScreenshots = true;

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
