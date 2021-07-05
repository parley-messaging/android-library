package nu.parley.android.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nu.parley.android.Parley;
import nu.parley.android.ParleyListener;
import nu.parley.android.R;
import nu.parley.android.data.messages.MessagesManager;
import nu.parley.android.data.model.Message;
import nu.parley.android.util.ConnectivityMonitor;
import nu.parley.android.util.StyleUtil;
import nu.parley.android.view.chat.MessageAdapter;
import nu.parley.android.view.chat.ParleyMessageListener;
import nu.parley.android.view.compose.ParleyComposeListener;
import nu.parley.android.view.compose.ParleyComposeView;
import nu.parley.android.view.compose.suggestion.SuggestionListener;
import nu.parley.android.view.compose.suggestion.SuggestionView;

import static android.app.Activity.RESULT_OK;

public final class ParleyView extends FrameLayout implements ParleyListener, ConnectivityMonitor.Listener {

    public static final int REQUEST_SELECT_IMAGE = 1661;
    public static final int REQUEST_TAKE_PHOTO = 1662;
    public static final long TIME_TYPING_START_TRIGGER = 20 * 1000; // 20 seconds
    public static final long TIME_TYPING_STOP_TRIGGER = 15 * 1000; // 15 seconds
    // Views
    private TextView statusTextView;
    private ProgressBar statusLoader;
    private ParleyNotificationView connectionNotificationView;
    private ParleyStickyView stickyView;
    private RecyclerView recyclerView;
    private SuggestionView suggestionView;
    private ParleyComposeView composeView;
    // View data
    private Listener listener;
    private ConnectivityMonitor connectivityMonitor;
    private ParleyComposeListener composeListener = new ParleyComposeListener();
    private MessageAdapter adapter = new MessageAdapter(new ParleyMessageListener());
    // Is typing
    private Handler isTypingAgentHandler = new Handler();
    private Runnable isTypingAgentRunnable = null;

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

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    /**
     * Sets whether the user can upload images in this chat.
     *
     * @param enabled
     */
    public void setImagesEnabled(boolean enabled) {
        composeView.setImagesEnabled(enabled);
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

        stickyView = findViewById(R.id.sticky_view);
        connectionNotificationView = findViewById(R.id.connection_notification_view);
        recyclerView = findViewById(R.id.recycler_view);
        suggestionView = findViewById(R.id.suggestion_view);
        composeView = findViewById(R.id.compose_view);

        // Configure
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
    }

    private void updateRecyclerViewTopPadding() {
        post(new Runnable() {
            @Override
            public void run() {
                final int heightStickyView = stickyView.getVisibility() == View.VISIBLE ? stickyView.getHeight() : 0;
                final int heightConnectionView = connectionNotificationView.getVisibility() == View.VISIBLE ? connectionNotificationView.getHeight() : 0;

                recyclerView.setPadding(recyclerView.getPaddingLeft(), heightStickyView + heightConnectionView, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
            }
        });
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

            setImagesEnabled(StyleUtil.getBoolean(ta, R.styleable.ParleyView_parley_images_enabled, true));

            ta.recycle();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == View.VISIBLE) {
            Parley.getInstance().setListener(this);
            connectivityMonitor.register(getContext(), this);
        } else {
            Parley.getInstance().clearListener();
            connectivityMonitor.unregister(getContext());
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
                connectionNotificationView.setVisibility(networkAvailable ? View.GONE : View.VISIBLE);
                updateRecyclerViewTopPadding();

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

                updateRecyclerViewTopPadding();
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
        boolean shouldRetainScrollState = !recyclerView.canScrollVertically(FOCUS_DOWN);
        Parcelable recyclerViewState = (shouldRetainScrollState && recyclerView.getLayoutManager() != null) ? recyclerView.getLayoutManager().onSaveInstanceState() : null;

        adapter.setMessages(getMessagesManager().getMessages(), getMessagesManager().canLoadMore());

        if (shouldRetainScrollState) {
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
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
                final int heightSuggestionView = suggestionView.getVisibility() == View.VISIBLE ? suggestionView.getHeight() : 0;
                recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), heightSuggestionView);

                // Fade suggestions away when scrolling away from the bottom
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        int bottomOfMessages = recyclerView.computeVerticalScrollRange();
                        int bottomOfShown = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
                        float kickIn = bottomOfMessages - heightSuggestionView;
                        if (bottomOfShown >= bottomOfMessages) {
                            // Show
                            suggestionView.setAlpha(1f);
                        } else if (bottomOfShown >= kickIn) {
                            // Fade
                            float current = bottomOfMessages - bottomOfShown;
                            float alpha = current / heightSuggestionView;
                            suggestionView.setAlpha(1-alpha);
                        } else {
                            // Hide
                            suggestionView.setAlpha(0f);
                        }
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

        if (requestCode == REQUEST_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                composeView.submitSelectedImage(data);
            }
            return true;
        }
        return false;
    }

    public interface Listener {

        void onMessageSent();
    }
}
