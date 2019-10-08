package nu.parley.android.imageviewer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import java.util.List;

import nu.parley.android.R;

public final class ImageViewerView<T> extends FrameLayout implements ImageViewerListener {

    private ImageView closeImageView;
    private ImageViewerViewPager viewPager;

    public ImageViewerView(Context context) {
        super(context);
        init(context);
    }

    public ImageViewerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageViewerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.dialog_image_viewer, this);

        closeImageView = findViewById(R.id.close_image_view);
        viewPager = findViewById(R.id.view_pager);

        setupListeners();
        setupView();
    }

    private void setupListeners() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                renderView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupView() {

    }

    private void renderView(int position) {
        // Title & indicator rendering
    }

    @Override
    public void onZoomStarted() {
        viewPager.setPagingEnabled(false);
    }

    @Override
    public void onZoomStopped() {
        viewPager.setPagingEnabled(true);
    }

    public void setImages(final List<T> images, final ImageViewerLoader<T> loader) {
        ImageViewerViewPagerAdapter adapter = new ImageViewerViewPagerAdapter<>(images, ImageViewerView.this, loader);
        viewPager.setAdapter(adapter);

        int selectedPage = 0;
        viewPager.setCurrentItem(selectedPage, false);
        renderView(selectedPage);
    }
}
