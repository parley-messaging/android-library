package nu.parleynetwork.android.data.net

import nu.parley.android.data.model.Message
import nu.parley.android.data.net.response.ParleyResponse
import nu.parleynetwork.android.data.model.MessageJson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

internal interface MessageService {

    @GET("messages/{id}")
    fun get(@Path("id") id: Int): Call<ParleyResponse<MessageJson>>
}