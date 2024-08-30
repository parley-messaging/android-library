package nu.parley.android.data.net.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import nu.parley.android.data.model.Agent;

public final class ParleyResponse<T> {

    public ParleyResponse(T data, @Nullable Agent agent, ParleyPaging paging, @Nullable String stickyMessage, @Nullable String welcomeMessage) {
        this.data = data;
        this.agent = agent;
        this.paging = paging;
        this.stickyMessage = stickyMessage;
        this.welcomeMessage = welcomeMessage;
    }

    @SerializedName("data")
    private T data;

    @SerializedName("agent")
    @Nullable
    private Agent agent;

    @SerializedName("paging")
    private ParleyPaging paging;

    @SerializedName("stickyMessage")
    @Nullable
    private String stickyMessage;

    @SerializedName("welcomeMessage")
    @Nullable
    private String welcomeMessage;

    public static boolean isOfflineErrorCode(Integer responseCode) {
        return responseCode == null;
    }

    public T getData() {
        return data;
    }

    @Nullable
    public Agent getAgent() {
        return agent;
    }

    public ParleyPaging getPaging() {
        return paging;
    }

    @Nullable
    public String getStickyMessage() {
        return stickyMessage;
    }

    @Nullable
    public String getWelcomeMessage() {
        return welcomeMessage;
    }
}
