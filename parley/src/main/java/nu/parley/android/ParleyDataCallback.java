package nu.parley.android;

import androidx.annotation.Nullable;

/**
 * Provides the methods that are available as Callback when calling certain methods on your Parley instance.
 */
public interface ParleyDataCallback<T> {

    /**
     * Indicates that the operation was successful.
     */
    void onSuccess(T data);

    /**
     * Indicates that the operation failed for some reason.
     *
     * @param code    Status code of the failing operation.
     * @param message Message describing the failure.
     */
    void onFailure(Integer code, String message);
}