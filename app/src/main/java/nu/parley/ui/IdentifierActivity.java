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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

import nu.parley.R;
import nu.parley.android.Parley;
import nu.parley.android.ParleyCallback;
import nu.parley.android.ParleyNetwork;
import nu.parley.android.data.messages.ParleyEncryptedDataSource;

public final class IdentifierActivity extends BaseActivity {

    private static final String REGEX_IDENTIFIER = "^[a-zA-Z0-9]{20}$";

    private EditText identifierEditText;
    private Button startChatButton;
    private ProgressBar startChatLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifier);

        identifierEditText = findViewById(R.id.identifier_edit_text);
        startChatButton = findViewById(R.id.start_chat_button);
        startChatLoader = findViewById(R.id.start_chat_loader);

        setupIdentifierEditText();
        setupStartChatButton();
    }

    private void setupIdentifierEditText() {
        identifierEditText.setText(getIdentifierFromSharedPreferences());
        identifierEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dismissKeyboard(identifierEditText);
                    openChatActivity();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupStartChatButton() {
        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatActivity();
            }
        });
    }

    private void openChatActivity() {
        String identifier = identifierEditText.getText().toString();
        if (validateIdentifier(identifier)) {
            saveIdentifierInSharedPreferences(identifier);

            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);

            initParley();
        }
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
        setFirebaseToken(); // Required

//        setParleyNetwork(); // Optional, defaults to Parley configuration
//        setOfflineMessagingEnabled(); // Optional, default off
//        Parley.disableOfflineMessaging();

//        setUserInformation(); // Optional, default off

        startChatButton.setEnabled(false);
        startChatLoader.setVisibility(View.VISIBLE);
        Parley.configure(this, getIdentifierFromSharedPreferences(), new ParleyCallback() {
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
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                String token = null;
                if (task.isSuccessful() && task.getResult() != null) {
                    token = task.getResult().getToken();
                }
                Parley.setFcmToken(token);
            }
        });
    }

    @SuppressWarnings("unused")
    private void setParleyNetwork() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "Custom header value");

        ParleyNetwork network = new ParleyNetwork(
                "https://api.parley.nu/",
                "clientApi/v1.2/",
                R.xml.parley_network_security_config,
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
}