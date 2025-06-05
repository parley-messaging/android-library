package nu.parley.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import nu.parley.R
import nu.parley.android.data.model.ParleyPosition
import nu.parley.android.view.ParleyView
import nu.parley.methods.MethodsBase
import nu.parley.methods.MethodsStyling
import nu.parley.util.BaseActivity

class ChatActivity : BaseActivity() {

    private val parleyView by lazy { findViewById<ParleyView>(R.id.parley_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

//        MethodsStyling.setMediaDisabled(parleyView)
//        MethodsStyling.setNotificationsPositionBottom(parleyView)

        parleyView.setListener {
            Log.d(
                "ChatActivity",
                "The user did sent a message"
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        @Suppress("UNUSED_VARIABLE")
        val handledByParley = MethodsBase.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        @Suppress("UNUSED_VARIABLE")
        val handledByParley = MethodsBase.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
