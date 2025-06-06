package nu.parley.methods

import nu.parley.android.data.model.ParleyPosition
import nu.parley.android.view.ParleyView

@Suppress("unused")
object MethodsStyling {

    fun setMediaDisabled(parleyView: ParleyView) {
        parleyView.setMediaEnabled(false) // Optional, default is `true`
    }

    fun setNotificationsPositionBottom(parleyView: ParleyView) {
        parleyView.setNotificationsPosition(ParleyPosition.Vertical.BOTTOM) // Optional, default is `TOP`
    }
}
