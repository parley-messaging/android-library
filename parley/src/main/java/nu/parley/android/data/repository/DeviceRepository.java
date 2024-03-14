package nu.parley.android.data.repository;

import android.content.Context;

import java.util.UUID;

import nu.parley.android.Parley;
import nu.parley.android.data.model.Device;
import nu.parley.android.data.net.Connectivity;
import nu.parley.android.data.net.RepositoryCallback;
import nu.parley.android.data.net.service.DeviceService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class DeviceRepository {

    public void register(final RepositoryCallback<Void> callback) {
        Device device = new Device();

        device.setPushToken(Parley.getInstance().getPushToken(), Parley.getInstance().getPushType());
        device.setUserAdditionalInformation(Parley.getInstance().getUserAdditionalInformation());
        device.setReferrer(Parley.getInstance().getReferrer());

        Call<Void> registerCall = Connectivity.getRetrofit().create(DeviceService.class).register(device);
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

    public static String getDeviceId(Context context) {
        PreferenceRepository preferences = new PreferenceRepository();
        String deviceId = preferences.getDeviceId(context);
        if (deviceId == null) {
            String newDeviceId = UUID.randomUUID().toString();
            preferences.setDeviceId(context, newDeviceId);
            return newDeviceId;
        } else {
            return deviceId;
        }
    }
}
