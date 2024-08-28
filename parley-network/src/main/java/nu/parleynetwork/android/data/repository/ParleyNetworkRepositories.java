package nu.parleynetwork.android.data.repository;

import nu.parley.android.data.net.ParleyRepositories;
import nu.parley.android.data.repository.DeviceRepository;

public class ParleyNetworkRepositories implements ParleyRepositories {
    private DeviceRepository deviceRepository = new DeviceRepositoryImpl();

    @Override
    public DeviceRepository getDeviceRepository() {
        return deviceRepository;
    }
}
