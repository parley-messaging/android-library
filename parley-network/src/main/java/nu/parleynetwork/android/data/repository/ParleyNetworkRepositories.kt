package nu.parleynetwork.android.data.repository;

import nu.parley.android.data.net.ParleyRepositories;
import nu.parley.android.data.repository.DeviceRepository;
import nu.parley.android.data.repository.EventRepository;
import nu.parley.android.data.repository.MessageRepository;

public class ParleyNetworkRepositories implements ParleyRepositories {
    private DeviceRepository deviceRepository = new DeviceRepositoryImpl();
    private EventRepository eventRepository = new EventRepositoryImpl();
    private MessageRepository messageRepository = new MessageRepositoryImpl();

    @Override
    public DeviceRepository getDeviceRepository() {
        return deviceRepository;
    }

    @Override
    public EventRepository getEventRepository() {
        return eventRepository;
    }

    @Override
    public MessageRepository getMessageRepository() {
        return messageRepository;
    }
}
