package nu.parley.android.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

final class PreferenceRepository {

    enum Key {
        DEVICE_ID("device_id");

        final String name;

        Key(String name) {
            this.name = name;
        }
    }

    private static final String PREFERENCES_NAME = "parley";
    private String deviceId;

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Nullable
    public String getDeviceId(Context context) {
        if (deviceId == null) {
            deviceId = getSharedPreferences(context).getString(Key.DEVICE_ID.name, null);
        }
        return deviceId;
    }

    public void setDeviceId(Context context, @Nullable String deviceId) {
        this.deviceId = deviceId;
        getSharedPreferences(context).edit().putString(Key.DEVICE_ID.name, deviceId).apply();
    }
}
