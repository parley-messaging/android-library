package nu.parley.methods

import android.content.Context
import nu.parley.android.Parley
import nu.parley.android.ParleyCallback

@Suppress("unused")
object MethodsSpecial {

    fun setup(context: Context, secret: String, uniqueDeviceIdentifier: String?) {
        Parley.setup(context, secret, uniqueDeviceIdentifier)
    }

    fun registerDevice(
        onSuccess: () -> Unit,
        onFailure: (code: Int, message: String) -> Unit,
    ) {
        Parley.registerDevice(object : ParleyCallback {
            override fun onSuccess() {
                onSuccess()
            }

            override fun onFailure(code: Int, message: String) {
                onFailure(code, message)
            }
        })
    }

    fun purgeLocalMemory() {
        Parley.purgeLocalMemory()
    }
}
