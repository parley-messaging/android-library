package nu.parley.android.data.repository;

import nu.parley.android.data.model.Device;
import nu.parley.android.data.net.RepositoryCallback;

public interface DeviceRepository {

    public void register(Device device, RepositoryCallback<Void> callback);
}
