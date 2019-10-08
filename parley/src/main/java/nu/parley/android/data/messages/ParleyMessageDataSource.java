package nu.parley.android.data.messages;

import java.util.List;

import nu.parley.android.data.model.Message;

/**
 * Provides the methods that are needed to enable caching of messages for Parley.
 */
public interface ParleyMessageDataSource {

    /**
     * Clears everything in the cache.
     */
    void clear();

    /**
     * Retrieve all messages from the cache.
     *
     * @return The cached messages
     */
    List<Message> getAll();

    /**
     * Add messages to the cache.
     *
     * @param messages Messages to be added to the cache.
     */
    void add(List<Message> messages);

    /**
     * Add messages to the cache at index.
     *
     * @param index    Index at which the messages should be added.
     * @param messages Messages to be added.
     */
    void add(int index, List<Message> messages);

    /**
     * Add a single message to the cache.
     *
     * @param message Message to be added to the cache.
     */
    void add(Message message);

    /**
     * Add a single message to the cache at index.
     *
     * @param index   Index at which the message should be added.
     * @param message Message to be added.
     */
    void add(int index, Message message);

    /**
     * Update a single message in the cache.
     * The existing message should be looked up by using {@link Message#getUuid()}, since the id of a message is nullable and can vary.
     *
     * @param message Updated message.
     */
    void update(Message message);
}
