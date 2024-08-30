package nu.parleynetwork.android.data.net

import nu.parley.android.data.net.response.ParleyResponse
import nu.parley.android.data.net.response.ParleyResponsePostMedia
import nu.parley.android.data.net.response.ParleyResponsePostMessage
import nu.parleynetwork.android.data.model.MessageJson
import nu.parleynetwork.android.data.model.ParleyResponseJson
import nu.parleynetwork.android.data.model.ParleyResponsePostMediaJson
import nu.parleynetwork.android.data.model.ParleyResponsePostMessageJson
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
    fun get(@Path("id") id: Int): Call<ParleyResponseJson<MessageJson>>

    @GET("messages")
    fun findAll(): Call<ParleyResponseJson<List<MessageJson>>>

    @GET
    fun getOlder(@Url url: String): Call<ParleyResponseJson<List<MessageJson>>>

    @POST("messages")
    fun post(@Body chatMessage: MessageJson): Call<ParleyResponseJson<ParleyResponsePostMessageJson>>

    /**
     * @param filePart
     * @return response
     */
    @Multipart
    @POST("messages")
    @Deprecated("Use {@link MessageService#postMedia(MultipartBody.Part)} from API 1.6 and onwards.")
    fun postImage(@Part filePart: MultipartBody.Part): Call<ParleyResponseJson<ParleyResponsePostMessageJson>>

    @Multipart
    @POST("media")
    fun postMedia(@Part filePart: MultipartBody.Part): Call<ParleyResponseJson<ParleyResponsePostMediaJson>>
}