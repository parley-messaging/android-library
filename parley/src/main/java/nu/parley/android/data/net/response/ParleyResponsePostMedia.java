package nu.parley.android.data.net.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public final class ParleyResponsePostMedia {

    @Nullable
    public final String mediaId;

    public ParleyResponsePostMedia(@Nullable String mediaId) {
        this.mediaId = mediaId;
    }
}
