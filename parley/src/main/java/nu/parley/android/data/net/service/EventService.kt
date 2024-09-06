package nu.parley.android.data.net.service

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

internal interface EventService {

    @POST("services/event/{event}")
    fun fire(@Path("event") event: String): Call<Void>
}