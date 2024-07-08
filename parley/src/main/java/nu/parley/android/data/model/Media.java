package nu.parley.android.data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;

public final class Media {

    @SerializedName("id")
    private String id;

    @SerializedName("description")
    private String description;

    @SerializedName("mimeType")
    private String mimeType;

    public Media(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

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
}