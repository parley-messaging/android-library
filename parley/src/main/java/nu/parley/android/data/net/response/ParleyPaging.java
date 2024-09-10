package nu.parley.android.data.net.response;

import com.google.gson.annotations.SerializedName;

public final class ParleyPaging {

    @SerializedName("before")
    private String before;

    @SerializedName("after")
    private String after;

    public String getBefore() {
        if (before == null || before.length() < 1) return null;

        return before.substring(1);
    }

    public String getAfter() {
        if (after == null || after.length() < 1) return null;

        return after.substring(1);
    }

}
