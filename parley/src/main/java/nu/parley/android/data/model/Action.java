package nu.parley.android.data.model;

import java.util.Date;

public final class Action {

    private String title;

    private String payload;

    private ButtonType type;

    public Action(String title, String payload) {
        this.title = title;
        this.payload = payload;
    }

    public Action(String title, String payload, ButtonType type) {
        this.title = title;
        this.payload = payload;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getPayload() {
        return payload;
    }

    public ButtonType getType() {
        return type;
    }
}