package nu.parley.android.data.model;

public final class MockAction {

    public static Action create(String title, String payload) {
        return new Action(title, payload);
    }
}
