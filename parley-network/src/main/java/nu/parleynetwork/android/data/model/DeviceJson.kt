package nu.parleynetwork.android.data.model

import com.bumptech.glide.Glide.init
import com.google.gson.annotations.SerializedName
import nu.parley.android.data.model.Device
import nu.parleynetwork.android.data.net.DeviceService

class DeviceJson(
    device: Device
) {

    @SerializedName("pushToken")
    private val pushToken: String?

    @SerializedName("pushType")
    private val pushType: Int

    @SerializedName("userAdditionalInformation")
    private val userAdditionalInformation: Map<String, String>?

    @SerializedName("referrer")
    private val referrer: String?

    init {
        pushToken = device.pushToken
        pushType = device.pushType
        userAdditionalInformation = device.userAdditionalInformation
        referrer = device.referrer
    }
}

