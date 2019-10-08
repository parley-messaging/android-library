package nu.parley.android.imageviewer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;


final class ImageViewerViewPager extends ViewPager {

    private boolean pagingEnabled = true;

    public ImageViewerViewPager(@NonNull Context context) {
        super(context);
    }

    public ImageViewerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return this.pagingEnabled = super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.pagingEnabled && super.onInterceptTouchEvent(ev);
    }

    public void setPagingEnabled(boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
    }
}
