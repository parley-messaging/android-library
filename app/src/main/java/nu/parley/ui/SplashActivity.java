package nu.parley.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import nu.parley.BuildConfig;
import nu.parley.R;

public final class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        openIdentifierActivity();
    }

    private void openIdentifierActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, IdentifierActivity.class);

            startActivity(intent);
        }, BuildConfig.DEBUG ? 1000 : 2500);
    }
}