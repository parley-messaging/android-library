package nu.parley.android.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public final class ParleyPermissionUtil {

    public static boolean hasPermission(Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean isPermissionInManifest(Context context, String permission) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] declaredPermissions = info.requestedPermissions;
            if (declaredPermissions != null && declaredPermissions.length > 0) {
                for (String p : declaredPermissions) {
                    if (p.equals(permission)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean shouldRequestPermission(Context context, @NonNull String permission) {
        if (permission.equals(Manifest.permission.CAMERA)) {
            // Camera permission is not required by Parley. However, Android will require it if the app declares using this permission in `AndroidManifest.xml`
            return ParleyPermissionUtil.isPermissionInManifest(context, permission) &&
                    !hasPermission(context, permission);
        }
        if (permission.equals(Manifest.permission.POST_NOTIFICATIONS)) {
            // Needed in Android 12 to show chat notifications when the chat isn't open currently.
            return !hasPermission(context, permission);
        }

        return false;
    }
}
