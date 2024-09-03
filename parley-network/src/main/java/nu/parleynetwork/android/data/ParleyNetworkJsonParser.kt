package nu.parleynetwork.android.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nu.parley.android.data.model.Message
import nu.parley.android.data.model.PushEventBody
import nu.parley.android.data.model.PushMessage
import nu.parley.android.data.net.ParleyJsonParser
import nu.parley.android.data.net.response.ParleyPaging
import nu.parley.android.notification.PushNotificationHandler
import org.json.JSONException
import java.lang.reflect.Type

object ParleyNetworkJsonParser : ParleyJsonParser {
    private const val OBJECT: String = "object"
    private val messagesListType: Type = object : TypeToken<List<Message>>() {}.type

    override fun getPushEventBody(data: Map<String, String>): PushEventBody =
        Gson().fromJson(
            getParleyObjectStringValue(data, OBJECT),
            PushEventBody::class.java
        )

    override fun getPushMessageBody(data: Map<String, String>): PushMessage =
        Gson().fromJson(
            getParleyObjectStringValue(data, OBJECT),
            PushMessage::class.java
        )

    private fun getParleyObjectStringValue(data: Map<String, String>, key: String): String? {
        try {
            if (PushNotificationHandler.getParleyObject(data).has(key)) {
                return PushNotificationHandler.getParleyObject(data).getString(key)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    override fun parleyPagingToJson(parleyPaging: ParleyPaging) = Gson().toJson(parleyPaging)

    override fun jsonToParleyPaging(json: String): ParleyPaging =
        Gson().fromJson(json, ParleyPaging::class.java)

    override fun messagesToJson(messages: List<Message>) = Gson().toJson(messages)

    override fun jsonToMessages(json: String): List<Message> =
        Gson().fromJson<List<Message>>(json, messagesListType)
}