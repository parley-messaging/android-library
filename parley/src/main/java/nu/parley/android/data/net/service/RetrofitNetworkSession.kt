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

class RetrofitNetworkSession(
    val interceptor: Interceptor? = null
) : ParleyNetworkSession {

    override fun request(
        url: String,
        data: String?,
        method: ParleyHttpRequestMethod,
        onCompletion: (String) -> Unit,
        onFailed: (Int, String) -> Unit
    ) {
        val networkService = Connectivity.getRetrofit().create(
            ParleyNetworkService::class.java
        )
        val retrofitCall = when (method) {
            ParleyHttpRequestMethod.Post -> {
                networkService.post(url, data)
            }

            ParleyHttpRequestMethod.Get -> {
                networkService.request(url)
            }

            ParleyHttpRequestMethod.Put -> {
                networkService.put(url, data)
            }
        }

        retrofitCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String?>) {
                if (response.isSuccessful) {
                    onCompletion(response.body()!!)
                } else {
                    onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onFailed(-1, t.message ?: "Unknown failure")
            }
        })
    }

    override fun upload(
        url: String,
        media: String,
        mimeType: String,
        formDataName: String,
        onCompletion: (String) -> Unit,
        onFailed: (Int, String) -> Unit
    ) {
        val file = File(media)
        val requestBody = RequestBody.create(MediaType.parse(fromValue(mimeType).value), file)
        val filePart = MultipartBody.Part.createFormData(formDataName, file.name, requestBody)

        upload(url, filePart, onCompletion, onFailed)
    }

    override fun upload(
        url: String,
        media: File,
        mimeType: String,
        formDataName: String,
        onCompletion: (String) -> Unit,
        onFailed: (Int, String) -> Unit
    ) {
        val requestBody = RequestBody.create(MediaType.parse(fromValue(mimeType).value), media)
        val filePart = MultipartBody.Part.createFormData(formDataName, media.name, requestBody)

        upload(url, filePart, onCompletion, onFailed)
    }

    private fun upload(
        url: String,
        filePart: MultipartBody.Part,
        onCompletion: (String) -> Unit,
        onFailed: (Int, String) -> Unit
    ) {
        val networkService = Connectivity.getRetrofit().create(
            ParleyNetworkService::class.java
        )
        val retrofitCall = networkService.upload(url, filePart)

        retrofitCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String?>) {
                if (response.isSuccessful) {
                    onCompletion(response.body()!!)
                } else {
                    onFailed(response.code(), Connectivity.getFormattedError(response))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onFailed(-1, t.message ?: "Unknown failure")
            }
        })
    }
}
