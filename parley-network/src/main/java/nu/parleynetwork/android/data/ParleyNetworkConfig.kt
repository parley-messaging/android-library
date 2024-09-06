package nu.parleynetwork.android.data

import nu.parley.android.NetworkConfig
import nu.parleynetwork.android.data.repository.ParleyNetworkRepositories
import okhttp3.Interceptor

class ParleyNetworkConfig(
    val interceptor: Interceptor? = null
) : NetworkConfig {
    override val repositories = ParleyNetworkRepositories
}