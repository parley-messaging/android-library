package nu.parley.android.data.net.service;

import nu.parley.android.data.model.Device;
import nu.parley.android.data.net.RepositoryCallback;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DeviceService {

    void register(RepositoryCallback<Void> callback, Device device);

    @POST("devices")
    Call<Void> register(@Body Device device);

}