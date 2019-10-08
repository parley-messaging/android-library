package nu.parley.android;

import androidx.annotation.Nullable;

/**
 * Provides the methods that are available as Callback when calling certain methods on your Parley instance.
 */
public interface ParleyCallback {

    /**
     * Indicates that the operation was successful.
     */
    void onSuccess();

    /**
     * Indicates that the operation failed for some reason.
     *
     * @param code    Status code of the failing operation, or `null` if not available.
     * @param message Message describing the failure, or `null` if not available.
     */
    void onFailure(@Nullable Integer code, @Nullable String message);
}