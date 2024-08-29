package nu.parleynetwork.android.data.repository

import nu.parley.android.data.net.Connectivity
import nu.parley.android.data.net.service.EventService
import nu.parley.android.data.repository.EventRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventRepositoryImpl : EventRepository {
    public override fun fire(event: String) {
        var eventCall = Connectivity.getRetrofit().create(
            EventService::class.java
        ).fire(event)
        eventCall.enqueue(object : Callback<Void?> {
            public override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                // Ignore
            }

            public override fun onFailure(call: Call<Void?>, t: Throwable) {
                // Ignore
            }
        })
    }
}
