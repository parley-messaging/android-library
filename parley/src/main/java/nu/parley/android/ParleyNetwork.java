package nu.parley.android;

import java.util.HashMap;
import java.util.Map;

import nu.parley.android.data.model.ApiVersion;
import nu.parley.android.data.net.service.ParleyNetworkSession;
import nu.parley.android.data.net.service.RetrofitNetworkSession;

/**
 * Provides the network configuration for Parley.
 */
public final class ParleyNetwork {

    private static final String DEFAULT_NETWORK_URL = "https://api.parley.nu/";
    private static final String DEFAULT_NETWORK_PATH = "clientApi/v1.9/";
    private static final ApiVersion DEFAULT_NETWORK_API_VERSION = ApiVersion.V1_9;

    public final String url;
    public final Map<String, String> headers;
    public final String path;
    public final ApiVersion apiVersion;
    public final ParleyNetworkSession networkSession;

    /**
     * Applies the default network settings of Parley.
     */
    ParleyNetwork() {
        this.url = DEFAULT_NETWORK_URL;
        this.path = DEFAULT_NETWORK_PATH;
        this.apiVersion = DEFAULT_NETWORK_API_VERSION;
        this.headers = new HashMap<>();
        this.networkSession = new RetrofitNetworkSession();
    }

    /**
     * Convenience for ParleyNetwork(url, path, apiVersion, headers).
     *
     * @see #ParleyNetwork(String, String, ApiVersion, Map)
     */
    @SuppressWarnings("unused")
    public ParleyNetwork(String url, String path, ApiVersion apiVersion) {
        this(url, path, apiVersion, new HashMap<>(), new RetrofitNetworkSession());
    }

    /**
     * Convenience for ParleyNetwork(url, path, apiVersion, parleyNetworkSession).
     *
     * @see #ParleyNetwork(String, String, ApiVersion, Map)
     */
    @SuppressWarnings("unused")
    public ParleyNetwork(String url, String path, ApiVersion apiVersion, ParleyNetworkSession parleyNetworkSession) {
        this(url, path, apiVersion, new HashMap<>(), parleyNetworkSession);
    }

    /**
     * Apply custom network settings. Parley requires SSL pinning, so it expects a valid security config file.
     *
     * <p>
     * <b>Note:</b>* Make sure to add the reference to the `AndroidManifest.xml` as well.
     * </p>
     *
     * @param url                        Url to your Parley backend service.
     * @param path                       Path to the Parley chat API.
     * @param apiVersion                 API version of the Parley chat API. Note that the `path` should use the same api version as well.
     * @param headers                    Additional headers to append to each network request of Parley.
     */
    public ParleyNetwork(String url, String path, ApiVersion apiVersion, Map<String, String> headers) {
        this.url = url;
        this.path = path;
        this.apiVersion = apiVersion;
        this.headers = headers;
        this.networkSession = new RetrofitNetworkSession();
    }

    /**
     * Apply custom network settings. Parley requires SSL pinning, so it expects a valid security config file.
     *
     * <p>
     * <b>Note:</b>* Make sure to add the reference to the `AndroidManifest.xml` as well.
     * </p>
     *
     * @param url                        Url to your Parley backend service.
     * @param path                       Path to the Parley chat API.
     * @param apiVersion                 API version of the Parley chat API. Note that the `path` should use the same api version as well.
     * @param headers                    Additional headers to append to each network request of Parley.
     */
    public ParleyNetwork(String url, String path, ApiVersion apiVersion, Map<String, String> headers, ParleyNetworkSession parleyNetworkSession) {
        this.url = url;
        this.path = path;
        this.apiVersion = apiVersion;
        this.headers = headers;
        this.networkSession = parleyNetworkSession;
    }

    /**
     * @return The base url with the domain and the path
     */
    public String getBaseUrl() {
        return url + path;
    }
}