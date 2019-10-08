package nu.parley.android.util;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public final class TakePictureFileProvider extends FileProvider {

    private static final String PARLEY_FILE_PROVIDER_SUFFIX = ".parley_file_provider";

    public static Uri getUriForFile(Context context, File path) {
        String applicationId = context.getPackageName();
        return FileProvider.getUriForFile(context, applicationId + PARLEY_FILE_PROVIDER_SUFFIX, path);
    }
}
