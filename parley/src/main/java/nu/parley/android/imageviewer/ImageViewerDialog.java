package nu.parley.android.imageviewer;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import nu.parley.android.R;

final class ImageViewerDialog<T> {

    private BuilderData<T> builderData;
    private AlertDialog dialog;
    private ImageViewerView<T> view;
    private boolean animate = true;

    ImageViewerDialog(Context context, BuilderData<T> builderData) {
        this.builderData = builderData;
        view = new ImageViewerView<>(context);

        view.findViewById(R.id.close_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setImages(builderData.images, builderData.loader);

        dialog = new AlertDialog.Builder(context, R.style.ImageViewer)
                .setView(view)
                .create();

    }

    void show(boolean animate) {
        this.animate = animate;
        dialog.show();
    }
}
