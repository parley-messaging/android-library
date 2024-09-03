package nu.parley.android.data.net;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.util.HashMap;
import java.util.Map;

import nu.parley.android.Parley;

public final class Connectivity {

    private static final String HEADER_PARLEY_IDENTIFICATION = "x-iris-identification"; // Rename Iris to Parley when backend accepts this
    private static final String HEADER_PARLEY_AUTHORIZATION = "Authorization";

    public static Map<String, String> getAdditionalHeaders() {
        return Parley.getInstance().getNetwork().headers;
    }

    public static Map<String, String> getParleyHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_PARLEY_IDENTIFICATION, Parley.getInstance().getSecret() + ":" + Parley.getInstance().getUniqueDeviceIdentifier());
        if (Parley.getInstance().getUserAuthorization() != null) {
            headers.put(HEADER_PARLEY_AUTHORIZATION, Parley.getInstance().getUserAuthorization());
        }
        return headers;
    }

    public static String toMediaUrl(String mediaId) {
        return Parley.getInstance().getNetwork().getBaseUrl() + "media/" + mediaId;
    }

    public static GlideUrl toGlideUrl(String url) {
        LazyHeaders.Builder lazyHeadersBuilder = new LazyHeaders.Builder();
        for (Map.Entry<String, String> entry : getAdditionalHeaders().entrySet()) {
            lazyHeadersBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : getParleyHeaders().entrySet()) {
            lazyHeadersBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        return new GlideUrl(url, lazyHeadersBuilder.build());
    }
}