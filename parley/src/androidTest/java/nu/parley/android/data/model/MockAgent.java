package nu.parley.android.data.model;

import java.util.Random;

public final class MockAgent {

    private static int generateRandomId() {
        return new Random().nextInt();
    }

    private static Agent create(String name) {
        return new Agent(name);
    }

    public static Agent Webuildapps = create("Webuildapps");
}
