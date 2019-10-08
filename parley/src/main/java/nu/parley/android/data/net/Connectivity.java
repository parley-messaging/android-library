package nu.parley.android.data.net;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.datatheorem.android.trustkit.TrustKit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import nu.parley.android.Parley;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class Connectivity {

    private static final String HEADER_PARLEY_IDENTIFICATION = "x-iris-identification"; // Rename Iris to Parley when backend accepts this
    private static final String HEADER_PARLEY_AUTHORIZATION = "Authorization";

    /**
     * Get a Retrofit instance with Parley's url and path and OkHttpClient.
     *
     * @return Retrofit
     */
    public static Retrofit getRetrofit() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(Parley.getInstance().getNetwork().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient());

        return retrofitBuilder.build();
    }

    /**
     * Get a OkHttpClient instance with Parley identification header.
     *
     * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request.Builder requestBuilder = original.newBuilder()
                                .method(original.method(), original.body());

                        addAdditionalHttpHeaders(requestBuilder);
                        addParleyHttpHeaders(requestBuilder);

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        applySslPinning(okHttpClientBuilder);

        return okHttpClientBuilder.build();
    }

    private static void applySslPinning(OkHttpClient.Builder okHttpClientBuilder) {
        URL url;
        String serverHostname;
        try {
            url = new URL(Parley.getInstance().getNetwork().url);
            serverHostname = url.getHost();

            SSLSocketFactory sslSocketFactory = TrustKit.getInstance().getSSLSocketFactory(serverHostname);
            X509TrustManager trustManager = TrustKit.getInstance().getTrustManager(serverHostname);
            okHttpClientBuilder.sslSocketFactory(sslSocketFactory, trustManager);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the additional http headers that were provided to the Parley instance to the request builder.
     *
     * @param requestBuilder The request builder on which the headers should be added.
     */
    private static void addAdditionalHttpHeaders(Request.Builder requestBuilder) {
        for (Map.Entry<String, String> entry : Parley.getInstance().getNetwork().headers.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            requestBuilder.addHeader(name, value);
        }
    }

    /**
     * Adds the additional http headers that were provided to the Parley instance to the request builder.
     *
     * @param requestBuilder The request builder on which the headers should be added.
     */
    private static void addAdditionalHttpHeaders(LazyHeaders.Builder requestBuilder) {
        for (Map.Entry<String, String> entry : Parley.getInstance().getNetwork().headers.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            requestBuilder.addHeader(name, value);
        }
    }

    /**
     * Adds the http headers that are specific to Parley to the request builder.
     *
     * @param requestBuilder The request builder on which the headers should be added.
     */
    private static void addParleyHttpHeaders(Request.Builder requestBuilder) {
        requestBuilder.addHeader(HEADER_PARLEY_IDENTIFICATION, Parley.getInstance().getSecret() + ":" + Parley.getInstance().getUniqueDeviceIdentifier());

        if (Parley.getInstance().getUserAuthorization() != null) {
            requestBuilder.addHeader(HEADER_PARLEY_AUTHORIZATION, Parley.getInstance().getUserAuthorization());
        }
    }

    public static GlideUrl toGlideUrl(int messageId) {
        return toGlideUrl(Parley.getInstance().getNetwork().getBaseUrl() + "images/" + messageId);
    }

    private static GlideUrl toGlideUrl(String url) {
        LazyHeaders.Builder lazyHeadersBuilder = new LazyHeaders.Builder();

        addAdditionalHttpHeaders(lazyHeadersBuilder);

        lazyHeadersBuilder.addHeader(HEADER_PARLEY_IDENTIFICATION, Parley.getInstance().getSecret() + ":" + Parley.getInstance().getUniqueDeviceIdentifier());

        if (Parley.getInstance().getUserAuthorization() != null) {
            lazyHeadersBuilder.addHeader(HEADER_PARLEY_AUTHORIZATION, Parley.getInstance().getUserAuthorization());
        }

        return new GlideUrl(url, lazyHeadersBuilder.build());
    }
}