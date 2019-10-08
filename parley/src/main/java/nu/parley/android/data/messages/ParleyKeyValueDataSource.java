package nu.parley.android.data.messages;

import androidx.annotation.Nullable;

/**
 * Provides the methods that are needed to enable caching of key value pairs for Parley.
 */
public interface ParleyKeyValueDataSource {

    String KEY_MESSAGE_INFO = "info_message";
    String KEY_PAGING = "paging";

    /**
     * Retrieve a value from the cache.
     *
     * @param key Key reference which was used when storing a value.
     * @return The value stored at this key, or null if nothing was found or saved.
     */
    @Nullable
    String get(String key);

    /**
     * Set a value in the cache for a specific key.
     *
     * @param key   Key referencing to the storage value, used for retrieval of the value later on.
     * @param value Actual value that should be stored for this key, or null to clear it.
     */
    void set(String key, @Nullable String value);
}
