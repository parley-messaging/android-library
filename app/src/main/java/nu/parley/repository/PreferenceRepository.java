package nu.parley.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

public final class PreferenceRepository {

    private static final String KEY_IDENTIFIER = "identifier";
    private static final String KEY_CUSTOMER_ID = "customer_id";
    private static final String DEFAULT_IDENTIFIER = "0W4qcE5aXoKq9OzvHxj2";

    public String getIdentifier(Context context) {
        return getPreferences(context).getString(KEY_IDENTIFIER, DEFAULT_IDENTIFIER);
    }

    @Nullable
    public String getCustomerId(Context context) {
        return getPreferences(context).getString(KEY_CUSTOMER_ID, null);
    }

    private SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setIdentifier(Context context, String identifier) {
        setPreferenceString(context, KEY_IDENTIFIER, identifier);
    }

    public void setCustomerId(Context context, @Nullable String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            removePreference(context, KEY_CUSTOMER_ID);
        } else {
            setPreferenceString(context, KEY_CUSTOMER_ID, customerId);
        }
    }

    private void setPreferenceString(Context context, String key, String value) {
        getPreferences(context)
                .edit()
                .putString(key, value)
                .apply();
    }

    private void removePreference(Context context, String key) {
        getPreferences(context)
                .edit()
                .remove(key)
                .apply();
    }
}
