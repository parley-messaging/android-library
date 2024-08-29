package nu.parley.android;

import java.util.Map;

public interface ParleyDownloadCallback {

    void launchParleyDownload(String url, Map<String, String> headers);
}
