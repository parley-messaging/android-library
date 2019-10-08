package nu.parley.android.view.compose;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import java.io.File;
import java.io.IOException;

import nu.parley.android.R;
import nu.parley.android.util.FileUtil;
import nu.parley.android.util.TakePictureFileProvider;

import static nu.parley.android.view.ParleyView.REQUEST_SELECT_IMAGE;
import static nu.parley.android.view.ParleyView.REQUEST_TAKE_PHOTO;

public final class ComposeImageInputView extends FrameLayout implements View.OnClickListener {

    private AppCompatImageView cameraImageView;

    private File currentPhotoPath;

    public ComposeImageInputView(Context context) {
        super(context);
        init();
    }

    public ComposeImageInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComposeImageInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_compose_input_image, this);

        cameraImageView = findViewById(R.id.camera_image_view);

        setOnClickListener(this);
    }

    @Nullable
    public File getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    @Override
    public void onClick(View v) {
        openImageChooser();
    }

    private void openImageChooser() {
        if (isCameraAvailable()) {
            // Show chooser
            String[] options = new String[]{
                    getContext().getString(R.string.parley_select_photo),
                    getContext().getString(R.string.parley_take_photo)
            };
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.parley_photo)
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                selectImage();
                            } else {
                                openCamera();
                            }
                        }
                    })
                    .setNegativeButton(R.string.parley_general_cancel, null)
                    .show();
        } else {
            // Force select image
            Log.d("ParleyComposeView", "Detected camera not available, disabling camera input.");
            selectImage();
        }
    }

    private boolean isCameraAvailable() {
        return getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void selectImage() {
        String intentFilter = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT;
        Intent intent = new Intent(intentFilter);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(FileUtil.MIME_TYPE_IMAGE);

        String title = getContext().getString(R.string.parley_select_photo);
        launchIntent(Intent.createChooser(intent, title), REQUEST_SELECT_IMAGE);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            try {
                currentPhotoPath = FileUtil.createImageFile(getContext());
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (currentPhotoPath != null) {
                Uri photoURI = TakePictureFileProvider.getUriForFile(getContext(), currentPhotoPath);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                launchIntent(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void launchIntent(Intent intent, int requestCode) {
        ((Activity) getContext()).startActivityForResult(intent, requestCode);
    }

    public void setImageDrawable(Drawable drawable) {
        cameraImageView.setImageDrawable(drawable);
    }

    public void setImageTintList(ColorStateList color) {
        cameraImageView.setSupportImageTintList(color);
    }
}
