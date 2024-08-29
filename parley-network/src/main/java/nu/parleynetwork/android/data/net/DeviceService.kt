package nu.parleynetwork.android.data.net;

import nu.parley.android.data.model.Device;
import nu.parleynetwork.android.data.model.DeviceJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface DeviceService {

    @POST("devices")
    Call<Void> register(@Body DeviceJson device);
}
