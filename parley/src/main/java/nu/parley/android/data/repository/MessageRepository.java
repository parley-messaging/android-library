package nu.parley.android.data.repository;

import androidx.annotation.Nullable;

import java.util.List;

import nu.parley.android.data.model.Message;
import nu.parley.android.data.net.RepositoryCallback;
import nu.parley.android.data.net.response.ParleyPaging;
import nu.parley.android.data.net.response.ParleyResponse;

public interface MessageRepository {

    public void findAll(final RepositoryCallback<ParleyResponse<List<Message>>> callback);

    public void getOlder(final ParleyPaging previousPaging, final RepositoryCallback<ParleyResponse<List<Message>>> callback);

    public void send(final Message message, @Nullable final String media, final RepositoryCallback<Message> callback);

    public void sendMedia(final Message message, final String media, final RepositoryCallback<Message> callback);

    public void get(final Integer messageId, final RepositoryCallback<Message> callback);
}
