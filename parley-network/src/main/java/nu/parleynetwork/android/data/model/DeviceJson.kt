package nu.parleynetwork.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.model.Device
import nu.parleynetwork.android.data.net.DeviceService

class DeviceJson(
    @SerializedName("pushToken")
    private val pushToken: String? = null,
    @SerializedName("pushType")
    private val pushType: Int = 6,
    @SerializedName("userAdditionalInformation")
    private val userAdditionalInformation: Map<String, String>? = null,
    @SerializedName("referrer")
    private val referrer: String? = null,
) {

    companion object {
        fun fromModel(device: Device) = with(device) {
            DeviceJson(pushToken, pushType, userAdditionalInformation, referrer)
        }
    }
}

