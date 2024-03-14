package nu.parley.android.view.compose;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import nu.parley.android.ParleyLaunchCallback;
import nu.parley.android.R;
import nu.parley.android.util.FileUtil;
import nu.parley.android.util.StyleUtil;

public final class ParleyComposeView extends FrameLayout implements View.OnClickListener {

    private static final float ALPHA_STATE_DISABLED = 0.7f;
    private static final float MAX_MEDIA_SIZE = 10_000_000; // 10 * 1024 * 1024; // 10MB, but with 0's

    private ViewGroup inputLayout;
    private EditText inputEditText;
    private ComposeImageInputView imageInputView;
    private FloatingActionButton sendButton;

    private ComposeListener listener;

    private long lastStartTypingTrigger;
    private long startTypingTriggerInterval = 5 * 1000; // Default: 5 seconds
    private long stopTypingTriggerTime = 5 * 1000; // Default: 5 seconds
    private Handler currentlyTypingHandler = new Handler();
    private Runnable currentlyTypingRunnable = null;

    // Styling
    private StyleUtil.StyleSpacing inputPadding;

    public ParleyComposeView(Context context) {
        super(context);
        init(context, null);
    }

    public ParleyComposeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ParleyComposeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.view_compose, this);

        // Views
        inputLayout = findViewById(R.id.input_layout);
        inputEditText = findViewById(R.id.input_edit_text);
        imageInputView = findViewById(R.id.image_input_view);
        sendButton = findViewById(R.id.send_button);

        // Logic
        applyStyle(context, attrs);

        addInputListener();
        sendButton.setOnClickListener(this);
        checkSendButtonEnabled();
    }

    private void applyStyle(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(R.style.ParleyComposeViewStyle, R.styleable.ParleyComposeView);

            setBackground(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyComposeView_parley_background));
            StyleUtil.Helper.applyBackgroundColor(this, ta, R.styleable.ParleyComposeView_parley_background_tint_color);

            StyleUtil.StyleSpacing styleSpacingPadding = StyleUtil.getSpacingData(ta, R.styleable.ParleyComposeView_parley_content_padding, R.styleable.ParleyComposeView_parley_content_padding_top, R.styleable.ParleyComposeView_parley_content_padding_right, R.styleable.ParleyComposeView_parley_content_padding_bottom, R.styleable.ParleyComposeView_parley_content_padding_left);
            setPadding(styleSpacingPadding.left, styleSpacingPadding.top, styleSpacingPadding.right, styleSpacingPadding.bottom);

            inputPadding = StyleUtil.getSpacingData(ta, R.styleable.ParleyComposeView_parley_input_padding, R.styleable.ParleyComposeView_parley_input_padding_top, R.styleable.ParleyComposeView_parley_input_padding_right, R.styleable.ParleyComposeView_parley_input_padding_bottom, R.styleable.ParleyComposeView_parley_input_padding_left);
            inputEditText.setPadding(inputPadding.left, inputPadding.top, inputPadding.right, inputPadding.bottom);

            Typeface font = StyleUtil.getFont(getContext(), ta, R.styleable.ParleyComposeView_parley_font_family);
            int fontStyle = StyleUtil.getFontStyle(ta, R.styleable.ParleyComposeView_parley_font_style);
            inputEditText.setTypeface(font, fontStyle);
            inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, StyleUtil.getDimension(ta, R.styleable.ParleyComposeView_parley_text_size));
            inputEditText.setTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyComposeView_parley_text_color));
            inputEditText.setHintTextColor(StyleUtil.getColorStateList(ta, R.styleable.ParleyComposeView_parley_hint_text_color));

            inputLayout.setBackground(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyComposeView_parley_input_background));
            StyleUtil.Helper.applyBackgroundColor(inputLayout, ta, R.styleable.ParleyComposeView_parley_input_background_tint_color);
            sendButton.setBackgroundTintList(StyleUtil.getColorStateList(ta, R.styleable.ParleyComposeView_parley_send_background_tint_color));
            sendButton.setImageDrawable(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyComposeView_parley_send_icon));
            sendButton.setSupportImageTintList(StyleUtil.getColorStateList(ta, R.styleable.ParleyComposeView_parley_send_icon_tint_color));
            imageInputView.setImageDrawable(StyleUtil.getDrawable(getContext(), ta, R.styleable.ParleyComposeView_parley_camera_icon));
            imageInputView.setImageTintList(StyleUtil.getColorStateList(ta, R.styleable.ParleyComposeView_parley_camera_icon_tint_color));

            ta.recycle();
        }
    }

    public void setListener(ComposeListener listener) {
        this.listener = listener;
    }

    public void setHintText(@StringRes int resource) {
        inputEditText.setHint(resource);
    }

    public void setHintText(String text) {
        inputEditText.setHint(text);
    }

    public void setStartTypingTriggerInterval(long millis) {
        this.startTypingTriggerInterval = millis;
    }

    public void setStopTypingTriggerTime(long millis) {
        this.stopTypingTriggerTime = millis;
    }

    private void addInputListener() {
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    updateCurrentlyTyping();
                }
                checkSendButtonEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkSendButtonEnabled() {
        boolean enabled = !inputEditText.getText().toString().isEmpty();

        sendButton.setEnabled(enabled);
        sendButton.setAlpha(enabled ? 1f : ALPHA_STATE_DISABLED);
    }

    private void updateCurrentlyTyping() {
        if (listener == null) {
            // Ignore: Nothing is interested in our callbacks currently
            return;
        }

        if (System.currentTimeMillis() - lastStartTypingTrigger > startTypingTriggerInterval) {
            // Trigger started typing (again)
            listener.onStartedTyping();
            lastStartTypingTrigger = System.currentTimeMillis();
        }

        if (currentlyTypingRunnable == null) {
            currentlyTypingRunnable = new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        currentlyTypingRunnable = null;
                        listener.onStoppedTyping();
                    }
                }
            };
        } else {
            currentlyTypingHandler.removeCallbacks(currentlyTypingRunnable);
        }
        currentlyTypingHandler.postDelayed(currentlyTypingRunnable, stopTypingTriggerTime);
    }

    @Override
    public void onClick(View v) {
        if (v == sendButton) {
            String message = inputEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                inputEditText.setText(null);
                if (listener != null) {
                    listener.onSendMessage(message);
                }
            }
        }
    }

    public void submitCreatedImage() {
        final File photoPath = imageInputView.getCurrentPhotoPath();
        if (photoPath != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    listener.onSendImage(photoPath);
                }
            });
        }
    }

    public void submitSelectedImage(Intent data) {
        if (data != null && data.getData() != null) {
            Uri uri = data.getData();

            final File file = FileUtil.getFileFromContentUri(getContext(), uri);
            if (file == null) {
                showAlert(R.string.parley_send_failed_body_media_invalid);
            } else if (file.length() > MAX_MEDIA_SIZE) {
                showAlert(R.string.parley_send_failed_body_media_too_large);
            } else {
                post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSendImage(file);
                    }
                });
            }
        }
    }

    private void showAlert(@StringRes int message) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.parley_send_failed_title)
                .setMessage(message)
                .setNegativeButton(R.string.parley_general_ok, null)
                .show();
    }

    public void onCameraPermissionGranted() {
        imageInputView.openCamera();
    }

    public void setImagesEnabled(boolean enabled) {
        imageInputView.setVisibility(enabled ? View.VISIBLE : View.GONE);

        FrameLayout.LayoutParams imageInputLayoutParams = new FrameLayout.LayoutParams(imageInputView.getLayoutParams());
        imageInputLayoutParams.gravity = ((LayoutParams) imageInputView.getLayoutParams()).gravity;
        imageInputLayoutParams.setMargins(0, inputPadding.top, inputPadding.right, inputPadding.bottom);//inputPadding.right, inputPadding.bottom);
        imageInputView.setLayoutParams(imageInputLayoutParams);
        int rightPaddingAddition = enabled ? StyleUtil.dpToPx(30) : 0;
        inputEditText.setPadding(inputPadding.left, inputPadding.top, inputPadding.right + rightPaddingAddition, inputPadding.bottom);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            setAlpha(1f);
            checkSendButtonEnabled();
        } else {
            setAlpha(ALPHA_STATE_DISABLED);
            sendButton.setAlpha(1f); // Don't visually disable this twice
        }
        inputEditText.setEnabled(enabled);
        imageInputView.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }

    public void setLaunchCallback(@NonNull ParleyLaunchCallback launchCallback) {
        imageInputView.setLaunchCallback(launchCallback);
    }
}
