package nu.parley.android.util;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;

import androidx.core.view.accessibility.AccessibilityManagerCompat;

public final class AccessibilityMonitor {

    private boolean talkback = false;
    private Listener listener;
    private AccessibilityManagerCompat.TouchExplorationStateChangeListener callback;

    public void register(Context context, Listener listener) {
        this.listener = listener;
        this.callback = createCallbackListener(context);
        checkChanged(context);
        AccessibilityManagerCompat.addTouchExplorationStateChangeListener(getAccessibilityManager(context), callback);
    }

    public void unregister(Context context) {
        if (callback != null) {
            AccessibilityManagerCompat.removeTouchExplorationStateChangeListener(getAccessibilityManager(context), callback);
        }
        this.listener = null;
        this.callback = null;
    }

    static private AccessibilityManager getAccessibilityManager(Context context) {
        return (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public static boolean isTalkbackEnabled(Context context) {
        return getAccessibilityManager(context).isTouchExplorationEnabled();
    }

    private AccessibilityManagerCompat.TouchExplorationStateChangeListener createCallbackListener(final Context context) {
        return new AccessibilityManagerCompat.TouchExplorationStateChangeListener() {
            @Override
            public void onTouchExplorationStateChanged(boolean enabled) {
                checkChanged(context);
            }
        };
    }

    private void checkChanged(Context context) {
        boolean talkbackEnabled = isTalkbackEnabled(context);
        if (listener != null && talkback != talkbackEnabled) {
            talkback = talkbackEnabled;
            listener.onTalkbackChanged(talkback);
        }
    }

    public interface Listener {

        void onTalkbackChanged(boolean enabled);
    }
}
