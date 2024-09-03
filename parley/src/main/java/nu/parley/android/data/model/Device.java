package nu.parley.android.data.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public final class Device {

    @SerializedName("pushToken")
    private String pushToken;

    @SerializedName("pushType")
    private Integer pushType = 6;

    @SerializedName("userAdditionalInformation")
    private Map<String, String> userAdditionalInformation;

    @Nullable
    @SerializedName("referrer")
    private String referrer;

    public void setPushToken(String pushToken, PushType pushType) {
        this.pushToken = pushToken;
        this.pushType = pushType.value;
    }

    public void setUserAdditionalInformation(Map<String, String> userAdditionalInformation) {
        this.userAdditionalInformation = userAdditionalInformation;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }
}
