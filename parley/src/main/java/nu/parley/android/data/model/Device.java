package nu.parley.android.data.model;

import androidx.annotation.Nullable;

import java.util.Map;

public final class Device {

    public Device() {
    }

    public Device(
            String pushToken,
            Integer pushType,
            Map<String, String> userAdditionalInformation,
            @Nullable String referrer
    ) {
        this.pushToken = pushToken;
        this.pushType = pushType;
        this.userAdditionalInformation = userAdditionalInformation;
        this.referrer = referrer;
    }

    public String pushToken;

    public Integer pushType = 6;

    public Map<String, String> userAdditionalInformation;

    @Nullable
    public String referrer;

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
