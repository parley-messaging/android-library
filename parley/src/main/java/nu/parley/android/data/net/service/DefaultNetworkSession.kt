package nu.parley.android.data.net.service

import nu.parley.android.data.model.MimeType.Companion.fromValue
import nu.parley.android.data.net.Connectivity
import nu.parley.android.data.net.ParleyHttpRequestMethod
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DefaultNetworkSession(
    val interceptor: Interceptor? = null
) : ParleyNetworkSession {

    override fun request(
        url: String,
        data: String?,
        method: ParleyHttpRequestMethod,
        headers: Map<String, String>,
        onCompetion: (String) -> Unit,
        onFailed: (Int?, String?) -> Unit
    ) {
        var networkService = Connectivity.getRetrofit().create(
            ParleyNetworkService::class.java
        )
        var retrofitCall = when (method) {
            ParleyHttpRequestMethod.Post -> {
                networkService.post(url, data!!)
            }
            ParleyHttpRequestMethod.Get -> {
                networkService.request(url)
            }
        }

        retrofitCall.enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String?>) {
                if (response.isSuccessful) {
                    onCompetion(response.body()!!)
                } else {
                    onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onFailed(null, t.message)
            }
        })
    }

    override fun upload(
        url: String,
        media: String,
        mimeType: String,
        formDataName: String,
        headers: Map<String, String>,
        onCompetion: (String) -> Unit,
        onFailed: (Int?, String?) -> Unit
    ) {
        val file = File(media)
        val requestBody = RequestBody.create(MediaType.parse(fromValue(mimeType).value), file)
        val filePart = MultipartBody.Part.createFormData(formDataName, file.name, requestBody)

        upload(url, filePart, onCompetion, onFailed)
    }

    override fun upload(
        url: String,
        media: File,
        mimeType: String,
        formDataName: String,
        headers: Map<String, String>,
        onCompetion: (String) -> Unit,
        onFailed: (Int?, String?) -> Unit
    ) {
        val requestBody = RequestBody.create(MediaType.parse(fromValue(mimeType).value), media)
        val filePart = MultipartBody.Part.createFormData(formDataName, media.name, requestBody)

        upload(url, filePart, onCompetion, onFailed)
    }

    private fun upload(
        url: String,
        filePart: MultipartBody.Part,
        onCompetion: (String) -> Unit,
        onFailed: (Int?, String?) -> Unit
    ) {
        val networkService = Connectivity.getRetrofit().create(
            ParleyNetworkService::class.java
        )
        val retrofitCall = networkService.upload(url, filePart)

        retrofitCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String?>) {
                if (response.isSuccessful) {
                    onCompetion(response.body()!!)
                } else {
                    onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onFailed(null, t.message)
            }
        })
    }
}