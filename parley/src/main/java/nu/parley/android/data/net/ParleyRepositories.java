package nu.parley.android.data.net;

import nu.parley.android.data.repository.DeviceRepository;
import nu.parley.android.data.repository.EventRepository;
import nu.parley.android.data.repository.MessageRepository;

public interface ParleyRepositories {

    public DeviceRepository getDeviceRepository();;

    public EventRepository getEventRepository();

    public MessageRepository getMessageRepository();
}
