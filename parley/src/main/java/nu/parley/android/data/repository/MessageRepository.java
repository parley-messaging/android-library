package nu.parley.android.data.repository;

import java.io.File;
import java.util.List;

import nu.parley.android.data.model.Message;
import nu.parley.android.data.net.Connectivity;
import nu.parley.android.data.net.RepositoryCallback;
import nu.parley.android.data.net.response.ParleyCreationData;
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

public final class MessageRepository {

    private final static String MIME_TYPE_IMAGE_FALLBACK = "image/*";

    public void findAll(final RepositoryCallback<ParleyResponse<List<Message>>> callback) {
        Call<ParleyResponse<List<Message>>> messagesCall = Connectivity.getRetrofit().create(MessageService.class).findAll();

        messagesCall.enqueue(new Callback<ParleyResponse<List<Message>>>() {
            @Override
            public void onResponse(Call<ParleyResponse<List<Message>>> call, Response<ParleyResponse<List<Message>>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailed(response.code(), response.message());
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
                    callback.onFailed(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ParleyResponse<List<Message>>> call, Throwable t) {
                t.printStackTrace();
                callback.onFailed(null, t.getMessage());
            }
        });
    }

    public void send(final Message message, final RepositoryCallback<Message> callback) {
        Call<ParleyResponse<ParleyCreationData>> messagesCall;
        if (message.getImage() == null) {
            // Text message
            messagesCall = Connectivity.getRetrofit().create(MessageService.class).post(message);
        } else {
            // Image message
            String url = message.getImage().toString();
            File file = new File(url);
            String mediaType = FileUtil.getMimeType(url);
            if (mediaType == null) {
                mediaType = MIME_TYPE_IMAGE_FALLBACK;
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse(mediaType), file);

            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
            messagesCall = Connectivity.getRetrofit().create(MessageService.class).postImage(filePart);
        }

        messagesCall.enqueue(new Callback<ParleyResponse<ParleyCreationData>>() {
            @Override
            public void onResponse(Call<ParleyResponse<ParleyCreationData>> call, Response<ParleyResponse<ParleyCreationData>> response) {
                if (response.isSuccessful()) {
                    Message updatedMessage = Message.withIdAndStatus(message, response.body().getData().getMessageId(), SEND_STATUS_SUCCESS);
                    callback.onSuccess(updatedMessage);
                } else {
                    callback.onFailed(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<ParleyResponse<ParleyCreationData>> call, Throwable t) {
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
                    callback.onFailed(response.code(), response.message());
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
