package nu.parley.android.util.mock

import nu.parley.android.data.model.Action

object MockAction {
    fun create(title: String?, payload: String?): Action {
        return Action(title, payload)
    }
}
