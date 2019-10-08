package nu.parley.android.data.net.service;

import java.util.List;

import nu.parley.android.data.model.Message;
import nu.parley.android.data.net.response.ParleyCreationData;
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

    @GET("messages/{id}")
    Call<ParleyResponse<Message>> get(@Path("id") Integer id);

    @GET("messages")
    Call<ParleyResponse<List<Message>>> findAll();

    @GET
    Call<ParleyResponse<List<Message>>> getOlder(@Url String url);

    @POST("messages")
    Call<ParleyResponse<ParleyCreationData>> post(@Body Message chatMessage);

    @Multipart
    @POST("messages")
    Call<ParleyResponse<ParleyCreationData>> postImage(@Part MultipartBody.Part filePart);

}
