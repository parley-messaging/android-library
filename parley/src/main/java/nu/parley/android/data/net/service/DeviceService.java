package nu.parley.android.data.net.service;

import nu.parley.android.data.model.Device;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DeviceService {

    @POST("devices")
    Call<Void> register(@Body Device device);

}