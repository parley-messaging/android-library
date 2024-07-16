package nu.parley.android.data.repository;

import java.io.File;
import java.util.List;

import nu.parley.android.data.model.Media;
import nu.parley.android.data.model.Message;
import nu.parley.android.data.model.MimeType;
import nu.parley.android.data.net.Connectivity;
import nu.parley.android.data.net.RepositoryCallback;
import nu.parley.android.data.net.response.ParleyResponsePostMedia;
import nu.parley.android.data.net.response.ParleyResponsePostMessage;
import nu.parley.android.data.net.response.ParleyPaging;
import nu.parley.android.data.net.response.ParleyResponse;
import nu.parley.android.data.net.service.MessageService;
import nu.parley.android.util.FileUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static nu.parley.android.data.model.Message.SEND_STATUS_SUCCESS;

import androidx.annotation.Nullable;

public final class MessageRepository {

    public void findAll(final RepositoryCallback<ParleyResponse<List<Message>>> callback) {
        Call<ParleyResponse<List<Message>>> messagesCall = Connectivity.getRetrofit().create(MessageService.class).findAll();

        messagesCall.enqueue(new Callback<ParleyResponse<List<Message>>>() {
            @Override
            public void onResponse(Call<ParleyResponse<List<Message>>> call, Response<ParleyResponse<List<Message>>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response));
                }
            }

            @Override
            public void onFailure(Call<ParleyResponse<List<Message>>> call, Throwable t) {
                t.printStackTrace();
                callback.onFailed(null, t.getMessage());
            }
        });
    }

    public void getOlder(final ParleyPaging previousPaging, final RepositoryCallback<ParleyResponse<List<Message>>> callback) {
        Call<ParleyResponse<List<Message>>> messagesCall = Connectivity.getRetrofit().create(MessageService.class).getOlder(previousPaging.getBefore());

        messagesCall.enqueue(new Callback<ParleyResponse<List<Message>>>() {
            @Override
            public void onResponse(Call<ParleyResponse<List<Message>>> call, Response<ParleyResponse<List<Message>>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response));
                }
            }

            @Override
            public void onFailure(Call<ParleyResponse<List<Message>>> call, Throwable t) {
                t.printStackTrace();
                callback.onFailed(null, t.getMessage());
            }
        });
    }

    public void send(final Message message, @Nullable final String media, final RepositoryCallback<Message> callback) {
        Call<ParleyResponse<ParleyResponsePostMessage>> messagesCall;
        if (media == null) {
            // Text or media message
            messagesCall = Connectivity.getRetrofit().create(MessageService.class).post(message);
        } else {
            // Image message API V1.2: Uploading it together when sending the message
            File file = new File(media);
            String mediaType = FileUtil.getMimeType(file.getAbsolutePath());
            MimeType mimeType = MimeType.Companion.fromValue(mediaType);
            RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType.getValue()), media);

            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
            messagesCall = Connectivity.getRetrofit().create(MessageService.class).postImage(filePart);
        }

        messagesCall.enqueue(new Callback<ParleyResponse<ParleyResponsePostMessage>>() {
            @Override
            public void onResponse(Call<ParleyResponse<ParleyResponsePostMessage>> call, Response<ParleyResponse<ParleyResponsePostMessage>> response) {
                if (response.isSuccessful()) {
                    Message updatedMessage = Message.withIdAndStatus(message, response.body().getData().getMessageId(), SEND_STATUS_SUCCESS);
                    callback.onSuccess(updatedMessage);
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response));
                }
            }

            @Override
            public void onFailure(Call<ParleyResponse<ParleyResponsePostMessage>> call, Throwable t) {
                t.printStackTrace();
                callback.onFailed(null, t.getMessage());
            }
        });
    }

    public void sendMedia(final Message message, final String media, final RepositoryCallback<Message> callback) {
        // API V1.6+: Uploading media
        File file = new File(media);
        String mediaType = FileUtil.getMimeType(media);
        MimeType mimeType = MimeType.Companion.fromValue(mediaType);
        RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType.getValue()), file);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("media", file.getName(), requestBody);

        Call<ParleyResponse<ParleyResponsePostMedia>> messagesCall = Connectivity.getRetrofit().create(MessageService.class).postMedia(filePart);
        messagesCall.enqueue(new Callback<ParleyResponse<ParleyResponsePostMedia>>() {
            @Override
            public void onResponse(Call<ParleyResponse<ParleyResponsePostMedia>> call, Response<ParleyResponse<ParleyResponsePostMedia>> response) {
                if (response.isSuccessful()) {
                    Media media = new Media(response.body().getData().mediaId, file.getName(), mimeType.getValue());
                    Message updatedMessage = Message.withMedia(message, media);
                    callback.onSuccess(updatedMessage);
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response));
                }
            }

            @Override
            public void onFailure(Call<ParleyResponse<ParleyResponsePostMedia>> call, Throwable t) {
                t.printStackTrace();
                callback.onFailed(null, t.getMessage());
            }
        });
    }

    public void get(final Integer messageId, final RepositoryCallback<Message> callback) {
        Call<ParleyResponse<Message>> messagesCall = Connectivity.getRetrofit().create(MessageService.class).get(messageId);

        messagesCall.enqueue(new Callback<ParleyResponse<Message>>() {
            @Override
            public void onResponse(Call<ParleyResponse<Message>> call, Response<ParleyResponse<Message>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onFailed(response.code(), Connectivity.getFormattedError(response));
                }
            }

            @Override
            public void onFailure(Call<ParleyResponse<Message>> call, Throwable t) {
                t.printStackTrace();
                callback.onFailed(null, t.getMessage());
            }
        });
    }
}
