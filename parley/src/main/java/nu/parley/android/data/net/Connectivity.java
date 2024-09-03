package nu.parley.android.data.net;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.datatheorem.android.trustkit.TrustKit;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import nu.parley.android.Parley;
import nu.parley.android.data.net.response.ParleyErrorResponse;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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