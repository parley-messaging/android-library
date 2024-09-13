package nu.parley.android.data.repository

import nu.parley.android.Parley
import nu.parley.android.data.net.ParleyHttpRequestMethod

final class EventRepository {
    fun fire(event: String?) {
        val network = Parley.getInstance().network
        network.networkSession.request(
            network.url + network.path + "services/event/$event",
            "",
            ParleyHttpRequestMethod.Post,
            onCompetion = {
                // Ignore
            },
            onFailed = { _, _ ->
                // Ignore
            }
        )
    }
}
