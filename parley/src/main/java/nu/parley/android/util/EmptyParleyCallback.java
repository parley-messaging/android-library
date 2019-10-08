package nu.parley.android.util;

import androidx.annotation.Nullable;

import nu.parley.android.ParleyCallback;

public final class EmptyParleyCallback implements ParleyCallback {

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(@Nullable Integer code, @Nullable String message) {

    }
}
