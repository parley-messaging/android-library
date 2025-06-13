package nu.parley.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit

class Preferences(
    private val context: Context,
) {
    companion object {
        private const val KeyIdentifier = "identifier"
        private const val KeyCustomerId = "customer_id"
        private const val DefaultIdentifier = "0W4qcE5aXoKq9OzvHxj2"
    }

    private fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getIdentifier(): String {
        return getPreferences().getString(KeyIdentifier, DefaultIdentifier)!!
    }

    fun getCustomerId(): String? {
        return getPreferences().getString(KeyCustomerId, null)
    }

    fun setIdentifier(identifier: String) {
        setPreferenceString(KeyIdentifier, identifier)
    }

    fun setCustomerId(customerId: String?) {
        if (customerId.isNullOrBlank()) {
            removePreference(KeyCustomerId)
        } else {
            setPreferenceString(KeyCustomerId, customerId)
        }
    }

    private fun setPreferenceString(key: String, value: String) {
        getPreferences()
            .edit {
                putString(key, value)
            }
    }

    private fun removePreference(key: String) {
        getPreferences()
            .edit {
                remove(key)
            }
    }
}
