package nu.parley.android.data.repository

import nu.parley.android.data.net.ParleyRepositories

object DefaultParleyRepositories : ParleyRepositories {
    private val deviceRepository: DeviceRepository = DefaultDeviceRepository()
    private val eventRepository: EventRepository = DefaultEventRepository()
    private val messageRepository: MessageRepository = DefaultMessageRepository()

    public override fun getDeviceRepository(): DeviceRepository = deviceRepository
    public override fun getEventRepository(): EventRepository = eventRepository
    public override fun getMessageRepository(): MessageRepository = messageRepository
}
