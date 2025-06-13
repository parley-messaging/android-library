package nu.parley.methods

import android.content.Context
import android.content.Intent
import nu.parley.android.Parley
import nu.parley.android.ParleyCallback

object MethodsBase {

    fun configure(
        context: Context,
        secret: String,
        uniqueDeviceIdentifier: String?,
        onSuccess: () -> Unit,
        onFailure: (code: Int, message: String) -> Unit,
    ) {
        Parley.configure(
            context,
            secret,
            uniqueDeviceIdentifier,
            object : ParleyCallback {
                override fun onSuccess() {
                    onSuccess()
                }

                override fun onFailure(code: Int, message: String) {
                    onFailure(code, message)
                }
            },
        )
    }

    fun setPushToken(token: String) {
        Parley.setPushToken(token)
//        Parley.setPushToken(token, PushType.FCM) // Default
//        Parley.setPushToken(token, PushType.CUSTOM_WEBHOOK)
//        Parley.setPushToken(token, PushType.CUSTOM_WEBHOOK_BEHIND_OAUTH)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return Parley.onActivityResult(requestCode, resultCode, data)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        return Parley.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun handle(context: Context, data: Map<String, String>, intent: Intent): Boolean {
        return Parley.handle(context, data, intent)
    }
}
