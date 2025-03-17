package nu.parley.android.view.chat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.parley.android.Parley;
import nu.parley.android.ParleyDownloadCallback;
import nu.parley.android.ParleyLaunchCallback;
import nu.parley.android.R;
import nu.parley.android.data.model.Action;
import nu.parley.android.data.model.Media;
import nu.parley.android.data.model.Message;
import nu.parley.android.data.net.Connectivity;
import nu.parley.android.imageviewer.ImageViewer;
import nu.parley.android.imageviewer.ImageViewerLoader;
import nu.parley.android.util.IntentUtil;

public final class ParleyMessageListener implements MessageListener {

    private ParleyLaunchCallback launchCallback;
    private ParleyDownloadCallback downloadCallback;

    @Override
    public void onRetryMessageClicked(Message message) {
        Parley.getInstance().resendMessage(message);
    }

    @Override
    public void onMediaClicked(final View view, final Message message) {
        Media media = message.getMedia();
        if (media == null) {
            return;
        }

        switch (media.getMimeType()) {
            case ImageJpeg:
            case ImagePng:
            case ImageGif:
                List<String> fileNames = new ArrayList<String>();
                List<Message> list = new ArrayList<>();
                fileNames.add(message.getMedia().getFileName());
                list.add(message);
                openImages(view.getContext(), list, fileNames);
                break;
            case ApplicationPdf:
            case Other:
                openDownload(view, media.getIdForUrl());
                break;
        }
    }

    private static void openImages(Context context, List<Message> list, List<String> fileNames) {
        ImageViewerLoader<Message> loader = new ImageViewerLoader<Message>() {
            @Override
            public void loadImage(final ImageView imageView, final Message image) {

                GlideUrl url = Connectivity.toGlideUrl(image.getImageUrl());
                Glide.with(imageView.getContext())
                        .load(url)
                        .placeholder(android.R.color.transparent)
                        .fitCenter()
                        .into(imageView);
            }
        };
        new ImageViewer.Builder<>(context, list, fileNames, loader)
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

    private void openDownload(View view, String url) {
        Uri downloadUri = Uri.parse(Parley.getInstance().getNetwork().getBaseUrl() + "media/" + url);
        Map<String, String> headers = new HashMap<>();
        headers.putAll(Connectivity.getAdditionalHeaders());
        headers.putAll(Connectivity.getParleyHeaders());
        showSnackbar(view, R.string.parley_message_file_downloading, R.string.parley_snackbar_close);
        downloadCallback.launchParleyDownload(
                downloadUri.toString(),
                headers
        );
    }

    private void showSnackbar(View view, @StringRes Integer message, @StringRes Integer actionMessage) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(actionMessage, v -> { snackbar.dismiss(); });
        snackbar.show();
    }

    private void openUrl(View view, String url) {
        try {
            Intent intent = IntentUtil.fromUrl(url);
            if (intent == null) {
                showSnackbar(view, R.string.parley_error_action_open, R.string.parley_snackbar_close);
            } else {
                launchCallback.launchParleyActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            showSnackbar(view, R.string.parley_error_action_open, R.string.parley_snackbar_close);
        }
    }

    public void setLaunchCallback(@NonNull ParleyLaunchCallback launchCallback) {
        this.launchCallback = launchCallback;
    }

    public void setDownloadCallback(@NonNull ParleyDownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
    }
}
