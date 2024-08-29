package nu.parleynetwork.android.data.repository

import nu.parley.android.data.net.ParleyRepositories
import nu.parley.android.data.repository.DeviceRepository
import nu.parley.android.data.repository.EventRepository
import nu.parley.android.data.repository.MessageRepository

class ParleyNetworkRepositories : ParleyRepositories {
    private val deviceRepository: DeviceRepository = DeviceRepositoryImpl()
    private val eventRepository: EventRepository = EventRepositoryImpl()
    private val messageRepository: MessageRepository = MessageRepositoryImpl()

    public override fun getDeviceRepository(): DeviceRepository = deviceRepository
    public override fun getEventRepository(): EventRepository = eventRepository
    public override fun getMessageRepository(): MessageRepository = messageRepository
}
