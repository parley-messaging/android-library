package nu.parleynetwork.android.data.repository

import nu.parley.android.data.model.Device
import nu.parley.android.data.net.Connectivity
import nu.parley.android.data.net.RepositoryCallback
import nu.parley.android.data.net.service.DeviceService
import nu.parley.android.data.repository.DeviceRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeviceRepositoryImpl : DeviceRepository {
    public override fun register(device: Device, callback: RepositoryCallback<Void>) {
        var deviceService = Connectivity.getRetrofit().create(
            DeviceService::class.java
        )
        var registerCall = deviceService.register(device)
        registerCall.enqueue(object : Callback<Void?> {
            public override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.isSuccessful) {
                    callback.onSuccess(null)
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            public override fun onFailure(call: Call<Void?>, t: Throwable) {
                t.printStackTrace()
                callback.onFailed(null, t.message)
            }
        })
    }
}
