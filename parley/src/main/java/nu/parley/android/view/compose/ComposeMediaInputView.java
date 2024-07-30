package nu.parley.android.view.compose;

import static nu.parley.android.view.ParleyView.REQUEST_SELECT_MEDIA;
import static nu.parley.android.view.ParleyView.REQUEST_TAKE_PHOTO;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.parley.android.Parley;
import nu.parley.android.ParleyLaunchCallback;
import nu.parley.android.R;
import nu.parley.android.data.model.MimeType;
import nu.parley.android.util.FileUtil;
import nu.parley.android.util.ParleyPermissionUtil;
import nu.parley.android.util.TakePictureFileProvider;
import nu.parley.android.view.ParleyView;

public final class ComposeMediaInputView extends FrameLayout implements View.OnClickListener {

    private AppCompatImageView addMediaView;
    private ParleyLaunchCallback launchCallback;

    private File currentPhotoPath;

    public ComposeMediaInputView(Context context) {
        super(context);
        init();
    }

    public ComposeMediaInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComposeMediaInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_compose_media, this);

        addMediaView = findViewById(R.id.add_media_view);

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
        String optionCamera = getContext().getString(R.string.parley_media_camera);
        String optionGallery = getContext().getString(R.string.parley_media_gallery);
        String optionDocument = getContext().getString(R.string.parley_media_document);

        List<String> options = new ArrayList<>();
        if (isCameraAvailable()) {
            options.add(optionCamera);
        }
        options.add(optionGallery);
        if (Parley.getInstance().getNetwork().apiVersion.isSupportingPdf()) {
            options.add(optionDocument);
        }

        // Show chooser
        String[] optionsArray = new String[options.size()];
        options.toArray(optionsArray);
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.parley_media_select)
                .setItems(optionsArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = options.get(which);
                        if (optionDocument.equals(selected)) {
                            selectDocument();
                        } else if (optionGallery.equals(selected)) {
                            selectImage();
                        } else if (optionCamera.equals(selected)) {
                            checkCameraAccess();
                        } else {
                            Log.d("ParleyComposeView", "Unhandled selection: " + selected);
                        }
                    }
                })
                .setNegativeButton(R.string.parley_general_cancel, null)
                .show();
    }

    private boolean isCameraAvailable() {
        return getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void selectDocument() {
        select(MimeType.Companion.getDocuments());
    }

    private void selectImage() {
        select(MimeType.Companion.getImages());
    }

    private void select(List<MimeType> mimeTypes) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
//        intent.setType(FileUtil.MIME_TYPE_IMAGE);
        String[] mimeTypeStrings = new String[mimeTypes.size()];
        for (int i = 0; i < mimeTypes.size(); i++) {
            mimeTypeStrings[i] = mimeTypes.get(i).getValue();
        }
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypeStrings);

        String title = ""; // getContext().getString(R.string.parley_media_gallery);
        launchIntent(Intent.createChooser(intent, title), REQUEST_SELECT_MEDIA);
    }

    private void checkCameraAccess() {
        if (ParleyPermissionUtil.shouldRequestPermission(getContext(), Manifest.permission.CAMERA)) {
            launchCallback.launchParleyPermissionRequest(
                    new String[]{Manifest.permission.CAMERA},
                    ParleyView.REQUEST_PERMISSION_ACCESS_CAMERA
            );
        } else {
            openCamera();
        }
    }

    void openCamera() {
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
        launchCallback.launchParleyActivityForResult(intent, requestCode);
    }

    public void setImageDrawable(Drawable drawable) {
        addMediaView.setImageDrawable(drawable);
    }

    public void setImageTintList(ColorStateList color) {
        ImageViewCompat.setImageTintList(addMediaView, color);
    }

    public void setLaunchCallback(@NonNull ParleyLaunchCallback launchCallback) {
        this.launchCallback = launchCallback;
    }
}
