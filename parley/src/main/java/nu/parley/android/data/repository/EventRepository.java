package nu.parley.android.data.repository;

import nu.parley.android.data.net.Connectivity;
import nu.parley.android.data.net.service.EventService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public interface EventRepository {

    public void fire(String event);
}
