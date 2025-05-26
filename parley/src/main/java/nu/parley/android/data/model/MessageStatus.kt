package nu.parley.android.data.model

enum class MessageStatus(
    val key: Int,
) {
    Send(2),
    Received(3),
    Read(4),
    ;

    companion object {
        fun from(key: Int) = entries.firstOrNull { it.key == key }
    }
}