package nu.parley.android.data.model;

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

    public final String key;

    MimeType(String key) {
        this.key = key;
    }
}
