package nu.parley.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

public final class ConnectivityMonitor {

    public interface Listener {

        void onNetworkAvailable();

        void onNetworkUnavailable();
    }

    private Listener listener;
    private final ConnectivityManager.NetworkCallback networkCallback;

    public ConnectivityMonitor() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.d("ConnectivityMonitor", "onAvailable");

                if (listener != null) {
                    listener.onNetworkAvailable();
                }
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                Log.d("ConnectivityMonitor", "onLost");

                if (listener != null) {
                    listener.onNetworkUnavailable();
                }
            }

            @Override
            public void onLosing(Network network, int maxMsToLive) {
                super.onLosing(network, maxMsToLive);
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
            }
        };
    }

    private static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = getConnectivityManager(context).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isNetworkOffline(Context context) {
        return !isNetworkAvailable(context);
    }

    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void register(Context context, Listener listener) {
        this.listener = listener;

        ConnectivityManager connectivityManager = getConnectivityManager(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API >= 28: Default network callback
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            // API >= 20: Registered network callback
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
        }

        if (isNetworkOffline(context)) {
            // Offline isn't triggered when registering, while online is being triggered.
            listener.onNetworkUnavailable();
        }
    }

    public void unregister(Context context) {
        this.listener = null;
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } catch (Exception e) {
            Log.e("Parley.CM.unregister", "Failed to unregister network callback");
            e.printStackTrace();
        }
    }
}
