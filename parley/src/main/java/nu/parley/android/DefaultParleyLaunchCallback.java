package nu.parley.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class DefaultParleyLaunchCallback implements ParleyLaunchCallback {

    private final Context context;

    public DefaultParleyLaunchCallback(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void launchParleyActivity(Intent intent) {
        context.startActivity(intent);
    }

    @Override
    public void launchParleyActivityForResult(Intent intent, int requestCode) {
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    public void launchParleyPermissionRequest(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(
                (Activity) context,
                permissions,
                requestCode
        );
    }
}
