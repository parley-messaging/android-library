package nu.parley.android.data.net.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public final class ParleyResponsePostMedia {

    @SerializedName("media")
    @Nullable
    public final String media;

    public ParleyResponsePostMedia(@Nullable String media) {
        this.media = media;
    }
}
