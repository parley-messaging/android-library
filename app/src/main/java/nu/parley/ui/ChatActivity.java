package nu.parley.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import nu.parley.R;
import nu.parley.android.Parley;
import nu.parley.android.data.model.ParleyPosition;
import nu.parley.android.view.ParleyView;

public final class ChatActivity extends BaseActivity {

    private ParleyView parleyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupToolbar();

        parleyView = findViewById(R.id.parley_view);

        setParleyViewSettings();
    }

    /**
     * Applies settings to the ParleyView.
     * <p>
     * All of these settings are optional.
     */
    private void setParleyViewSettings() {
//        parleyView.setMediaEnabled(false); // Optional, default `true`
//        parleyView.setNotificationsPosition(ParleyPosition.Vertical.BOTTOM); // Optional, default `TOP`

        parleyView.setListener(() -> Log.d("ChatActivity", "The user did sent a message"));
    }

    @Override
    @SuppressWarnings("unused")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean handledByParley = Parley.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    @SuppressWarnings("unused")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean handledByParley = Parley.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
