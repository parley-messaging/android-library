package nu.parley.android.view.chat;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import nu.parley.android.Parley;
import nu.parley.android.data.model.Message;
import nu.parley.android.imageviewer.ImageViewer;
import nu.parley.android.imageviewer.ImageViewerLoader;

public final class ParleyMessageListener implements MessageListener {

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
}
