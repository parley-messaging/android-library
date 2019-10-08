package nu.parley.android.data.model;

import com.google.gson.annotations.SerializedName;

public final class PushEventBody {

    @SerializedName("name")
    private String name;

    @SerializedName("body")
    private PushEvent body;

    public String getName() {
        return name;
    }
}
