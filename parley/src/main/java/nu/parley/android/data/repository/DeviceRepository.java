package nu.parley.android.data.repository;

import android.content.Context;

import java.util.UUID;

import nu.parley.android.Parley;
import nu.parley.android.data.model.Device;
import nu.parley.android.data.net.Connectivity;
import nu.parley.android.data.net.RepositoryCallback;
import nu.parley.android.data.net.service.DeviceService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public interface DeviceRepository {

    public void register(Device device, RepositoryCallback<Void> callback);
}
