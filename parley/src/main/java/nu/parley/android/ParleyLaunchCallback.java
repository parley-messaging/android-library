package nu.parley.android;

import android.app.Activity;
import android.content.Intent;

import androidx.core.app.ActivityCompat;

public interface ParleyLaunchCallback {

    /**
     * Called when Parley wants to start an activity, redirect this to
     * {@link Activity#startActivity(Intent)} or to the respective Fragment one
     * ({@link androidx.fragment.app.Fragment#startActivity(Intent)}.
     */
    void launchParleyActivity(Intent intent);

    /**
     * Called when Parley wants to start an activity and await its result, redirect this to
     * {@link Activity#startActivityForResult(Intent, int)}  or
     * {@link androidx.fragment.app.Fragment#startActivityForResult(Intent, int)}. Depending on this
     * implementation, the `onActivityResult` method will be called in either the Activity or the
     * Fragment. This `onActivityResult` must be forwarded to
     * {@link Parley#onActivityResult(int, int, Intent)}
     */
    void launchParleyActivityForResult(Intent intent, int requestCode);

    /**
     * Called when Parley wants to request a permission, redirect this to
     * {@link ActivityCompat#requestPermissions(Activity, String[], int)} or
     * {@link androidx.fragment.app.Fragment#requestPermissions(String[], int)}. Depending on this
     * implementation, the `onRequestPermissionsResult` method will be called in either the Activity
     * or the Fragment. This `onRequestPermissionsResult` must be forwarded to
     * {@link Parley#onRequestPermissionsResult(int, String[], int[])}
     */
    void launchParleyPermissionRequest(String[] permissions, int requestCode);
}
