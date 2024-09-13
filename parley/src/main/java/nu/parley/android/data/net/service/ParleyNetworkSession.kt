package nu.parley.android.data.net.service

import nu.parley.android.data.net.ParleyHttpRequestMethod
import java.io.File

interface ParleyNetworkSession {

    fun request(
        url: String,
        data: String?,
        method: ParleyHttpRequestMethod,
        onCompetion: (String) -> Unit,
        onFailed: (Int?, String?) -> Unit
    )

    fun upload(
        url: String,
        media: String,
        mimeType: String,
        formDataName: String,
        onCompetion: (String) -> Unit,
        onFailed: (Int?, String?) -> Unit
    )

    fun upload(
        url: String,
        media: File,
        mimeType: String,
        formDataName: String,
        onCompetion: (String) -> Unit,
        onFailed: (Int?, String?) -> Unit
    )
}