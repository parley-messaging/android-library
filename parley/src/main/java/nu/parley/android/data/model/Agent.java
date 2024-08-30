package nu.parley.android.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public final class Agent {

    private Integer id;

    private String name;

    private String avatar;

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

    public Agent(String name) {
        this(name, null);
    }

    Agent(String name, String avatarUrl) {
        this.name = name;
        this.avatar = avatarUrl;
    }

    public Agent(Integer id, String name, long isTyping, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.isTyping = isTyping;
        this.avatar = avatarUrl;
    }
}