package nu.parley.android.imageviewer;

import java.util.List;

class BuilderData<T> {

    List<T> images;
    ImageViewerLoader<T> loader;

    public BuilderData(List<T> images, ImageViewerLoader<T> loader) {
        this.images = images;
        this.loader = loader;
    }
}
