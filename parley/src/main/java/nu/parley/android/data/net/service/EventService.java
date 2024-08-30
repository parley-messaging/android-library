package nu.parley.android.data.net.service;

import nu.parley.android.data.net.RepositoryCallback;

public interface EventService {

    void fire(RepositoryCallback<Void> callback, String event);
}
