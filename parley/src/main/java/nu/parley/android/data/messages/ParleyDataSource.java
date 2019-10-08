package nu.parley.android.data.messages;

/**
 * Provides the methods that are needed to enable caching for your Parley instance.
 */
public interface ParleyDataSource extends ParleyKeyValueDataSource, ParleyMessageDataSource {

    /**
     * Clears everything in the cache.
     */
    void clear();
}
