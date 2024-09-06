package nu.parley.android.data.net.service

import nu.parley.android.data.model.Device
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

internal interface DeviceService {

    @POST("devices")
    fun register(@Body device: Device): Call<Void>
}
