package nu.parley.android.imageviewer;

import android.widget.ImageView;

public interface ImageViewerLoader<T> {

    void loadImage(ImageView imageView, T image);
}