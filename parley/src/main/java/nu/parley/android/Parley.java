package nu.parley.android;

import static nu.parley.android.data.model.Message.SEND_STATUS_FAILED;
import static nu.parley.android.notification.PushNotificationHandler.EVENT_START_TYPING;
import static nu.parley.android.notification.PushNotificationHandler.EVENT_STOP_TYPING;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.datatheorem.android.trustkit.TrustKit;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.parley.android.data.messages.MessagesManager;
import nu.parley.android.data.messages.ParleyDataSource;
import nu.parley.android.data.model.Message;
import nu.parley.android.data.model.PushType;
import nu.parley.android.data.net.ParleyRepositories;
import nu.parley.android.data.net.RepositoryCallback;
import nu.parley.android.data.net.response.ParleyNotificationResponseType;
import nu.parley.android.data.net.response.ParleyResponse;
import nu.parley.android.data.repository.DeviceRepository;
import nu.parley.android.data.repository.EventRepository;
import nu.parley.android.data.repository.MessageRepository;
import nu.parley.android.data.repository.PreferenceRepository;
import nu.parley.android.notification.PushNotificationHandler;
import nu.parley.android.util.ChainListener;
import nu.parley.android.util.CompareUtil;
import nu.parley.android.util.ConnectivityMonitor;
import nu.parley.android.util.DeviceUtil;
import nu.parley.android.util.EmptyParleyCallback;
import nu.parley.android.view.ParleyView;
import nu.parley.android.view.chat.MessageViewHolderFactory;

public final class Parley {

    public enum State {
        UNCONFIGURED,
        CONFIGURING,
        CONFIGURED,
        FAILED
    }

    private static class Loader {
        private static volatile Parley INSTANCE = new Parley();
    }

    public static final String ADDITIONAL_VALUE_NAME = "name";
    public static final String ADDITIONAL_VALUE_EMAIL = "email";
    public static final String ADDITIONAL_VALUE_ADDRESS = "address";

    private State state = State.UNCONFIGURED;
    private ParleyListener listener;
    private String secret;
    private ParleyNetwork network = new ParleyNetwork();
    @Nullable
    private String userAuthorization;
    private Map<String, String> userAdditionalInformation = new HashMap<>();
    @Nullable
    private String referrer;
    @Nullable
    private String pushToken;
    private PushType pushType = PushType.FCM;
    private String uniqueDeviceIdentifier;
    private final MessagesManager messagesManager = new MessagesManager();
    private boolean retrievedFirstMessages = false;
    private boolean loadingMore = false;
    private final Set<String> uploading = new HashSet<>(); // Message UUID's
    private boolean refreshingMessages = false;

    private boolean requestNotificationPermission = true;

    private Parley() {
        // Hide default constructor
    }

    public static Parley getInstance() {
        return Loader.INSTANCE;
    }

    /**
     * Convenience for configure(context, secret, {@link EmptyParleyCallback}).
     *
     * @see #configure(Context, String, ParleyCallback)
     */
    @SuppressWarnings("unused")
    public static void configure(final Context context, final String secret) {
        configure(context, secret, new EmptyParleyCallback());
    }

    /**
     * Configure Parley Messaging.
     *
     * @param context  Context of the application.
     * @param secret   Application secret of your Parley instance.
     * @param callback {@link ParleyCallback} indicating the result of configuring.
     * @see #configure(Context, String, String, ParleyCallback)
     */
    @SuppressWarnings("WeakerAccess")
    public static void configure(final Context context, final String secret, final ParleyCallback callback) {
        getInstance().configureI(context, secret, null, callback);
    }

    /**
     * Convenience method to call configure() with an {@link EmptyParleyCallback}.
     *
     * @see #configure(Context, String, String, ParleyCallback)
     */
    @SuppressWarnings("unused")
    public static void configure(@NonNull final Context context, @NonNull final String secret, @NonNull final String uniqueDeviceIdentifier) {
        configure(context, secret, uniqueDeviceIdentifier, new EmptyParleyCallback());
    }

    /**
     * Configure Parley Messaging
     * <p>
     * The configure method allows setting a unique device identifier. If none is provided (by
     * calling the configure methods without `uniqueDeviceIdentifier`), Parley will default to
     * a random UUID that will be stored in the shared preferences. When providing a unique device
     * ID to this configure method, it is not stored by Parley and only kept for the current instance
     * of Parley. Client applications are responsible for storing it and providing Parley with the
     * same ID. This gives client applications the flexibility to change the ID if required (for
     * example when another user is logged-in to the app).
     * </p>
     *
     * <i>Note: calling `Parley.configure()` twice is unsupported, make sure to call `Parley.configure()` only once for the lifecycle of Parley.</i>
     *
     * @param context                Context of the application.
     * @param secret                 Application secret of your Parley instance.
     * @param uniqueDeviceIdentifier the device identifier to use for device registration.
     * @param callback               {@link ParleyCallback} indicating the result of configuring.
     */
    @SuppressWarnings("WeakerAccess")
    public static void configure(@NonNull final Context context, @NonNull final String secret, @NonNull final String uniqueDeviceIdentifier, @NonNull final ParleyCallback callback) {
        getInstance().configureI(context, secret, uniqueDeviceIdentifier, callback);
    }

    /**
     * Convenience for reset({@link EmptyParleyCallback}).
     *
     * @see #reset(ParleyCallback)
     */
    @SuppressWarnings("unused")
    public static void reset() {
        reset(new EmptyParleyCallback());
    }

    /**
     * Resets Parley back to its initial state (clearing the user information). Useful when logging out a user for example. Ensures that no user and chat data is left in memory.
     * <p>
     * Leaves the network, offline messaging and referrer settings as is, these can be altered via the corresponding methods.
     * </p>
     *
     * <b>Note</b>: Requires calling the `configure()` method again to use Parley.
     */
    @SuppressWarnings("unused")
    public static void reset(final ParleyCallback callback) {
        getInstance().resetI(callback);
    }


    /**
     * Resets all local user identifiers. Ensures that no user and chat data is left in memory.
     * <p>
     * Leaves the network, offline messaging and referrer settings as is, these can be altered via the corresponding methods.
     * </p>
     *
     * <b>Note</b>: Requires calling the `configure()` method again to use Parley.
     */
    @SuppressWarnings("unused")
    public static void purgeLocalMemory() {
        getInstance().purgeLocalMemoryI();
    }

    /**
     * Convenience for setUserInformation(authorization, null).
     *
     * @see #setUserInformation(String, Map)
     */
    @SuppressWarnings("unused")
    public static void setUserInformation(final String authorization) {
        setUserInformation(authorization, null);
    }

    /**
     * Convenience for setUserInformation(authorization, additionalInformation, {@link EmptyParleyCallback}).
     *
     * @see #setUserInformation(String, Map, ParleyCallback)
     */
    public static void setUserInformation(final String authorization, @Nullable final Map<String, String> additionalInformation) {
        setUserInformation(authorization, additionalInformation, new EmptyParleyCallback());
    }

    /**
     * Set user information.
     *
     * @param authorization         Authorization of the user.
     * @param additionalInformation Additional information of the user.
     * @param callback              {@link ParleyCallback} indicating the result of the update (only called when Parley is configuring/configured).
     */
    @SuppressWarnings("WeakerAccess")
    public static void setUserInformation(final String authorization, @Nullable final Map<String, String> additionalInformation, final ParleyCallback callback) {
        getInstance().setUserInformationI(authorization, additionalInformation, callback);
    }

    /**
     * Set referrer.
     *
     * @param referrer The referrer
     */
    @SuppressWarnings("unused")
    public static void setReferrer(final String referrer) {
        getInstance().setReferrerI(referrer);
    }

    /**
     * Convenience for clearUserInformation({@link EmptyParleyCallback})
     *
     * @see #clearUserInformation(ParleyCallback)
     */
    @SuppressWarnings("unused")
    public static void clearUserInformation() {
        clearUserInformation(new EmptyParleyCallback());
    }

    /**
     * Clear user information.
     *
     * @param callback {@link ParleyCallback} indicating the result of the update (only called when Parley is configuring/configured).
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void clearUserInformation(final ParleyCallback callback) {
        getInstance().clearUserInformationI(callback);
    }

    /**
     * Enables offline messaging.
     *
     * @param dataSource Implementation of {@link ParleyDataSource} that handles the operations of caching.
     */
    public static void enableOfflineMessaging(final ParleyDataSource dataSource) {
        getInstance().messagesManager.setDataSource(dataSource);
    }

    /**
     * Disable offline messaging.
     * <p>
     * <b>Note:</b> The `clear()` method will be called on the current instance to prevent unused data on the device.
     * </p>
     */
    @SuppressWarnings("unused")
    public static void disableOfflineMessaging() {
        getInstance().messagesManager.disableCaching();
    }

    /**
     * Set whether Parley should request notification permission and create the channels needed for Parley to work properly.
     * By default this is `true`, where Parley will handle the permission request and create the notification channels when needed.
     * <p>
     * <b>Note:</b> When disabling this, it's required to handle requesting the notification permission in another way, as well as creating the notification channels that are required for Parley to work properly.
     * </p>
     */
    @SuppressWarnings("unused")
    public static void setRequestNotificationPermission(Boolean enabled) {
        getInstance().requestNotificationPermission = enabled;
    }

    public static Boolean getRequestNotificationPermission() {
        return getInstance().requestNotificationPermission;
    }

    /**
     * Convenience for setPushToken(pushToken, PushType.FCM, {@link EmptyParleyCallback})
     *
     * @see #setPushToken(String, PushType)
     */
    @SuppressWarnings("unused")
    public static void setPushToken(String pushToken) {
        setPushToken(pushToken, PushType.FCM, new EmptyParleyCallback());
    }

    /**
     * Convenience for setPushToken(pushToken, {@link PushType}, {@link EmptyParleyCallback})
     *
     * @see #setPushToken(String, PushType, ParleyCallback)
     */
    @SuppressWarnings("unused")
    public static void setPushToken(String pushToken, PushType pushType) {
        setPushToken(pushToken, pushType, new EmptyParleyCallback());
    }

    /**
     * Set the users Firebase Cloud Messaging token.
     *
     * <p>
     * <b>Note:</b> Method must be called before {@link #configure(Context, String)}.
     * </p>
     *
     * @param pushToken Firebase Cloud Messaging token
     * @param pushType  Push Type
     * @param callback  {@link ParleyCallback} indicating the result of the update (only called when Parley is configuring/configured).
     */
    @SuppressWarnings("WeakerAccess")
    public static void setPushToken(@Nullable String pushToken, PushType pushType, ParleyCallback callback) {
        getInstance().setPushTokenI(pushToken, pushType, callback);
    }

    /**
     * Handle remote message.
     *
     * @param context Context for showing a notification
     * @param data    Remote message payload data
     * @param intent  Intent to launch when clicking a notification
     * @return `true` if Parley handled this payload, `false` otherwise
     */
    public static boolean handle(Context context, Map<String, String> data, Intent intent) {
        return getInstance().handleI(context, data, intent);
    }

    public String getSecret() {
        return this.secret;
    }

    public ParleyNetwork getNetwork() {
        return this.network;
    }

    /**
     * Set custom network settings.
     *
     * <p>
     * <b>Note:</b> Method must be called before {@link #configure(Context, String)}.
     * </p>
     *
     * @param network {@link ParleyNetwork} configuration.
     */
    public static void setNetwork(ParleyNetwork network) {
        getInstance().setNetworkI(network);
    }

    @Nullable
    public String getPushToken() {
        return this.pushToken;
    }

    public PushType getPushType() {
        return this.pushType;
    }

    /**
     * Send a message to Parley.
     *
     * @param message The message to sent
     */
    @SuppressWarnings("unused")
    public static void send(String message) {
        send(message, false);
    }

    /**
     * Send a message to Parley.
     *
     * @param message The message to sent
     * @param silent  Indicates if the message needs to be sent silently. The message will not be shown when silent is `true`.
     */
    public static void send(String message, boolean silent) {
        getInstance().sendMessage(message, silent);
    }

    /**
     * Handles the activity result of activities launched by Parley.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return `true` if Parley handled this request, `false` otherwise
     */
    public static boolean onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if (requestCode == ParleyView.REQUEST_SELECT_MEDIA || requestCode == ParleyView.REQUEST_TAKE_PHOTO) {
            if (getInstance().listener == null) {
                // We will handle it when the listener is attached
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getInstance().listener != null) {
                            getInstance().listener.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                }, 200);
                return true;
            }
            return getInstance().listener.onActivityResult(requestCode, resultCode, data);
        }
        // It's not a request of Parley
        return false;
    }

    /**
     * Handles the permission result of permssion requests launched by Parley
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return `true` if Parley handled this request, `false` otherwise
     */
    public static boolean onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode == ParleyView.REQUEST_PERMISSION_ACCESS_CAMERA || requestCode == ParleyView.REQUEST_PERMISSION_NOTIFICATIONS) {
            if (getInstance().listener == null) {
                // We will handle it when the listener is attached
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getInstance().listener != null) {
                            getInstance().listener.onRequestPermissionsResult(requestCode, permissions, grantResults);
                        }
                    }
                }, 200);
                return true;
            }
            return getInstance().listener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        // It's not a request of Parley
        return false;
    }

    // Logic
    public String getUniqueDeviceIdentifier() {
        return this.uniqueDeviceIdentifier;
    }

    @Nullable
    public String getUserAuthorization() {
        return this.userAuthorization;
    }

    public Map<String, String> getUserAdditionalInformation() {
        return this.userAdditionalInformation;
    }

    @Nullable
    public String getReferrer() {
        return this.referrer;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public void setListener(ParleyListener listener) {
        this.listener = listener;
        listener.onStateChanged(state);
    }

    public void clearListener() {
        this.listener = null;
    }

    // Implementation

    private void setState(State state) {
        this.state = state;

        if (listener != null) {
            listener.onStateChanged(state);
        }
    }

    public void triggerRefreshOnConnected() {
        triggerRefreshOnConnected(uploading.isEmpty());
    }

    /**
     * @param resendPendingMessages whether pending messages should be attempt to send.
     * @discussion https://github.com/parley-messaging/android-library/pull/55
     */
    public void triggerRefreshOnConnected(boolean resendPendingMessages) {
        if (this.state == State.UNCONFIGURED || this.state == State.CONFIGURING) {
            // Ignore, we cannot refresh data if we are not configured yet or if we are already configuring
            return;
        }

        if (this.state == State.FAILED || !retrievedFirstMessages) {
            // Notify that we are configuring now
            setState(State.CONFIGURING);
        }

        // Only additional messages are needed to retrieve
        this.refreshingMessages = true;
        network.repositories.getDeviceRepository().register(DeviceUtil.getDevice(), new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                new MessageRepository().findAll(new RepositoryCallback<ParleyResponse<List<Message>>>() {
                    @Override
                    public void onSuccess(ParleyResponse<List<Message>> data) {
                        refreshingMessages = false;

                        // Update paging, if needed
                        if (messagesManager.getPaging() == null) {
                            messagesManager.applyPaging(data.getPaging());
                        }

                        // Update the welcome message
                        messagesManager.applyWelcomeMessage(data.getWelcomeMessage());

                        // Detect new messages, and add them
                        boolean didAddNew = messagesManager.addOnlyNew(data.getData());

                        // Trigger update to listener
                        if (listener != null && didAddNew) {
                            listener.onReceivedLatestMessages();
                        }

                        if (resendPendingMessages) {
                            resendPendingMessages(messagesManager.getPendingMessages(true));
                        }

                        if (state != State.CONFIGURED) {
                            setState(State.CONFIGURED);
                        }
                        retrievedFirstMessages = true;
                    }

                    @Override
                    public void onFailed(Integer code, String message) {
                        refreshingMessages = false;

                        if (ParleyResponse.isOfflineErrorCode(code) && messagesManager.isCachingEnabled()) {
                            // We are fine with being offline
                            if (state != State.CONFIGURED) {
                                setState(State.CONFIGURED);
                            }
                        } else {
                            setState(State.FAILED);
                        }
                    }
                });
            }

            @Override
            public void onFailed(Integer code, String message) {
                refreshingMessages = false;

                if (ParleyResponse.isOfflineErrorCode(code) && messagesManager.isCachingEnabled()) {
                    // We are fine with being offline
                    if (state != State.CONFIGURED) {
                        setState(State.CONFIGURED);
                    }
                } else {
                    setState(State.FAILED);
                }
            }
        });
    }

    /**
     * Resends the pending messages 1 by 1, such that the order of messages remains the same.
     *
     * @param pendingMessages
     */
    private void resendPendingMessages(final List<Message> pendingMessages) {
        if (!pendingMessages.isEmpty()) {
            resendMessage(pendingMessages.get(0), new ChainListener() {
                @Override
                public void onNext() {
                    resendPendingMessages(pendingMessages.subList(1, pendingMessages.size()));
                }
            });
        }
    }

    private void configureI(Context context, String secret, @Nullable String uniqueDeviceIdentifier, final ParleyCallback callback) {
        setState(State.CONFIGURING);
        this.secret = secret;

        if (uniqueDeviceIdentifier == null) {
            this.uniqueDeviceIdentifier = PreferenceRepository.getOrGenerateDeviceId(context);
        } else {
            this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
        }
        messagesManager.clear(false);

        applySslPinning(context);

        if (ConnectivityMonitor.isNetworkOffline(context)) {
            // Direct callback
            setState(messagesManager.isCachingEnabled() ? State.CONFIGURED : State.FAILED);
        } else {
            configureI(callback);
        }
    }

    private void configureI(final ParleyCallback callback) {
        network.repositories.getDeviceRepository().register(DeviceUtil.getDevice(), new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                new MessageRepository().findAll(new RepositoryCallback<ParleyResponse<List<Message>>>() {
                    @Override
                    public void onSuccess(ParleyResponse<List<Message>> data) {
                        messagesManager.begin(data.getWelcomeMessage(), data.getStickyMessage(), data.getData(), data.getPaging());

                        setState(State.CONFIGURED);
                        retrievedFirstMessages = true;

                        callback.onSuccess();
                    }

                    @Override
                    public void onFailed(Integer code, String message) {
                        if (ParleyResponse.isOfflineErrorCode(code) && messagesManager.isCachingEnabled()) {
                            setState(State.CONFIGURED);
                            callback.onSuccess();
                        } else {
                            setState(State.FAILED);
                            callback.onFailure(code, message);
                        }
                    }
                });
            }

            @Override
            public void onFailed(Integer code, String message) {
                if (ParleyResponse.isOfflineErrorCode(code) && messagesManager.isCachingEnabled()) {
                    setState(State.CONFIGURED);
                    callback.onSuccess();
                } else {
                    setState(State.FAILED);
                    callback.onFailure(code, message);
                }
            }
        });
    }

    private void reconfigure(ParleyCallback callback) {
        retrievedFirstMessages = false;
        messagesManager.clear(true);

        setState(State.UNCONFIGURED);
        setState(State.CONFIGURING);

        configureI(callback);
    }

    private void resetI(final ParleyCallback callback) {
        this.userAuthorization = null;
        this.userAdditionalInformation = new HashMap<>();

        network.repositories.getDeviceRepository().register(DeviceUtil.getDevice(), new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                secret = null;
                callback.onSuccess();
            }

            @Override
            public void onFailed(Integer code, String message) {
                secret = null;
                callback.onFailure(code, message);
            }
        });

        retrievedFirstMessages = false;
        messagesManager.clear(true);

        setState(State.UNCONFIGURED);
    }

    private void purgeLocalMemoryI() {
        this.userAuthorization = null;
        this.userAdditionalInformation = new HashMap<>();
        this.secret = null;
        this.retrievedFirstMessages = false;
        this.messagesManager.clear(true);

        setState(State.UNCONFIGURED);
    }

    private void setNetworkI(ParleyNetwork network) {
        this.network = network;
    }

    private void setUserInformationI(String authorization, @Nullable Map<String, String> additionalInformation, final ParleyCallback callback) {
        if (additionalInformation == null) {
            additionalInformation = new HashMap<>();
        }

        this.userAuthorization = authorization;
        this.userAdditionalInformation = additionalInformation;

        if (state == State.CONFIGURED) {
            this.reconfigure(callback);
        }
    }

    private void setReferrerI(String referrer) {
        this.referrer = referrer;
    }

    private void clearUserInformationI(final ParleyCallback callback) {
        this.userAuthorization = null;
        this.userAdditionalInformation = new HashMap<>();

        if (state == State.CONFIGURED) {
            this.reconfigure(callback);
        }
    }

    private void registerDeviceIfNeeded(final ParleyCallback callback) {
        boolean shouldConfigureForState = state == State.CONFIGURING || state == State.CONFIGURED;

        if (shouldConfigureForState) {
            // We should update the device
            network.repositories.getDeviceRepository().register(DeviceUtil.getDevice(), new RepositoryCallback<Void>() {
                @Override
                public void onSuccess(Void data) {
                    callback.onSuccess();
                }

                @Override
                public void onFailed(Integer code, String message) {
                    callback.onFailure(code, message);
                }
            });
        }
    }

    // Push notifications

    private void setPushTokenI(@Nullable String pushToken, PushType pushType, final ParleyCallback callback) {
        if (CompareUtil.equals(this.pushToken, pushToken)) {
            return;
        }

        this.pushToken = pushToken;
        this.pushType = pushType;
        this.registerDeviceIfNeeded(callback);
    }

    private void applySslPinning(Context context) {
        try {
            TrustKit.initializeWithNetworkSecurityConfiguration(context, this.network.securityConfigResourceFile);
        } catch (IllegalStateException e) {
            // TrustKit was already initialized!
            e.printStackTrace();
        }
    }

    public void loadMoreMessages() {
        if (loadingMore || messagesManager.getPaging() == null) {
            // We are already loading more messages or we have no paging yet
            return;
        }
        loadingMore = true;
        new MessageRepository().getOlder(messagesManager.getPaging(), new RepositoryCallback<ParleyResponse<List<Message>>>() {
            @Override
            public void onSuccess(ParleyResponse<List<Message>> data) {
                loadingMore = false;
                messagesManager.applyPaging(data.getPaging());
                listener.onReceivedMoreMessages(data.getData());
            }

            @Override
            public void onFailed(Integer code, String message) {
                loadingMore = false;
            }
        });
    }

    // Static access configuration

    public void sendMessage(String text) {
        sendMessage(text, false);
    }

    public void sendMessage(String text, boolean silent) {
        final Message message = Message.ofTypeOwnMessage(text, silent);
        this.submitMessage(message, true);
    }

    public void resendMessage(Message message) {
        this.resendMessage(message, null);
    }

    private void resendMessage(Message message, @Nullable ChainListener chainListener) {
        this.submitMessage(message, false, false, chainListener);
    }

    public void sendMediaMessage(final File mediaFile) {
        if (refreshingMessages) {
            // Wait for it
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMediaMessage(mediaFile);
                }
            }, 100);
            return;
        }
        final Message message = Message.ofTypeOwnPendingMedia(mediaFile);
        this.submitMessage(message, true);
    }

    @SuppressWarnings("SameParameterValue")
    private void submitMessage(final Message message, final boolean isNewMessage) {
        boolean triggerSentMessage = isNewMessage && getNetwork().apiVersion.isUsingMedia() && message.getLocalUrl() == null;
        this.submitMessage(message, isNewMessage, triggerSentMessage, null);
    }

    private void submitMessage(final Message message, final boolean showNewMessage, final boolean triggerSentMessage, @Nullable final ChainListener chainListener) {
        String uuid = message.getUuid().toString();
        uploading.add(uuid);

        if (showNewMessage) {
            listener.onNewMessage(message);
            getNetwork().repositories.getEventRepository().fire(EVENT_STOP_TYPING);
        }

        String uploadMedia = message.getLocalUrl();
        if (getNetwork().apiVersion.isUsingMedia() && uploadMedia != null) {
            // Upload image first, then update the message (remove `image`, add `media`) and after that submit the actual message with the right media
            new MessageRepository().sendMedia(message, uploadMedia, new RepositoryCallback<Message>() {
                @Override
                public void onSuccess(Message updatedMessage) {
                    uploading.remove(uuid);
                    if (updatedMessage.getMedia() == null)
                        throw new AssertionError("Missing media");

                    // Media is updated
                    listener.onUpdateMessage(updatedMessage);

                    // Submit the actual message
                    submitMessage(updatedMessage, false, triggerSentMessage, chainListener);
                }

                @Override
                public void onFailed(Integer code, String errorMessage) {
                    uploading.remove(uuid);
                    if (ParleyResponse.isOfflineErrorCode(code) && messagesManager.isCachingEnabled()) {
                        // It is cached, this will be handled later
                    } else {
                        Message updatedMessage = Message.withIdAndStatus(message, message.getId(), SEND_STATUS_FAILED);
                        ParleyNotificationResponseType type = ParleyNotificationResponseType.from(errorMessage);
                        if (type != null) {
                            updatedMessage.setResponseInfoType(errorMessage);
                        }
                        listener.onUpdateMessage(updatedMessage);
                    }

                    if (showNewMessage) { // Not on `triggerSentMessage` because we wanna trigger this here when the media upload failed
                        listener.onMessageSent();
                    }

                    if (chainListener != null) {
                        chainListener.onNext();
                    }
                }
            });
        } else {
            // Just submit the message (and upload images like before V1.6)
            new MessageRepository().send(message, uploadMedia, new RepositoryCallback<Message>() {
                @Override
                public void onSuccess(final Message updatedMessage) {
                    uploading.remove(uuid);
                    listener.onUpdateMessage(updatedMessage);

                    if (triggerSentMessage) {
                        listener.onMessageSent();
                    }

                    if (chainListener != null) {
                        chainListener.onNext();
                    }
                }

                @Override
                public void onFailed(Integer code, String errorMessage) {
                    uploading.remove(uuid);
                    if (ParleyResponse.isOfflineErrorCode(code) && messagesManager.isCachingEnabled()) {
                        // It is cached, this will be handled later
                    } else {
                        Message updatedMessage = Message.withIdAndStatus(message, message.getId(), SEND_STATUS_FAILED);
                        listener.onUpdateMessage(updatedMessage);
                    }

                    if (triggerSentMessage) {
                        listener.onMessageSent();
                    }

                    if (chainListener != null) {
                        chainListener.onNext();
                    }
                }
            });
        }
    }

    private boolean handleI(Context context, Map<String, String> data, Intent intent) {
        if (!PushNotificationHandler.isParleyMessage(data)) {
            // Not a message of us, return directly
            return false;
        }

        if (listener == null) {
            if (PushNotificationHandler.isMessage(data)) {
                Message parsedMessage = PushNotificationHandler.attemptParseAsMessage(data);
                if (parsedMessage != null) {
                    if (parsedMessage.getTypeId() == MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_OWN) {
                        Log.d("Parley", "Incoming message was a Parley message, but it was a message of the user itself, ignoring it.");
                    } else if (parsedMessage.getTypeId() == MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_SYSTEM_USER) {
                        Log.d("Parley", "Incoming message was a Parley message, but it was a system message of the user, ignoring it.");
                    } else if (parsedMessage.getTypeId() == MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_SYSTEM_AGENT) {
                        Log.d("Parley", "Incoming message was a Parley message, but it was a system message of the agent, ignoring it.");
                    } else {
                        PushNotificationHandler.showNotification(context, data, intent);
                    }
                } else {
                    PushNotificationHandler.showNotification(context, data, intent);
                }
            }
        } else if (PushNotificationHandler.isMessage(data)) {
            retrieveNewMessage(data);
        } else if (PushNotificationHandler.isEvent(data)) {
            String eventType = PushNotificationHandler.getEventType(data);
            if (eventType == null) {
                Log.d("Parley", "Incoming message was a Parley event, but without event type");
            } else {
                switch (eventType) {
                    case EVENT_START_TYPING:
                        listener.onAgentStartTyping();
                        break;
                    case EVENT_STOP_TYPING:
                        listener.onAgentStopTyping();
                        break;
                    default:
                        Log.d("Parley", "Incoming message was a Parley event, but with unsupported event type: " + eventType);
                        break;
                }
            }
        } else {
            Log.d("Parley", "Incoming message was intended for Parley, but unsupported.");
        }
        return true;
    }

    private void retrieveNewMessage(Map<String, String> data) {
        Integer messageId = PushNotificationHandler.getMessageId(data);

        final Message localMessage = PushNotificationHandler.attemptParseAsMessage(data);

        if (messageId == null) {
            Log.d("Parley", "Incoming message was a Parley message, but without message id");
            if (listener != null) {
                executeNewMessageFromBackground(localMessage);
            }
            return;
        }

        if (refreshingMessages) {
            Log.d("Parley", "Incoming message was a Parley message, but we were syncing. Message will appear");
            return;
        }

        new MessageRepository().get(messageId, new RepositoryCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                if (listener != null) {
                    listener.onNewMessage(message);

                    if (message.getTypeId() != null && message.getTypeId() == MessageViewHolderFactory.MESSAGE_TYPE_MESSAGE_AGENT) {
                        listener.onAgentStopTyping();
                    }
                }
            }

            @Override
            public void onFailed(Integer code, String message) {
                if (listener != null) {
                    listener.onNewMessage(localMessage);
                }
            }
        });
    }

    private void executeNewMessageFromBackground(final Message message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                listener.onNewMessage(message);
            }
        });
    }
}
