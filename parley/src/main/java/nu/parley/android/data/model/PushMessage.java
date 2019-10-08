package nu.parley.android.data.model;

import com.google.gson.annotations.SerializedName;

public final class PushMessage {

    @SerializedName("id")
    private Integer id;

    @SerializedName("typeId")
    private Integer typeId;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    public Integer getId() {
        return id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}