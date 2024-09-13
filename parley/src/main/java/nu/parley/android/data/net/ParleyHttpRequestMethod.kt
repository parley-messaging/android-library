package nu.parley.android.data.net

enum class ParleyHttpRequestMethod(val value: String) {
    Get("GET"),
    Head("HEAD"),
    Post("POST"),
    Put("PUT"),
    Delete("DELETE"),
}
