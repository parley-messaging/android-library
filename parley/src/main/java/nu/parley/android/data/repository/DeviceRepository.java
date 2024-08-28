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

public interface DeviceRepository {

    public void register(final RepositoryCallback<Void> callback);

    // TODO
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
