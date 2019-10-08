package nu.parley.android.data.net;

public interface RepositoryCallback<T> {

    void onSuccess(T data);

    void onFailed(Integer code, String message);
}