package nu.parley.android

import nu.parley.android.data.repository.DefaultParleyRepositories
import okhttp3.Interceptor

/**
 * Use the default network config, optionally with a custom okhttp interceptor
 *
 * @param interceptor an optional interceptor
 */
class DefaultNetworkConfig(
    val interceptor: Interceptor? = null
) : NetworkConfig {
    override val repositories = DefaultParleyRepositories
}