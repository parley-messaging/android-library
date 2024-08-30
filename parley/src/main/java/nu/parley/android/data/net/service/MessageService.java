package nu.parley.android.data.net.service;

import java.util.List;

import nu.parley.android.data.model.Message;
import nu.parley.android.data.net.RepositoryCallback;
import nu.parley.android.data.net.response.ParleyResponsePostMedia;
import nu.parley.android.data.net.response.ParleyResponsePostMessage;
import nu.parley.android.data.net.response.ParleyResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface MessageService {

    Call<ParleyResponse<Message>> get(RepositoryCallback<ParleyResponse<Message>> callback, Integer id);

    @GET("messages")
    Call<ParleyResponse<List<Message>>> findAll();

    @GET
    Call<ParleyResponse<List<Message>>> getOlder(@Url String url);

    @POST("messages")
    Call<ParleyResponse<ParleyResponsePostMessage>> post(@Body Message chatMessage);

    /**
     * @deprecated Use {@link MessageService#postMedia(MultipartBody.Part)} from API 1.6 and onwards.
     *
     * @param filePart
     * @return response
     */
    @Multipart
    @POST("messages")
    Call<ParleyResponse<ParleyResponsePostMessage>> postImage(@Part MultipartBody.Part filePart);

    @Multipart
    @POST("media")
    Call<ParleyResponse<ParleyResponsePostMedia>> postMedia(@Part MultipartBody.Part filePart);

}
