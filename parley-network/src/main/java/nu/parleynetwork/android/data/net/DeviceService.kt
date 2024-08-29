package nu.parleynetwork.android.data.net

import nu.parleynetwork.android.data.model.DeviceJson
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

internal interface DeviceService {
    @POST("devices")
    fun register(@Body device: DeviceJson): Call<Void>
}
