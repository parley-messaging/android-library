package nu.parley.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nu.parley.ParleyCustomerAuthorization
import nu.parley.R
import nu.parley.android.Parley
import nu.parley.android.ParleyCallback
import nu.parley.android.ParleyDataCallback
import nu.parley.android.ParleyNetwork
import nu.parley.android.data.messages.ParleyEncryptedDataSource
import nu.parley.android.data.model.ApiVersion
import nu.parley.repository.PreferenceRepository
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import kotlin.time.Duration.Companion.seconds

class IdentifierActivity : BaseActivity() {

    companion object {
        private const val REGEX_IDENTIFIER = "^[a-zA-Z0-9]{20}$"
    }

    private val identifierEditText by lazy { findViewById<EditText>(R.id.identifier_edit_text) }
    private val customerIdEditText by lazy { findViewById<EditText>(R.id.customer_id_edit_text) }
    private val startChatButton by lazy { findViewById<Button>(R.id.start_chat_button) }
    private val startChatLoader by lazy { findViewById<ProgressBar>(R.id.start_chat_loader) }
    private val lightweightButton by lazy { findViewById<Button>(R.id.lightweight_button) }
    private val lightweightLoader by lazy { findViewById<ProgressBar>(R.id.lightweight_loader) }

    private val submitActionListener =
        OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dismissKeyboard(identifierEditText)
                openChatActivity()
                return@OnEditorActionListener true
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identifier)

        setupView()
        setFirebaseToken() // Required
    }

    private fun setupView() {
        val preferenceRepository = PreferenceRepository()

        identifierEditText.setText(preferenceRepository.getIdentifier(this))
        identifierEditText.setOnEditorActionListener(submitActionListener)

        customerIdEditText.setText(preferenceRepository.getCustomerId(this))
        customerIdEditText.setOnEditorActionListener(submitActionListener)

        startChatButton.setOnClickListener {
            openChatActivity()
        }
        lightweightButton.setOnClickListener {
            setParleyNetwork()
            registerUserWithCustomerId()
            Parley.setup(getCurrentIdentifier(), getCurrentDeviceId())
            pollUnreadCount()
//            Parley.registerDevice(object : ParleyCallback {
//                override fun onSuccess() {
//                    pollUnreadCount()
//                }
//
//                override fun onFailure(code: Int?, message: String?) {
//                    TODO("Not yet implemented")
//                }
//            })
        }
    }

    private fun openChatActivity() {
        val identifier = identifierEditText.text.toString()
        if (validateIdentifier(identifier)) {
            val customerId = customerIdEditText.text.toString()

            savePreferences(identifier, customerId)

            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)

            initParley()
        }
    }

    private fun savePreferences(identifier: String, customerId: String) {
        val preferenceRepository = PreferenceRepository()

        preferenceRepository.setIdentifier(this, identifier)
        preferenceRepository.setCustomerId(this, customerId)
    }

    private fun validateIdentifier(identifier: String): Boolean {
        if (identifier.matches(REGEX_IDENTIFIER.toRegex()).not()) {
            showAlertDialog(
                R.string.identifier_invalid_identifier_title,
                R.string.identifier_invalid_identifier_body
            )
            return false
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        startChatButton.isEnabled = true
        startChatLoader.visibility = View.GONE
    }

    private fun getCurrentIdentifier(): String {
        return PreferenceRepository().getIdentifier(this)
    }

    private fun getCurrentDeviceId(): String? {
//        return null
        return "_device_id_3"
    }

    /**
     * Simple sample of polling for the unread count.
     */
    private fun pollUnreadCount() {
        lifecycleScope.launch {
            while (true) {
                delay(5.seconds)
                Parley.getUnseenCount(
                    object : ParleyDataCallback<Int> {
                        override fun onSuccess(data: Int?) {
                            Toast.makeText(this@IdentifierActivity, data.toString(), Toast.LENGTH_LONG).show()
                        }

                        override fun onFailure(code: Int?, message: String?) {
                            Toast.makeText(this@IdentifierActivity, message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }

    private fun initParley() {
//        setParleyNetwork(); // Optional, defaults to Parley configuration
//        setOfflineMessagingEnabled(); // Optional, default off
//        Parley.disableOfflineMessaging();
//        Parley.setReferrer("https://parley.nu/"); // Optional, default `null`

        registerUserWithCustomerId() // Optional, default off

        startChatButton.isEnabled = false
        startChatLoader.visibility = View.VISIBLE

        Parley.configure(this, getCurrentIdentifier(), getCurrentDeviceId(), object : ParleyCallback {
            override fun onSuccess() {
                startChatButton.isEnabled = true
                startChatLoader.visibility = View.GONE
            }

            override fun onFailure(code: Int?, message: String?) {
                Log.d(this::class.toString(), "onFailure: $message")
                startChatButton.isEnabled = true
                startChatLoader.visibility = View.GONE
            }
        })

//        Parley.clearUserInformation(); // Clear user information if needed (for example when the user logs out)
    }

    private fun setFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (task.isSuccessful && task.result != null) {
                Parley.setPushToken(task.result)
            }
        }
    }

    @Suppress("unused")
    private fun setParleyNetwork() {
        val headers: MutableMap<String, String> = HashMap()
        headers["X-Custom-Header"] = "Custom header value"

        val network = ParleyNetwork(
            "https://api.parley.nu/",
            "clientApi/v1.9/",
            ApiVersion.V1_9,
            nu.parley.android.R.xml.parley_network_security_config,
            headers
        )

        Parley.setNetwork(network)
    }

    @Suppress("unused")
    private fun setUserInformation() {
        val additionalInformation: MutableMap<String, String> = HashMap()
        additionalInformation[Parley.ADDITIONAL_VALUE_NAME] = "John Doe"
        additionalInformation[Parley.ADDITIONAL_VALUE_EMAIL] = "j.doe@parley.nu"
        additionalInformation[Parley.ADDITIONAL_VALUE_ADDRESS] = "Randstad 21 30, 1314, Nederland"

        val authorization =
            "ZGFhbnw5ZTA5ZjQ2NWMyMGNjYThiYjMxNzZiYjBhOTZmZDNhNWY0YzVlZjYzMGVhNGZmMWUwMjFjZmE0NTEyYjlmMDQwYTJkMTJmNTQwYTE1YmUwYWU2YTZjNTc4NjNjN2IxMmRjODNhNmU1ODNhODhkMmQwNzY2MGYxZTEzZDVhNDk1Mnw1ZDcwZjM5ZTFlZWE5MTM2YmM3MmIwMzk4ZDcyZjEwNDJkNzUwOTBmZmJjNDM3OTg5ZWU1MzE5MzdlZDlkYmFmNTU1YTcyNTUyZWEyNjllYmI5Yzg5ZDgyZGQ3MDYwYTRjZGYxMzE3NWJkNTUwOGRhZDRmMDA1MTEzNjlkYjkxNQ"
        Parley.setUserInformation(authorization, additionalInformation)
    }

    @Suppress("unused")
    private fun setOfflineMessagingEnabled() {
        Parley.enableOfflineMessaging(ParleyEncryptedDataSource(this, "1234567890123456"))
    }

    /**
     * Registers a user for the chat with the provided customer id.
     * Here, the user authorization is generated by the device itself. However, generation of it
     * should be done elsewhere, as noted in [ParleyCustomerAuthorization].
     *
     *
     * Check out `setUserInformation()` for a simple example when the user authorization is known.
     *
     */
    private fun registerUserWithCustomerId() {
        val customerId = PreferenceRepository().getCustomerId(this)
        if (customerId == null || customerId.trim().isEmpty()) {
            // Unregister, if previously was registered
            Parley.clearUserInformation()
            return
        }

        val sharedSecret =
            "d13acc9e59d422cdec0d71a44bb571a5ab1de02f2e025198a610191fdf831e18ce84569b8af0893b85425a080f3d18055ba1bc44541b9c12373d3ec8045cb320" // Shared secret with Parley
        try {
            val userAuthorization = ParleyCustomerAuthorization().generate(customerId, sharedSecret)
            Parley.setUserInformation(userAuthorization)
        } catch (e: InvalidKeyException) {
            showAlertDialog(
                R.string.default_error_title,
                R.string.identifier_customer_id_error_encryption
            )
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            showAlertDialog(
                R.string.default_error_title,
                R.string.identifier_customer_id_error_encryption
            )
            e.printStackTrace()
        }
    }
}