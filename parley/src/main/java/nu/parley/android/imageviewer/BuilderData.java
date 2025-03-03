package nu.parley.android.imageviewer;

import java.util.List;

class BuilderData<T> {

    List<T> images;
    List<String> fileNames;
    ImageViewerLoader<T> loader;

    public BuilderData(List<T> images, List<String> fileNames, ImageViewerLoader<T> loader) {
        this.images = images;
        this.fileNames = fileNames;
        this.loader = loader;
    }
}
