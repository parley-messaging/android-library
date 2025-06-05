package nu.parley.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView.OnEditorActionListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import nu.parley.R
import nu.parley.Settings
import nu.parley.methods.MethodsAdvanced
import nu.parley.methods.MethodsBase
import nu.parley.methods.MethodsSpecial
import nu.parley.model.Flow
import nu.parley.util.BaseActivity
import nu.parley.util.setGone
import nu.parley.util.setVisible

class IdentifierActivity : BaseActivity() {

    companion object {
        private const val REGEX_IDENTIFIER = "^[a-zA-Z0-9]{20}$"
    }

    private val identifierEditText by lazy { findViewById<EditText>(R.id.identifier_edit_text) }
    private val customerIdEditText by lazy { findViewById<EditText>(R.id.customer_id_edit_text) }

    // Flow: Default
    private val flowDefaultLayout by lazy { findViewById<ViewGroup>(R.id.flow_default_layout) }
    private val defaultStartButton by lazy { findViewById<Button>(R.id.default_start_button) }
    private val defaultStartLoader by lazy { findViewById<ProgressBar>(R.id.default_start_loader) }

    // Flow: SpecialLightweight
    private val flowLightweightLayout by lazy { findViewById<ViewGroup>(R.id.flow_lightweight_layout) }
    private val lightweightSetup by lazy { findViewById<Button>(R.id.lightweight_setup) }
    private val lightweightSetupLoader by lazy { findViewById<ProgressBar>(R.id.lightweight_setup_loader) }
    private val lightweightOpen by lazy { findViewById<Button>(R.id.lightweight_open) }
    private val lightweightOpenLoader by lazy { findViewById<ProgressBar>(R.id.lightweight_open_loader) }
    private val lightweightRegisterDevice by lazy { findViewById<Button>(R.id.lightweight_register_device) }
    private val lightweightRegisterDeviceLoader by lazy { findViewById<ProgressBar>(R.id.lightweight_register_device_loader) }
    private val lightweightGetUnseen by lazy { findViewById<Button>(R.id.lightweight_get_unseen) }
    private val lightweightGetUnseenLoader by lazy { findViewById<ProgressBar>(R.id.lightweight_get_unseen_loader) }

    private val submitActionListener =
        OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dismissKeyboard(identifierEditText)
                return@OnEditorActionListener true
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identifier)

        setupView()
        setupSettings()
        setupFlowDefault()
        setupFlowLightweight()
    }

    private fun setupView() {
        identifierEditText.setText(preferences.getIdentifier())
        identifierEditText.setOnEditorActionListener(submitActionListener)

        customerIdEditText.setText(preferences.getCustomerId())
        customerIdEditText.setOnEditorActionListener(submitActionListener)
    }

    private fun setupFlowDefault() {
        flowDefaultLayout.setVisible(Settings.flow is Flow.Default)
        if (Settings.flow !is Flow.Default) return
        defaultStartButton.setText(if (Settings.flow.openChatDirectly) R.string.identifier_open_then_start else R.string.identifier_start_then_open)
        defaultStartButton.setOnClickListener {
            configureAndOpen(Settings.flow.openChatDirectly)
        }
    }

    private fun setupFlowLightweight() {
        flowLightweightLayout.setVisible(Settings.flow is Flow.SpecialLightweight)
        lightweightSetup.setOnClickListener {
            lightweightSetupLoader.setVisible()
            MethodsAdvanced.setNetwork()
            MethodsAdvanced.setUserAuthorizationGenerate(preferences.getCustomerId())
            MethodsSpecial.setup(this, preferences.getIdentifier(), null)
            showToast(R.string.identifier_lightweight_message_setup_completed)
            lightweightSetupLoader.setGone()
        }
        lightweightOpen.setOnClickListener {
            configureAndOpen(true)
        }
        lightweightRegisterDevice.setOnClickListener {
            lightweightRegisterDeviceLoader.setVisible()
            MethodsSpecial.registerDevice(
                {
                    lightweightRegisterDeviceLoader.setGone()
                    showToast(R.string.identifier_lightweight_message_device_registered)
                },
                { code, message ->
                    lightweightRegisterDeviceLoader.setGone()
                    showToast("$code: $message")
                }
            )
        }
        lightweightGetUnseen.setOnClickListener {
            lightweightGetUnseenLoader.setVisible()
            MethodsAdvanced.getUnseenCount(
                { count ->
                    lightweightGetUnseenLoader.setGone()
                    showToast(getString(R.string.identifier_lightweight_message_x_unseen_messages, count))
                },
                { code, message ->
                    lightweightGetUnseenLoader.setGone()
                    showToast("$code: $message")
                }
            )
        }
    }

    private fun savePreferences(identifier: String, customerId: String) {
        preferences.setIdentifier(identifier)
        preferences.setCustomerId(customerId)
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
        defaultStartButton.isEnabled = true
        defaultStartLoader.visibility = View.GONE
    }

    private fun setupSettings() {
//        MethodsAdvanced.setNetwork() // Optional, defaults to Parley configuration
//        MethodsAdvanced.setReferrer() // Optional, default `null`
        if (Settings.offlineMessaging) {
            MethodsAdvanced.enableOfflineMessaging(this) // Optional, default is disabled
        } else {
            MethodsAdvanced.disableOfflineMessaging() // Optional, default is disabled
        }
        MethodsAdvanced.setUserAuthorizationGenerate(preferences.getCustomerId()) // Optional, default anonymous

        setFirebaseToken() // Required
    }

    private fun setFirebaseToken() {
        FirebaseMessaging.getInstance()
            .token
            .addOnCompleteListener { task: Task<String?> ->
                val token = task.result
                if (task.isSuccessful && token != null) {
                    MethodsBase.setPushToken(token)
                }
            }
    }

    private fun openChatActivity() {
        val identifier = identifierEditText.text.toString()
        if (validateIdentifier(identifier)) {
            val customerId = customerIdEditText.text.toString()

            savePreferences(identifier, customerId)

            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configureAndOpen(openDirectly: Boolean) {
        if (openDirectly) {
            openChatActivity()
        }

        defaultStartLoader.setVisible()
        lightweightOpenLoader.setVisible()
        MethodsBase.configure(
            this,
            preferences.getIdentifier(),
            null,
            {
                if (openDirectly.not()) {
                    openChatActivity()
                }
                lightweightOpenLoader.setGone()
                defaultStartLoader.setGone()
            },
            { code, message ->
                showToast("$code: $message")
                lightweightOpenLoader.setGone()
                defaultStartLoader.setGone()
            }
        )
    }
}
