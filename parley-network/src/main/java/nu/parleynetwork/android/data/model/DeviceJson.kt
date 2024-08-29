package nu.parleynetwork.android.data.model

import com.google.gson.annotations.SerializedName
import nu.parley.android.data.model.Device

class DeviceJson {
    @SerializedName("pushToken")
    private val pushToken: String? = null

    @SerializedName("pushType")
    private val pushType = 6

    @SerializedName("userAdditionalInformation")
    private val userAdditionalInformation: Map<String, String>? = null

    @SerializedName("referrer")
    private val referrer: String? = null

    fun toModel(): Device {
        return Device(pushToken, pushType, userAdditionalInformation, referrer)
    }
}

