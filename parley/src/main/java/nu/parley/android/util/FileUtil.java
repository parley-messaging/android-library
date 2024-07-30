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

import nu.parley.android.data.model.MimeType;

public final class FileUtil {

    private static String getUniqueFileName() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp;
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                getUniqueFileName(),
                ".jpg",
                storageDir
        );
        return image;
    }

    public static File getFileFromContentUri(Context context, Uri uri) {
        try {
            String type = context.getContentResolver().getType(uri);
            MimeType mimeType = MimeType.Companion.fromValue(type);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            String directory;
            if (mimeType.isImage()) {
                directory = Environment.DIRECTORY_PICTURES;
            } else {
                directory = Environment.DIRECTORY_DOCUMENTS;
            }
            File destination = new File(new File(context.getExternalFilesDir(directory).getPath() + "/" + FileUtil.getUniqueFileName() + "." + mimeType.getExtension()).getAbsolutePath());
            copyInputStreamToFile(inputStream, destination);

            return destination;
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
