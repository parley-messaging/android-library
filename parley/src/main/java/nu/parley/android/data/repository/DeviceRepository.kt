package nu.parley.android.data.repository

import com.google.gson.Gson
import nu.parley.android.Parley
import nu.parley.android.data.model.Device
import nu.parley.android.data.net.ParleyHttpRequestMethod
import nu.parley.android.data.net.RepositoryCallback

final class DeviceRepository {

    fun register(callback: RepositoryCallback<Void>) {
        val parley = Parley.getInstance()
        val device = Device().apply {
            setPushToken(parley.pushToken, parley.pushType)
            setUserAdditionalInformation(parley.userAdditionalInformation)
            setReferrer(parley.referrer)
        }

        val network = parley.network
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
