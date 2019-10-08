package nu.parley.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class FileUtil {

    public static final String MIME_TYPE_IMAGE = "image/*";

    private static String getUniqueImageFileName() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "image_" + timeStamp + "_";
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                getUniqueImageFileName(),
                ".jpg",
                storageDir
        );
        return image;
    }

    public static File getFileFromContentUri(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File file = new File(new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/" + FileUtil.getUniqueImageFileName()).getAbsolutePath());
            copyInputStreamToFile(inputStream, file);

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }

    public static String getMimeType(String url) {
        String mimeType = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return mimeType;
    }
}
