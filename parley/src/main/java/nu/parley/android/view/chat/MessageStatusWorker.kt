package nu.parley.android.view.chat

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nu.parley.android.Parley
import nu.parley.android.data.model.Message
import nu.parley.android.data.model.MessageStatus
import nu.parley.android.data.net.RepositoryCallback
import nu.parley.android.data.repository.MessageRepository
import kotlin.time.Duration.Companion.seconds

class MessageStatusWorker {

    private val cached = mutableSetOf<Int>()
    private var current: Job? = null

    @OptIn(DelicateCoroutinesApi::class) // One-offs that we clean up ourself when finished
    fun add(message: Message) {
        val messageId = message.id ?: return
        if (message.status == MessageStatus.Read) return
        if (listOf(
                MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT,
                MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AUTO,
                MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_SYSTEM_AGENT,
            ).contains(message.typeId).not()
        ) return
        cached.add(messageId)
        current?.cancel("Debounce")
        Log.d("STATUS", "Start send $cached")
        current = GlobalScope.launch {
            withContext(Dispatchers.IO) {
                delay(1.seconds)
                Log.d("STATUS", "Sending... $cached")
                MessageRepository().updateStatusRead(cached, object : RepositoryCallback<Void> {
                    override fun onSuccess(data: Void?) {
                        Log.d("STATUS", "Successfully send $cached")
                        Parley.getInstance().messagesManager.updateRead(cached)
                        cached.clear()
                    }

                    override fun onFailed(code: Int?, message: String?) {
                        Log.d("STATUS", "Failed sending $cached: [$code] $message")
                    }
                })
                current = null
            }
        }
    }
}