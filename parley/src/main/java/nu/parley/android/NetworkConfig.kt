package nu.parley.android

import nu.parley.android.data.net.ParleyJsonParser
import nu.parley.android.data.net.ParleyRepositories

interface NetworkConfig {
    val repositories: ParleyRepositories
}