package nu.parley.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

public final class ConnectivityMonitor {

    private Listener listener;
    private ConnectivityManager.NetworkCallback networkCallback; // Only used in API >= 20
    private ParleyConnectivityBroadcastReceiver connectivityBroadcastReceiver; // Only used in API < 20

    public ConnectivityMonitor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // API >= 20: Network callback usage
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
        } else {
            // API < 20: Legacy method - Broadcast receiver
            connectivityBroadcastReceiver = new ParleyConnectivityBroadcastReceiver();
        }
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

    @SuppressWarnings({"deprecation", "RedundantSuppression"})
    public void register(Context context, Listener listener) {
        this.listener = listener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        } else {
            // API < 20: Legacy method - Broadcast receiver
            if (!connectivityBroadcastReceiver.isRegistered()) { // Only register once
                IntentFilter filter = new IntentFilter();
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                context.registerReceiver(connectivityBroadcastReceiver, filter);
                connectivityBroadcastReceiver.setRegistered(true);
            }
        }
    }

    public void unregister(Context context) {
        this.listener = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // API >= 20: Using connectivity manager
            ConnectivityManager connectivityManager = getConnectivityManager(context);
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                Log.e("Parley.CM.unregister", "Failed to unregister network callback");
                e.printStackTrace();
            }
        } else {
            // API < 20: Legacy method - Broadcast receiver
            if (connectivityBroadcastReceiver.isRegistered()) { // Only unregister if it was registered
                context.unregisterReceiver(connectivityBroadcastReceiver);
                connectivityBroadcastReceiver.setRegistered(false);
            }
        }
    }

    public interface Listener {

        void onNetworkAvailable();

        void onNetworkUnavailable();
    }


    /**
     * A connectivity broadcast receiver to support these callbacks on API &lt; 20
     *
     * <p>
     * <b>Note:</b> Only used in API &lt; 20
     * </p>
     */
    public class ParleyConnectivityBroadcastReceiver extends BroadcastReceiver {

        private boolean registered;

        @Override
        public void onReceive(@Nullable final Context context, Intent intent) {
            if (context != null) {
                if (isNetworkAvailable(context)) {
                    listener.onNetworkAvailable();
                } else {
                    listener.onNetworkUnavailable();
                }
            }
        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        }

        public void setRegistered(boolean registered) {
            this.registered = registered;
        }

        public boolean isRegistered() {
            return registered;
        }
    }
}
