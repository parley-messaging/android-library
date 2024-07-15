package nu.parley.android.data.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.model.GlideUrl;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;

import nu.parley.android.data.net.Connectivity;

public final class Media {

    @SerializedName("id")
    private String id;

    @Nullable
    @SerializedName("filename")
    private String fileName;

    @SerializedName("mimeType")
    private String mimeType;

    public Media(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        if (fileName == null) {
            String[] splits = id.split("/");
            return splits[splits.length-1];
        } else {
            return fileName;
        }
    }

    @NonNull
    public MimeType getMimeType() {
        return MimeType.from(mimeType);
    }

    /**
     * @return The id that is needed for the Media url
     */
    public String getIdForUrl() {
        ArrayList<String> splits = new ArrayList<>(Arrays.asList(id.split("/")));
        splits.remove(0); // Remove `img`
        splits.remove(0); // Remove account id
        return TextUtils.join("/", splits);
    }
    /**
     * @since clientApi v1.6.
     * @return The file url as GlideUrl. `null` if the message has no file.
     */
    public GlideUrl getUrl() {
        String mediaId = getIdForUrl();
        return Connectivity.toGlideUrlMedia(mediaId);
    }
}