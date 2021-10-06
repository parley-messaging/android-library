package nu.parley.android.data.model;

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
     * This is the latest supported version by the library.
     */
    V1_6;

    public boolean isUsingMedia() {
        return this == V1_6;
    }
}
