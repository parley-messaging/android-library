package nu.parley.util

import android.view.View

fun View.setVisible(visible: Boolean? = true) {
    visibility = if (visible == true) View.VISIBLE else View.GONE
}

fun View.setGone(gone: Boolean? = true) {
    visibility = if (gone == true) View.GONE else View.VISIBLE
}