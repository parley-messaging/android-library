package nu.parley.android.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.parley.android.Parley;

public enum MimeType {
    ImageJpeg("image/jpeg"),
    ImagePng("image/png"),
    ImageGif("image/gif"),
    ApplicationPdf("application/pdf"),
    Unknown("unknown");

    static MimeType from(String key) {
        for (MimeType mimeType : values()) {
            if (mimeType.key.equals(key)) {
                return mimeType;
            }
        }
        return Unknown;
    }

    public static List<MimeType> getSupported(ApiVersion apiVersion) {
        ArrayList<MimeType> mimeTypes = new ArrayList<>(Arrays.asList(
                MimeType.ImageJpeg,
                MimeType.ImagePng,
                MimeType.ImageGif
        ));
        if (apiVersion.isSupportingPdf()) {
            mimeTypes.add(MimeType.ApplicationPdf);
        }
        return mimeTypes;
    }

    public final String key;

    MimeType(String key) {
        this.key = key;
    }
}
