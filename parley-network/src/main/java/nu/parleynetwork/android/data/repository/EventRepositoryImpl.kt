package nu.parleynetwork.android.data.repository;

import nu.parley.android.data.net.Connectivity;
import nu.parley.android.data.net.service.EventService;
import nu.parley.android.data.repository.EventRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRepositoryImpl implements EventRepository {

    public void fire(String event) {
        Call<Void> eventCall = Connectivity.getRetrofit().create(EventService.class).fire(event);
        eventCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Ignore
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Ignore
            }
        });
    }
}
