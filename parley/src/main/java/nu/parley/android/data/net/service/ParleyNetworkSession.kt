package nu.parley.android.data.net.service

import nu.parley.android.data.model.Message
import nu.parley.android.data.net.ParleyHttpRequestMethod
import nu.parley.android.data.net.response.ParleyErrorResponse
import nu.parley.android.data.net.response.ParleyHttpDataResponse
import nu.parley.android.data.net.response.ParleyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import java.io.File

interface ParleyNetworkSession {

    fun request(
        url: String,
        data: String?,
        method: ParleyHttpRequestMethod,
        // TODO: Do we need headers?
        headers: Map<String, String>,
        onCompetion: (String) -> Unit,
        onFailed: (Int?, String?) -> Unit
    )

    fun upload(
        url: String,
        media: String,
        mimeType: String,
        formDataName: String,
        headers: Map<String, String>,
        onCompetion: (String) -> Unit,
        onFailed: (Int?, String?) -> Unit
    )

    fun upload(
        url: String,
        media: File,
        mimeType: String,
        formDataName: String,
        headers: Map<String, String>,
        onCompetion: (String) -> Unit,
        onFailed: (Int?, String?) -> Unit
    )
}