package nu.parley.android.data.repository

import nu.parley.android.Parley
import nu.parley.android.data.net.ParleyHttpRequestMethod

internal class EventRepository {
    fun fire(event: String?) {
        val network = Parley.getInstance().network
        network.networkSession.request(
            network.url + network.path + "services/event/$event",
            null,
            ParleyHttpRequestMethod.Post,
            onCompletion = {
                // Ignore
            },
            onFailed = { _, _ ->
                // Ignore
            }
        )
    }
}
