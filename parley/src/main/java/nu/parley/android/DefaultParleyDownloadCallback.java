package nu.parley.android;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Map;

public class DefaultParleyDownloadCallback implements ParleyDownloadCallback {

    public interface Listener {
        void onComplete(Uri uri);
        void onFailed();
    }

    private final Context context;
    private final Listener listener;

    public DefaultParleyDownloadCallback(@NonNull Context context, @NonNull Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    private DownloadManager getDownloadManager() {
        return (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void launchParleyDownload(String url, Map<String, String> headers) {
        DownloadManager downloadManager = getDownloadManager();
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            request.addRequestHeader(key, value);
        }
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long downloadId = downloadManager.enqueue(request);
        register(downloadId);
    }

    private void register(long downloadId) {
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (downloadId == id) {
                        context.unregisterReceiver(this);

                        Uri uri = getDownloadManager().getUriForDownloadedFile(id);
                        if (uri == null) {
                            listener.onFailed();
                        } else {
                            listener.onComplete(uri);
                        }
                    }
                }
            }
        };

        ContextCompat.registerReceiver(context, onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_EXPORTED);
    }
}
