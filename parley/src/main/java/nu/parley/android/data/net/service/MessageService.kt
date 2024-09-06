package nu.parley.android.data.net.service

import nu.parley.android.data.model.Message
import nu.parley.android.data.net.response.ParleyResponse
import nu.parley.android.data.net.response.ParleyResponsePostMedia
import nu.parley.android.data.net.response.ParleyResponsePostMessage
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

internal interface MessageService {

    @GET("messages/{id}")
    fun get(@Path("id") id: Int): Call<ParleyResponse<Message>>

    @GET("messages")
    fun findAll(): Call<ParleyResponse<List<Message>>>

    @GET
    fun getOlder(@Url url: String): Call<ParleyResponse<List<Message>>>

    @POST("messages")
    fun post(@Body chatMessage: Message): Call<ParleyResponse<ParleyResponsePostMessage>>

    /**
     * @param filePart
     * @return response
     */
    @Multipart
    @POST("messages")
    @Deprecated("Use {@link MessageService#postMedia(MultipartBody.Part)} from API 1.6 and onwards.")
    fun postImage(@Part filePart: MultipartBody.Part): Call<ParleyResponse<ParleyResponsePostMessage>>

    @Multipart
    @POST("media")
    fun postMedia(@Part filePart: MultipartBody.Part): Call<ParleyResponse<ParleyResponsePostMedia>>
}