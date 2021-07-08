package nu.parley.android.data.model;

public enum PushType {
    CUSTOM_WEBHOOK(4),
    CUSTOM_WEBHOOK_BEHIND_OAUTH(5),
    FCM(6);

    final int value;

    PushType(int value) {
        this.value = value;
    }
}
