package nu.parleynetwork.android.data.repository;

import nu.parley.android.Parley;
import nu.parley.android.data.model.Device;
import nu.parley.android.data.net.Connectivity;
import nu.parley.android.data.net.RepositoryCallback;
import nu.parley.android.data.net.service.DeviceService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class DeviceRepositoryImpl implements nu.parley.android.data.repository.DeviceRepository {

    public void register(Device device, final RepositoryCallback<Void> callback) {
        DeviceService deviceService = Connectivity.getRetrofit().create(DeviceService.class);
        Call<Void> registerCall = deviceService.register(device);
        registerCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                callback.onFailed(null, t.getMessage());
            }
        });
    }
}
