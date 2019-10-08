package nu.parley.android.imageviewer;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ImageViewer<T> {

    private Context context;
    private BuilderData<T> builderData;
    private ImageViewerDialog<T> dialog;

    public ImageViewer(@NonNull Context context, @NonNull BuilderData<T> builderData) {
        this.context = context;
        this.builderData = builderData;
        this.dialog = new ImageViewerDialog<>(context, builderData);
    }

    public void show(boolean animate) {
        dialog.show(animate);
    }

    public static class Builder<T> {

        private Context context;
        private BuilderData<T> data;

        public Builder(Context context, T[] images, ImageViewerLoader<T> loader) {
            this(context, new ArrayList<>(Arrays.asList(images)), loader);
        }

        public Builder(Context context, List<T> images, ImageViewerLoader<T> loader) {
            this.context = context;
            this.data = new BuilderData<>(images, loader);
        }

        public ImageViewer<T> build() {
            return new ImageViewer<>(context, data);
        }

        public ImageViewer<T> show(boolean animate) {
            ImageViewer<T> viewer = build();
            viewer.show(animate);
            return viewer;
        }
    }
}