package nu.parley.android.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public static List<MimeType> getImages() {
        return new ArrayList<>(Arrays.asList(
                MimeType.ImageJpeg,
                MimeType.ImagePng,
                MimeType.ImageGif
        ));
    }

    public static List<MimeType> getDocuments() {
        return new ArrayList<>(Collections.singletonList(
                MimeType.ApplicationPdf
        ));
    }

    public final String key;

    MimeType(String key) {
        this.key = key;
    }
}
