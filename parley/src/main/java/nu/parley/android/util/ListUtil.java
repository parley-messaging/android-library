package nu.parley.android.util;

import java.util.List;

public final class ListUtil {

    public static <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);
    }
}
