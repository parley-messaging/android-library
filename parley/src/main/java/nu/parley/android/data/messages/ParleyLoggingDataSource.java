package nu.parley.android.data.messages;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import nu.parley.android.data.model.Message;

/**
 * A simple implementation of a data source for Parley by just logging the callbacks. In fact, it does not cache items at all.
 */
@SuppressWarnings("unused")
public final class ParleyLoggingDataSource implements ParleyDataSource {

    @Override
    public void clear() {
        Log.d("ParleyLoggingDataSource", "clear");
    }

    @Override
    public List<Message> getAll() {
        Log.d("ParleyLoggingDataSource", "get all messages");
        return new ArrayList<>();
    }

    @Override
    public void add(List<Message> messages) {
        Log.d("ParleyLoggingDataSource", "add messages");
    }

    @Override
    public void add(int index, List<Message> messages) {
        Log.d("ParleyLoggingDataSource", "add messages at index: " + index);
    }

    @Override
    public void add(Message message) {
        Log.d("ParleyLoggingDataSource", "add message");
    }

    @Override
    public void add(int index, Message message) {
        Log.d("ParleyLoggingDataSource", "add message at index: " + index);
    }

    @Override
    public void update(Message message) {
        Log.d("ParleyLoggingDataSource", "update message");
    }

    @Override
    public void set(String key, @Nullable String value) {
        Log.d("ParleyLoggingDataSource", "set `" + key + "` to value: " + value);
    }

    @Nullable
    @Override
    public String get(String key) {
        Log.d("ParleyLoggingDataSource", "get `" + key + "`. Returning null as logger.");
        return null;
    }
}
