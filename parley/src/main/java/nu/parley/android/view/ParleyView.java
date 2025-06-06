package nu.parley.android.view;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import nu.parley.android.DefaultParleyDownloadCallback;
import nu.parley.android.DefaultParleyLaunchCallback;
import nu.parley.android.Parley;
import nu.parley.android.ParleyDownloadCallback;
import nu.parley.android.ParleyLaunchCallback;
import nu.parley.android.ParleyListener;
import nu.parley.android.R;
import nu.parley.android.data.messages.MessagesManager;
import nu.parley.android.data.model.Message;
import nu.parley.android.data.model.ParleyPosition;
import nu.parley.android.notification.ParleyNotificationManager;
import nu.parley.android.util.AccessibilityMonitor;
import nu.parley.android.util.AccessibilityUtil;
import nu.parley.android.util.ConnectivityMonitor;
import nu.parley.android.util.IntentUtil;
import nu.parley.android.util.ParleyPermissionUtil;
import nu.parley.android.util.StyleUtil;
import nu.parley.android.view.chat.MessageAdapter;
import nu.parley.android.view.chat.ParleyMessageListener;
import nu.parley.android.view.compose.ParleyComposeListener;
import nu.parley.android.view.compose.ParleyComposeView;
import nu.parley.android.view.compose.suggestion.SuggestionListener;
import nu.parley.android.view.compose.suggestion.SuggestionView;

public final class ParleyView extends FrameLayout implements ParleyListener, ConnectivityMonitor.Listener, AccessibilityMonitor.Listener {

    public static final int REQUEST_SELECT_MEDIA = 1661;
    public static final int REQUEST_TAKE_PHOTO = 1662;
    public static final int REQUEST_PERMISSION_ACCESS_CAMERA = 1663;
    public static final int REQUEST_PERMISSION_NOTIFICATIONS = 1664;
    public static final long TIME_TYPING_START_TRIGGER = 20 * 1000; // 20 seconds
    public static final long TIME_TYPING_STOP_TRIGGER = 15 * 1000; // 15 seconds
    // Appearance
    private ParleyPosition.Vertical notificationsPosition = ParleyPosition.Vertical.TOP;
    // Views
    private TextView statusTextView;
    private ProgressBar statusLoader;
    private LinearLayout notificationsLayout;
    private ParleyNotificationView notificationsView;
    private ParleyStickyView stickyView;
    private RecyclerView recyclerView;
    private SuggestionView suggestionView;
    private ParleyComposeView composeView;
    // View data
    private boolean isAtBottom = true;
    private Listener listener;
    private ConnectivityMonitor connectivityMonitor;

    private AccessibilityMonitor accessibilityMonitor = new AccessibilityMonitor();
    private ParleyComposeListener composeListener = new ParleyComposeListener();
    private ParleyMessageListener parleyMessageListener = new ParleyMessageListener();
    private MessageAdapter adapter = new MessageAdapter(parleyMessageListener);
    // Is typing
    private Handler isTypingAgentHandler = new Handler();
    private Runnable isTypingAgentRunnable = null;
    // Callbacks
    private ParleyLaunchCallback launchCallback;
    private ParleyDownloadCallback downloadCallback;

    public ParleyView(Context context) {
        super(context);
        init();
        applyStyle(null);
    }

    public ParleyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        applyStyle(attrs);
    }

    public ParleyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        applyStyle(attrs);
    }

    /**
     * Allows setting a {@link ParleyLaunchCallback} which allows client apps to change how Parley
     * "starts an Activity for result" and requests permissions.
     */
    public void setLaunchCallback(@NonNull ParleyLaunchCallback launchCallback) {
        this.launchCallback = launchCallback;
        parleyMessageListener.setLaunchCallback(this.launchCallback);
        composeView.setLaunchCallback(this.launchCallback);
    }

    /**
     * Allows setting a {@link ParleyDownloadCallback} which allows client apps to change how Parley
     * downloads files from the chat.
     */
    public void setDownloadCallback(@NonNull ParleyDownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
        parleyMessageListener.setDownloadCallback(this.downloadCallback);
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }


    @Deprecated(since = "3.10.0, replace with `setMediaEnabled`,", forRemoval = true)
    public void setImagesEnabled(boolean enabled) {
        setMediaEnabled(enabled);
    }

    /**
     * Sets whether the user can upload images in this chat.
     *
     * @param enabled
     */
    public void setMediaEnabled(boolean enabled) {
        composeView.setMediaEnabled(enabled);
    }

    /**
     * Sets the position where notifications are shown in the chat.
     *
     * @param position
     */
    public void setNotificationsPosition(ParleyPosition.Vertical position) {
        this.notificationsPosition = position;
        updateRecyclerViewPadding();
    }

    private MessagesManager getMessagesManager() {
        return Parley.getInstance().getMessagesManager();
    }

    private void init() {
        Log.d("ParleyView", "init()");
        inflate(getContext(), R.layout.view_parley, this);
        connectivityMonitor = new ConnectivityMonitor();

        // Views
        statusTextView = findViewById(R.id.status_text_view);
        statusLoader = findViewById(R.id.status_loader);

        notificationsLayout = findViewById(R.id.notifications_layout);
        notificationsView = findViewById(R.id.connection_notifications_view);
        stickyView = findViewById(R.id.sticky_view);
        recyclerView = findViewById(R.id.recycler_view);
        suggestionView = findViewById(R.id.suggestion_view);
        composeView = findViewById(R.id.compose_view);

        // Configure
        setLaunchCallback(new DefaultParleyLaunchCallback(getContext()));
        setDownloadCallback(new DefaultParleyDownloadCallback(getContext(), new DefaultParleyDownloadCallback.Listener() {
            @Override
            public void onFailed() {
                Snackbar.make(ParleyView.this, getContext().getString(R.string.parley_message_file_download_failed), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onComplete(Uri uri) {
                launchCallback.launchParleyActivity(IntentUtil.fromUrl(uri.toString()));
            }
        }));

        notificationsView.checkNotifications();
        recyclerView.setAdapter(adapter);
        composeView.setStartTypingTriggerInterval(TIME_TYPING_START_TRIGGER);
        composeView.setStopTypingTriggerTime(TIME_TYPING_STOP_TRIGGER);

        suggestionView.setListener(new SuggestionListener() {
            @Override
            public void onSuggestionClicked(String suggestion) {
                composeListener.onSendMessage(suggestion);
            }
        });
        composeView.setListener(composeListener);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (recyclerView.getLayoutManager() != null) {
                    boolean canScrollDown = recyclerView.canScrollVertically(1); // `canScrollVertically()` is not always correct
                    int first = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    isAtBottom = !canScrollDown || first <= 1; // When having quick replies, item 0 isn't visible (that message has no content)
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkSuggestionsTransparency();
            }
        });
    }

    private void checkSuggestionsTransparency() {
        if (AccessibilityMonitor.isTalkbackEnabled(getContext())) {
            // Always show suggestions
            suggestionView.setAlpha(1f);
        } else {
            // Fade suggestions away when scrolling away from the bottom
            final int heightSuggestionView = getSuggestionsHeight();
            int bottomOfMessages = recyclerView.computeVerticalScrollRange();
            int bottomOfShown = recyclerView.computeVerticalScrollOffset() + recyclerView.getHeight();
            float kickIn = bottomOfMessages - (heightSuggestionView + suggestionView.getPaddingBottom());
            if (isAtBottom || bottomOfShown >= bottomOfMessages) {
                // Show
                suggestionView.setAlpha(1f);
            } else if (bottomOfShown >= kickIn) {
                // Fade
                float current = bottomOfMessages - bottomOfShown;
                float alpha = current / (heightSuggestionView + suggestionView.getPaddingBottom());
                suggestionView.setAlpha(1 - alpha);
            } else {
                // Hide
                suggestionView.setAlpha(0f);
            }
        }
    }

    private void updateRecyclerViewPadding() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int paddingTop = 0;
        int paddingBottom = 0;
        switch (notificationsPosition) {
            case TOP:
                params.gravity = Gravity.TOP;
                paddingTop = getStickyHeight() + getNotificationsHeight();
                paddingBottom = getSuggestionsHeight();
                break;
            case BOTTOM:
                params.gravity = Gravity.BOTTOM;
                paddingTop = 0;
                if (getSuggestionsHeight() > 0) {
                    // Suggestions have `paddingBottom` of `stickyHeight` + `connectionHeight`  in this case
                    paddingBottom = getSuggestionsHeight();
                } else {
                    paddingBottom = getStickyHeight() + getNotificationsHeight();
                }
                break;
        }
        notificationsLayout.setLayoutParams(params);
        final int finalPaddingBottom = paddingBottom;
        final int finalPaddingTop = paddingTop;
        if (finalPaddingTop != recyclerView.getPaddingTop() || finalPaddingBottom != recyclerView.getPaddingBottom()) {
            // Only update when needed
            recyclerView.setPadding(recyclerView.getPaddingLeft(), finalPaddingTop, recyclerView.getPaddingRight(), finalPaddingBottom);
        }
    }

    private int getStickyHeight() {
        return stickyView.getVisibility() == View.VISIBLE ? stickyView.getHeight() : 0;
    }

    private int getNotificationsHeight() {
        return notificationsView.getVisibleHeight();
    }

    private int getSuggestionsHeight() {
        return suggestionView.getVisibility() == View.VISIBLE ? suggestionView.getHeight() : 0;
    }

    private void applyStyle(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(R.style.ParleyViewStyle, R.styleable.ParleyView);

            @ColorInt @Nullable Integer backgroundColor = StyleUtil.getColor(ta, R.styleable.ParleyView_parley_background_color);
            if (backgroundColor != null) {
                setBackgroundColor(backgroundColor);
            }

            @ColorInt @Nullable Integer loaderColor = StyleUtil.getColor(ta, R.styleable.ParleyView_parley_loader_tint_color);
            if (loaderColor != null) {
                StyleUtil.Helper.applyLoaderTint(statusLoader, loaderColor);
            }

            setMediaEnabled(StyleUtil.getBoolean(ta, R.styleable.ParleyView_parley_media_enabled, true));

            ParleyPosition.Vertical notificationsPosition = StyleUtil.getPositionVertical(ta, R.styleable.ParleyView_parley_notifications_position);
            setNotificationsPosition(notificationsPosition);

            ta.recycle();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        notificationsView.checkNotifications();
        if (visibility == View.VISIBLE) {
            Parley.getInstance().setListener(this);
            connectivityMonitor.register(getContext(), this);
            accessibilityMonitor.register(getContext(), this);
            if (Parley.getRequestNotificationPermission()) {
                requestPermissionsIfNeeded();
            }
        } else {
            Parley.getInstance().clearListener();
            connectivityMonitor.unregister(getContext());
            accessibilityMonitor.unregister(getContext());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // When orientation changes, visibility is not changed to invisible/gone, need to clear the listeners as well here. https://github.com/parley-messaging/android-library/issues/105
        Parley.getInstance().clearListener();
        connectivityMonitor.unregister(getContext());
        accessibilityMonitor.unregister(getContext());
        super.onDetachedFromWindow();
    }

    private void requestPermissionsIfNeeded() {
        ParleyNotificationManager.createChannels(getContext().getApplicationContext()); // Required for Android 12

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13: Request permission
            if (ParleyPermissionUtil.shouldRequestPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS)) {
                launchCallback.launchParleyPermissionRequest(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        ParleyView.REQUEST_PERMISSION_NOTIFICATIONS
                );
            }
        }
    }

    @Override
    public void onNetworkAvailable() {
        updateNetworkState(true);
    }

    @Override
    public void onNetworkUnavailable() {
        updateNetworkState(false);
    }

    private void updateNetworkState(final boolean networkAvailable) {
        post(new Runnable() {
            @Override
            public void run() {
                notificationsView.setOnline(networkAvailable);
                updateRecyclerViewPadding();

                if (!getMessagesManager().isCachingEnabled()) {
                    // Enable/disable composing accordingly
                    composeView.setEnabled(networkAvailable);
                }

                if (networkAvailable) {
                    Parley.getInstance().triggerRefreshOnConnected();
                }
            }
        });
    }

    @Override
    public void onStateChanged(Parley.State state) {
        statusTextView.setVisibility(View.GONE);
        statusLoader.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        composeView.setVisibility(View.GONE);
        switch (state) {
            case UNCONFIGURED:
                renderMessages();
                statusTextView.setText(R.string.parley_state_unconfigured);
                statusTextView.setVisibility(View.VISIBLE);
                break;
            case CONFIGURING:
                statusLoader.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
            case FAILED:
                statusTextView.setText(R.string.parley_state_failed);
                statusTextView.setVisibility(View.VISIBLE);
                break;
            case CONFIGURED:
                recyclerView.setVisibility(View.VISIBLE);
                composeView.setVisibility(View.VISIBLE);

                applyStickyMessage();
                renderMessages();

                post(new Runnable() {
                    @Override
                    public void run() {
                        updateRecyclerViewPadding();
                    }
                });
                break;
        }
    }

    private void applyStickyMessage() {
        stickyView.setMessage(getMessagesManager().getStickyMessage());
    }

    @Override
    public void onReceivedMoreMessages(List<Message> messages) {
        Log.d("ParleyView", "onReceivedMoreMessages()");

        getMessagesManager().moreLoad(messages);
        renderMessages();
    }

    @Override
    public void onNewMessage(Message message) {
        Log.d("ParleyView", "onNewMessage()");

        getMessagesManager().add(message);
        renderMessages();

        @Nullable String announcement = AccessibilityUtil.getAnnouncement(getContext(), message);
        if (announcement != null) {
            announceForAccessibility(announcement);
        }
    }

    @Override
    public void onMessageSent() {
        if (listener != null) {
            listener.onMessageSent();
        }
    }

    @Override
    public void onUpdateMessage(Message message) {
        getMessagesManager().update(message);
        renderMessages();
    }

    @Override
    public void onReceivedLatestMessages() {
        post(new Runnable() {
            @Override
            public void run() {
                applyStickyMessage();
                renderMessages();
            }
        });
    }

    @Override
    public void onAgentStartTyping() {
        getMessagesManager().addAgentTypingMessage();
        this.post(new Runnable() {
            @Override
            public void run() {
                renderMessages();
            }
        });


        if (isTypingAgentRunnable == null) {
            isTypingAgentRunnable = new Runnable() {
                @Override
                public void run() {
                    onAgentStopTyping();
                }
            };
        } else {
            isTypingAgentHandler.removeCallbacks(isTypingAgentRunnable);
        }

        // Stop after X seconds
        isTypingAgentHandler.postDelayed(isTypingAgentRunnable, TIME_TYPING_STOP_TRIGGER);

        announceForAccessibility(AccessibilityUtil.getAnnouncementAgentTyping(getContext()));
    }

    @Override
    public void onAgentStopTyping() {
        getMessagesManager().removeAgentTypingMessage();
        this.post(new Runnable() {
            @Override
            public void run() {
                renderMessages();
            }
        });

        if (isTypingAgentRunnable != null) {
            isTypingAgentHandler.removeCallbacks(isTypingAgentRunnable);
        }
    }

    private void renderMessages() {
        adapter.setMessages(getMessagesManager().getMessages(), getMessagesManager().canLoadMore());

        if (isAtBottom) {
            // Keep at bottom of the list when messages are added to the bottom
            recyclerView.scrollToPosition(0);
        }

        renderSuggestions();
    }

    private void renderSuggestions() {
        final List<String> suggestions = getMessagesManager().getAvailableQuickReplies();
        suggestionView.setSuggestions(suggestions);
        suggestionView.setVisibility(suggestions.isEmpty() ? View.GONE : View.VISIBLE);

        post(new Runnable() {
            @Override
            public void run() {
                int bottomPadding = 0;
                if (notificationsPosition == ParleyPosition.Vertical.BOTTOM) {
                    bottomPadding = getNotificationsHeight() + getStickyHeight();
                }
                suggestionView.setPadding(suggestionView.getPaddingLeft(), suggestionView.getPaddingTop(), suggestionView.getPaddingRight(), bottomPadding);

                post(new Runnable() {
                    @Override
                    public void run() {
                        updateRecyclerViewPadding();
                    }
                });
            }
        });
    }

    /**
     * Handles an activity result from the activity
     * Parley cannot use the fragment part of this
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return true if Parley handled it, false otherwise
     */
    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                composeView.submitCreatedImage();
            }
            return true;
        }

        if (requestCode == REQUEST_SELECT_MEDIA) {
            if (resultCode == RESULT_OK) {
                composeView.submitSelectedMedia(data);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int result = grantResults[i];
            if (requestCode == REQUEST_PERMISSION_ACCESS_CAMERA && permission.equals(Manifest.permission.CAMERA)) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    composeView.onCameraPermissionGranted();
                } else {
                    Snackbar.make(getRootView(), R.string.parley_error_permission_missing_camera, Snackbar.LENGTH_LONG).show();
                }
                return true;
            }
            if (requestCode == REQUEST_PERMISSION_NOTIFICATIONS && permission.equals(Manifest.permission.POST_NOTIFICATIONS)) {
                notificationsView.checkNotifications();
            }
            return true;
        }
        return false;
    }

    public interface Listener {

        void onMessageSent();
    }

    // Accessibility

    @SuppressLint("NotifyDataSetChanged")
    // TalkBack updates visuals only, which is why we need to render the chat messages again
    @Override
    public void onTalkbackChanged(boolean enabled) {
        adapter.notifyDataSetChanged();
        checkSuggestionsTransparency();
    }
}
