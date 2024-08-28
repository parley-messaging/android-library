package nu.parley.android.data.net;

import nu.parley.android.data.repository.DeviceRepository;
import nu.parley.android.data.repository.EventRepository;

public interface ParleyRepositories {

    public DeviceRepository getDeviceRepository();;

    public EventRepository getEventRepository();
}
