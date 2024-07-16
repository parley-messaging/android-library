package nu.parley.android.util.mock

import java.util.UUID

internal object Mock {

    fun uuid() = UUID.randomUUID().toString()
}