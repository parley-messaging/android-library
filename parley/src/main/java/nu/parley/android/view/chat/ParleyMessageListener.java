package nu.parley.android.view.chat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import nu.parley.android.Parley;
import nu.parley.android.ParleyLaunchCallback;
import nu.parley.android.R;
import nu.parley.android.data.model.Action;
import nu.parley.android.data.model.Message;
import nu.parley.android.imageviewer.ImageViewer;
import nu.parley.android.imageviewer.ImageViewerLoader;

public final class ParleyMessageListener implements MessageListener {

    private ParleyLaunchCallback launchCallback;

    @Override
    public void onRetryMessageClicked(Message message) {
        Parley.getInstance().resendMessage(message);
    }

    @Override
    public void onImageClicked(final Context context, final Message message) {
        List<Message> list = new ArrayList<>();
        list.add(message);

        ImageViewerLoader<Message> loader = new ImageViewerLoader<Message>() {
            @Override
            public void loadImage(final ImageView imageView, final Message image) {
                Glide.with(imageView.getContext())
                        .load(image.getImage())
                        .placeholder(android.R.color.transparent)
                        .fitCenter()
                        .into(imageView);
            }
        };
        new ImageViewer.Builder<>(context, list, loader)
                .show(true);
    }

    @Override
    public void onActionClicked(View view, Action action) {
        switch (action.getType()) {
            case REPLY:
                Parley.send(action.getPayload());
                break;
            case WEB_URL:
                openUrl(view, action.getPayload());
                break;
            case PHONE_NUMBER:
                openUrl(view, "tel://" + action.getPayload());
                break;
        }
    }

    private void openUrl(View view, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            if (intent != null) {
                launchCallback.launchParleyActivity(intent);
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                launchCallback.launchParleyActivity(browserIntent);
            }
        } catch (URISyntaxException | ActivityNotFoundException e) {
            e.printStackTrace();
            Snackbar.make(view, R.string.parley_error_action_open, Snackbar.LENGTH_LONG).show();
        }
    }

    public void setLaunchCallback(@NonNull ParleyLaunchCallback launchCallback) {
        this.launchCallback = launchCallback;
    }
}
