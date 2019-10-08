package nu.parley.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class PreferenceRepository {

    private static final String KEY_IDENTIFIER = "identifier";
    private static final String DEFAULT_IDENTIFIER = "0W4qcE5aXoKq9OzvHxj2";

    public String getIdentifier(Context context) {
        return getPreferences(context).getString(KEY_IDENTIFIER, DEFAULT_IDENTIFIER);
    }

    private SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setIdentifier(Context context, String identifier) {
        getPreferences(context)
                .edit()
                .putString(KEY_IDENTIFIER, identifier)
                .apply();
    }
}
