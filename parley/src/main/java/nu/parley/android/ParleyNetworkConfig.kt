package nu.parley.android

import nu.parley.android.data.repository.DefaultParleyRepositories
import okhttp3.Interceptor

class ParleyNetworkConfig(
    val interceptor: Interceptor? = null
) : NetworkConfig {
    override val repositories = DefaultParleyRepositories
}