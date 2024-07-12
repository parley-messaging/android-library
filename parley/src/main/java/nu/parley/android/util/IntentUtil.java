package nu.parley.android.util;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.net.URISyntaxException;

public class IntentUtil {

    @Nullable
    public static Intent fromUrl(String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            if (intent == null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                browserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                return browserIntent;
            } else {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                return intent;
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
