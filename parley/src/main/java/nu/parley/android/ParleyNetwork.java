package nu.parley.android;

import androidx.annotation.XmlRes;

import java.util.HashMap;
import java.util.Map;

import nu.parley.android.data.model.ApiVersion;

/**
 * Provides the network configuration for Parley.
 */
public final class ParleyNetwork {

    private static final String DEFAULT_NETWORK_URL = "https://api.parley.nu/";
    private static final String DEFAULT_NETWORK_PATH = "clientApi/v1.7/";

    public final String url;
    public final Map<String, String> headers;
    @XmlRes
    final Integer securityConfigResourceFile;
    public final String path;
    public final ApiVersion apiVersion;
    public NetworkConfig config = new ParleyNetworkConfig();

    /**
     * Applies the default network settings of Parley.
     */
    ParleyNetwork() {
        this.url = DEFAULT_NETWORK_URL;
        this.path = DEFAULT_NETWORK_PATH;
        this.apiVersion = ApiVersion.V1_7;
        this.securityConfigResourceFile = R.xml.parley_network_security_config;
        this.headers = new HashMap<>();
    }

    /**
     * Convenience for ParleyNetwork(url, path, apiVersion, securityConfigResourceFile, headers).
     *
     * @see #ParleyNetwork(String, String, ApiVersion, Integer, Map)
     */
    @SuppressWarnings("unused")
    public ParleyNetwork(String url, String path, ApiVersion apiVersion, @XmlRes Integer securityConfigResourceFile) {
        this(url, path, apiVersion, securityConfigResourceFile, new HashMap<String, String>());
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
     * @param securityConfigResourceFile Android Network Security Configuration file xml resource with SSL Pinning configuration.
     * @param headers                    Additional headers to append to each network request of Parley.
     */
    public ParleyNetwork(String url, String path, ApiVersion apiVersion, @XmlRes Integer securityConfigResourceFile, Map<String, String> headers) {
        this.url = url;
        this.path = path;
        this.apiVersion = apiVersion;
        this.securityConfigResourceFile = securityConfigResourceFile;
        this.headers = headers;
    }

    /**
     * @return The base url with the domain and the path
     */
    public String getBaseUrl() {
        return url + path;
    }
}