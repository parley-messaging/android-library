package nu.parley.android.data.model;

import com.google.gson.annotations.SerializedName;

public enum ButtonType {
    @SerializedName("webUrl")
    WEB_URL,
    @SerializedName("phoneNumber")
    PHONE_NUMBER,
    @SerializedName("reply")
    REPLY
}
