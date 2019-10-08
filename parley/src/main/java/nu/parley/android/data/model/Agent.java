package nu.parley.android.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public final class Agent {

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("isTyping")
    private long isTyping;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public Date getIsTyping() {
        return new Date(isTyping * 1000);
    }
}