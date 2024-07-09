package nu.parley.android.data.model;

/**
 * For information about each api version, check out the version lifetime documentation at:
 * https://developers.parley.nu/docs/version-lifetime
 */
public enum ApiVersion {
    /**
     * @deprecated A newer version is available to support the latest functionality of Parley.
     */
    @Deprecated V1_0,
    /**
     * @deprecated A newer version is available to support the latest functionality of Parley.
     */
    @Deprecated V1_1,
    /**
     * @deprecated A newer version is available to support the latest functionality of Parley.
     */
    @Deprecated V1_2,
    /**
     * @deprecated A newer version is available to support the latest functionality of Parley.
     */
    @Deprecated V1_3,
    /**
     * @deprecated A newer version is available to support the latest functionality of Parley.
     */
    @Deprecated V1_4,
    /**
     * @deprecated A newer version is available to support the latest functionality of Parley.
     */
    @Deprecated V1_5,
    /**
     * This is a previous version that is supported by the library.
     */
    V1_6,
    /**
     * This is the latest supported version by the library.
     */
    V1_7;

    public boolean isUsingMedia() {
        switch (this) {
            case V1_0:
            case V1_1:
            case V1_2:
            case V1_3:
            case V1_4:
            case V1_5:
                return false;
            case V1_6:
            case V1_7:
                return true;
        }
        throw new IllegalStateException("Unhandled API version");
    }

    public boolean isSupportingPdf() {
        switch (this) {
            case V1_0:
            case V1_1:
            case V1_2:
            case V1_3:
            case V1_4:
            case V1_5:
                return false;
            case V1_6:
            case V1_7:
                return true;
        }
        throw new IllegalStateException("Unhandled API version");
    }
}
