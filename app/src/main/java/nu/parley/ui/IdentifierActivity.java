package nu.parley.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import nu.parley.ParleyCustomerAuthorization;
import nu.parley.R;
import nu.parley.android.Parley;
import nu.parley.android.ParleyCallback;
import nu.parley.android.ParleyNetwork;
import nu.parley.android.data.messages.ParleyEncryptedDataSource;
import nu.parley.android.data.model.ApiVersion;
import nu.parley.repository.PreferenceRepository;

public final class IdentifierActivity extends BaseActivity {

    private static final String REGEX_IDENTIFIER = "^[a-zA-Z0-9]{20}$";

    private EditText identifierEditText;
    private EditText customerIdEditText;
    private Button startChatButton;
    private ProgressBar startChatLoader;

    private final TextView.OnEditorActionListener submitActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dismissKeyboard(identifierEditText);
                openChatActivity();
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifier);

        identifierEditText = findViewById(R.id.identifier_edit_text);
        customerIdEditText = findViewById(R.id.customer_id_edit_text);
        startChatButton = findViewById(R.id.start_chat_button);
        startChatLoader = findViewById(R.id.start_chat_loader);

        setupView();
        setFirebaseToken(); // Required
    }

    private void setupView() {
        PreferenceRepository preferenceRepository = new PreferenceRepository();

        identifierEditText.setText(preferenceRepository.getIdentifier(this));
        identifierEditText.setOnEditorActionListener(submitActionListener);

        customerIdEditText.setText(preferenceRepository.getCustomerId(this));
        customerIdEditText.setOnEditorActionListener(submitActionListener);

        startChatButton.setOnClickListener(v -> openChatActivity());
    }

    private void openChatActivity() {
        String identifier = identifierEditText.getText().toString();
        if (validateIdentifier(identifier)) {
            String customerId = customerIdEditText.getText().toString();

            savePreferences(identifier, customerId);

            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);

            initParley();
        }
    }

    private void savePreferences(String identifier, String customerId) {
        PreferenceRepository preferenceRepository = new PreferenceRepository();

        preferenceRepository.setIdentifier(this, identifier);
        preferenceRepository.setCustomerId(this, customerId);
    }

    private Boolean validateIdentifier(String identifier) {
        if (!identifier.matches(REGEX_IDENTIFIER)) {
            showAlertDialog(
                    R.string.identifier_invalid_identifier_title,
                    R.string.identifier_invalid_identifier_body
            );
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startChatButton.setEnabled(true);
        startChatLoader.setVisibility(View.GONE);
    }

    private void initParley() {
//        setParleyNetwork(); // Optional, defaults to Parley configuration
//        setOfflineMessagingEnabled(); // Optional, default off
//        Parley.disableOfflineMessaging();
//        Parley.setReferrer("https://parley.nu/"); // Optional, default `null`

        registerUserWithCustomerId(); // Optional, default off

        startChatButton.setEnabled(false);
        startChatLoader.setVisibility(View.VISIBLE);

        String identifier = new PreferenceRepository().getIdentifier(this);
        Parley.configure(this, identifier, new ParleyCallback() {
            @Override
            public void onSuccess() {
                startChatButton.setEnabled(true);
                startChatLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@Nullable Integer code, @Nullable String message) {
                startChatButton.setEnabled(true);
                startChatLoader.setVisibility(View.GONE);
            }
        });

//        Parley.clearUserInformation(); // Clear user information if needed (for example when the user logs out)
    }

    private void setFirebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Parley.setPushToken(task.getResult());
            }
        });
    }

    @SuppressWarnings("unused")
    private void setParleyNetwork() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "Custom header value");

        ParleyNetwork network = new ParleyNetwork(
                "https://api.parley.nu/",
                "clientApi/v1.7/",
                ApiVersion.V1_7,
                nu.parley.android.R.xml.parley_network_security_config,
                headers
        );

        Parley.setNetwork(network);
    }

    @SuppressWarnings("unused")
    private void setUserInformation() {
        Map<String, String> additionalInformation = new HashMap<>();
        additionalInformation.put(Parley.ADDITIONAL_VALUE_NAME, "John Doe");
        additionalInformation.put(Parley.ADDITIONAL_VALUE_EMAIL, "j.doe@parley.nu");
        additionalInformation.put(Parley.ADDITIONAL_VALUE_ADDRESS, "Randstad 21 30, 1314, Nederland");

        String authorization = "ZGFhbnw5ZTA5ZjQ2NWMyMGNjYThiYjMxNzZiYjBhOTZmZDNhNWY0YzVlZjYzMGVhNGZmMWUwMjFjZmE0NTEyYjlmMDQwYTJkMTJmNTQwYTE1YmUwYWU2YTZjNTc4NjNjN2IxMmRjODNhNmU1ODNhODhkMmQwNzY2MGYxZTEzZDVhNDk1Mnw1ZDcwZjM5ZTFlZWE5MTM2YmM3MmIwMzk4ZDcyZjEwNDJkNzUwOTBmZmJjNDM3OTg5ZWU1MzE5MzdlZDlkYmFmNTU1YTcyNTUyZWEyNjllYmI5Yzg5ZDgyZGQ3MDYwYTRjZGYxMzE3NWJkNTUwOGRhZDRmMDA1MTEzNjlkYjkxNQ";
        Parley.setUserInformation(authorization, additionalInformation);
    }

    @SuppressWarnings("unused")
    private void setOfflineMessagingEnabled() {
        Parley.enableOfflineMessaging(new ParleyEncryptedDataSource(this, "1234567890123456"));
    }

    /**
     * Registers a user for the chat with the provided customer id.
     * Here, the user authorization is generated by the device itself. However, generation of it
     * should be done elsewhere, as noted in {@link ParleyCustomerAuthorization}.
     * <p>
     * Check out `setUserInformation()` for a simple example when the user authorization is known.
     * </p>
     */
    private void registerUserWithCustomerId() {
        String customerId = new PreferenceRepository().getCustomerId(this);
        if (customerId == null || customerId.trim().isEmpty()) {
            // Unregister, if previously was registered
            Parley.clearUserInformation();
            return;
        }

        final String sharedSecret = "d13acc9e59d422cdec0d71a44bb571a5ab1de02f2e025198a610191fdf831e18ce84569b8af0893b85425a080f3d18055ba1bc44541b9c12373d3ec8045cb320"; // Shared secret with Parley
        try {
            String userAuthorization = new ParleyCustomerAuthorization().generate(customerId, sharedSecret);
            Parley.setUserInformation(userAuthorization);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            showAlertDialog(R.string.default_error_title, R.string.identifier_customer_id_error_encryption);
            e.printStackTrace();
        }
    }
}