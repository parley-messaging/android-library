package nu.parley.android.data.net.response

import com.google.gson.annotations.SerializedName
import org.commonmark.node.Code

data class ParleyHttpDataResponse(
    val statusCode: Int,
    val data: String?,
    val headers: Map<String, String>
)