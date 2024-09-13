package nu.parley.android.data.repository

import com.google.gson.Gson
import nu.parley.android.Parley
import nu.parley.android.data.model.Device
import nu.parley.android.data.net.ParleyHttpRequestMethod
import nu.parley.android.data.net.RepositoryCallback
import nu.parley.android.data.net.service.DefaultNetworkSession

class DefaultDeviceRepository : DeviceRepository {
    public override fun register(device: Device, callback: RepositoryCallback<Void>) {
        val network = Parley.getInstance().network
        network.networkSession.request(
            network.url + network.path + "devices",
            Gson().toJson(device),
            ParleyHttpRequestMethod.Post,
            emptyMap(),
            onCompetion = {
                callback.onSuccess(null)
            },
            onFailed = { statusCode, message ->
                callback.onFailed(statusCode, message)
            }
        )
    }
}
