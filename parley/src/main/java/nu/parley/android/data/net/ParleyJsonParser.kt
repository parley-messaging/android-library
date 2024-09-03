package nu.parley.android.data.net

import nu.parley.android.data.model.Message
import nu.parley.android.data.model.PushEventBody
import nu.parley.android.data.model.PushMessage
import nu.parley.android.data.net.response.ParleyPaging

interface ParleyJsonParser {

    fun getPushEventBody(data: Map<String, String>): PushEventBody

    fun getPushMessageBody(data: Map<String, String>): PushMessage

    fun parleyPagingToJson(parleyPaging: ParleyPaging): String

    fun jsonToParleyPaging(json: String): ParleyPaging

    fun messagesToJson(messages: List<Message>): String

    fun jsonToMessages(json: String): List<Message>
}