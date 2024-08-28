package nu.parleynetwork.android.data.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import nu.parley.android.data.model.Device;

public final class DeviceJson {

    @SerializedName("pushToken")
    private String pushToken;

    @SerializedName("pushType")
    private Integer pushType = 6;

    @SerializedName("userAdditionalInformation")
    private Map<String, String> userAdditionalInformation;

    @Nullable
    @SerializedName("referrer")
    private String referrer;

    public Device toModel() {
        return new Device(pushToken, pushType, userAdditionalInformation, referrer);
    }
}

