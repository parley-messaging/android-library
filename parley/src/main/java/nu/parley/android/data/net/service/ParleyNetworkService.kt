package nu.parley.android.data.net.service

import nu.parley.android.data.net.response.ParleyResponse
import nu.parley.android.data.net.response.ParleyResponsePostMedia
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url

interface ParleyNetworkService {

    @GET
    fun request(@Url url: String): Call<String>

    @POST
    fun post(@Url url: String, @Body body: String): Call<String>

    @Multipart
    @POST
    fun upload(@Url url: String, @Part filePart: MultipartBody.Part): Call<String>
}