package nu.parley.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nu.parley.R
import nu.parley.android.Parley
import nu.parley.android.ParleyDataCallback
import nu.parley.android.view.ParleyView
import kotlin.time.Duration.Companion.seconds

class ChatActivity : BaseActivity() {

    private val parleyView by lazy {
        findViewById<ParleyView>(R.id.parley_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setupToolbar()

        setParleyViewSettings()
//        pollUnreadCount()
    }

    /**
     * Applies settings to the ParleyView.
     *
     *
     * All of these settings are optional.
     */
    private fun setParleyViewSettings() {
//        parleyView.setMediaEnabled(false); // Optional, default `true`
//        parleyView.setNotificationsPosition(ParleyPosition.Vertical.BOTTOM); // Optional, default `TOP`

        parleyView.setListener {
            Log.d(
                "ChatActivity",
                "The user did sent a message"
            )
        }
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
                            Toast.makeText(this@ChatActivity, data.toString(), Toast.LENGTH_LONG).show()
                        }

                        override fun onFailure(code: Int?, message: String?) {
                            Toast.makeText(this@ChatActivity, message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }

    @Suppress("unused")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val handledByParley = Parley.onActivityResult(requestCode, resultCode, data)
    }

    @Suppress("unused")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val handledByParley =
            Parley.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
