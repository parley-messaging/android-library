package nu.parley.android.imageviewer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ortiz.touchview.OnTouchImageViewListener;
import com.ortiz.touchview.TouchImageView;

import java.util.List;

import nu.parley.android.R;

final class ImageViewerViewPagerAdapter<T> extends PagerAdapter {

    private List<T> images;
    private ImageViewerListener zoomListener;
    private ImageViewerLoader<T> loader;
    private boolean lastZoomedState = false;

    ImageViewerViewPagerAdapter(List<T> images, ImageViewerListener zoomListener, ImageViewerLoader<T> loader) {
        this.images = images;
        this.zoomListener = zoomListener;
        this.loader = loader;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_image_view, container, false);
        container.addView(view);

        final TouchImageView imageView = view.findViewById(R.id.image_view);
        imageView.setOnTouchImageViewListener(new OnTouchImageViewListener() {
            @Override
            public void onMove() {
                boolean newZoomedState = imageView.isZoomed();
                if (lastZoomedState != newZoomedState) {
                    // Trigger update
                    if (newZoomedState) {
                        zoomListener.onZoomStarted();
                    } else {
                        zoomListener.onZoomStopped();
                    }
                }
                lastZoomedState = newZoomedState;
            }
        });

        loader.loadImage(imageView, images.get(position));
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return callbackListener.getTitle(position);
//    }
}
