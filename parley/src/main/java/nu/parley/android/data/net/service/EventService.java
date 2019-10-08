package nu.parley.android.data.net.service;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventService {

    @POST("services/event/{event}")
    Call<Void> fire(@Path("event") String event);
}
