package nu.parley.android.util;

public class CompareUtil {

    public static boolean equals(Object a, Object b) {
        //noinspection EqualsReplaceableByObjectsCall // This implementation is needed for API < 19
        return (a == b) || (a != null && a.equals(b));
    }
}
