package nu.parley.android;

import android.app.Activity;
import android.content.Intent;

import androidx.core.app.ActivityCompat;

public interface ParleyLaunchCallback {

    /**
     * Called when Parley wants to start an activity, redirect this to
     * {@link Activity#startActivity(Intent)} or any other method that allows an app to open an Activity.
     */
    void launchParleyActivity(Intent intent);

    /**
     * Called when Parley wants to start an activity and await its result, redirect this to
     * {@link Activity#startActivityForResult(Intent, int)}  or
     * {@link androidx.fragment.app.Fragment#startActivityForResult(Intent, int)}. Depending on this
     * you may need to update where you call {@link Parley#onActivityResult(int, int, Intent)}.
     */
    void launchParleyActivityForResult(Intent intent, int requestCode);

    /**
     * Called when Parley wants to request a permission, redirect this to
     * {@link ActivityCompat#requestPermissions(Activity, String[], int)} or
     * {@link androidx.fragment.app.Fragment#requestPermissions(String[], int)}. Depending on this
     * you may need to update where you call {@link Parley#onRequestPermissionsResult(int, String[], int[])}.
     */
    void launchParleyPermissionRequest(String[] permissions, int requestCode);
}
