# Changelog

## 3.11.4 - Unreleased

- [Accessibility] Added configurable background color to time and checkmark in messages and made corner radius configurable.
- [Styling] Added `parley_meta_background_corner_radius` to style the corner radius of the meta background.
- [Styling] Renamed `parley_message_time_background_color` to `parley_meta_background_color`. And renamed `parley_user_image_time_background` and `parley_agent_image_time_background` to `parley_user_meta_background_color` and `parley_agent_meta_background_color` accordingly.

## 3.11.3 - Released 28 Apr 2025

- [Accessibility] Added configurable background color to time in messages.
- [Accessibility] Date labels are now marked as headings for accessibility.
- [Accessibility] Add media is now marked as button for accessibility.
- [Styling] *Addition*: Added `parley_message_time_background_color` (for the styles `ParleyMessageUserStyle` and `ParleyMessageAgentStyle`). Defaults being `parley_user_image_time_background` and `parley_agent_image_time_background`, transparent.

## 3.11.2 - Released 14 Mar 2025

- [Accessibility] Fixed not being able to open images when using TalkBack.
- [Accessibility] Showing images fullscreen now show the image name. 
- [Balloon] Fixed shadows not resizing when font size changes.
- [Accessibility] Links in the chat are now always underline and bold.
- [Accessibility] Improved navigation when a chat message contains only text.

## 3.11.1 - Released 8 Oct 2024

- [Network] Fixed a typo when providing a custom network session (`onCompetion` > `onCompletion`).

## 3.11.0 - Released 18 Sep 2024

- [Chat Message] Fixed an issue causing agent images not to load since 3.10.0.
- [Network] Added support for providing a custom network session to prevent using Parley's default implementation. Check out the [Advanced - Network](https://github.com/parley-messaging/android-library?tab=readme-ov-file#network) section to use this.
- [Network] *Important* `ParleyNetwork.setInterceptor()` is removed. Use a custom network session instead, or provide the interceptor with the default `RetrofitNetworkSession` of Parley, as described in the [Advanced - Network](https://github.com/parley-messaging/android-library?tab=readme-ov-file#network) section.

## 3.10.0 - Released 30 Jul 2024

- [Source] Parley now uses Kotlin at certain parts. Make sure to configure Kotlin in your project in case it doesn't use Kotlin yet.
- [Send Media] Fixed an issue that could cause media to be send twice when using Android 14 or higher.
- [Send Media] Added support for sending PDF files when using clientApi version 1.6 or higher.
- [Chat Message] Added support for PDF documents within the chat.
- [Styling] *Addition*: Added `parley_compose_media_icon` to `ParleyComposeView`. By default this is a `+` icon to send media within the chat (camera/gallery/document).
- [Styling] *Addition*: Added `parley_compose_media_icon_tint` to `ParleyComposeView`. Since this now reflects what it is referring to.
- [Styling] *DELETION*: Removed `parley_compose_camera_tint` from `ParleyComposeView`. Use `parley_compose_media_icon_tint` instead.
- [Styling] *DELETION*: Removed `parley_compose_camera_icon` from `ParleyComposeView`. Use `parley_compose_media_icon` instead.
- [Styling] *DEPRECATION*: Replace `parley_images_enabled` with `parley_media_enabled`.
- [Styling] *REPLACED*: Replaced `parley_images_enabled` style attribute with `parley_media_enabled`.
- [Styling] *REPLACED*: Replaced `parley_ic_camera` icon from the drawables with `parley_ic_add`.
- [Styling] *REPLACED*: Replaced `parley_action_divider_margin_*` with `parley_divider_margin_*`.
- [Styling] *REPLACED*: Replaced `parley_action_divider_color` with `parley_divider_color`.
- [Styling] *REPLACED*: Replaced `parley_agent_action_divider_margin_*` with `parley_agent_divider_margin_*`.
- [Styling] *Addition*: Added `parley_user_divider_margin_*` (also as `parley_divider_margin_*` for the style `ParleyMessageUserStyle`).
- [Styling] *Addition*: Added `parley_user_divider_color` (also as `parley_divider_color` for the style `ParleyMessageUserStyle`).
- [Styling] *Addition*: Added `parley_file_name_font_family` (for the styles `ParleyMessageUserStyle` and `ParleyMessageAgentStyle`).
- [Styling] *Addition*: Added `parley_file_name_font_style` (for the styles `ParleyMessageUserStyle` and `ParleyMessageAgentStyle`).
- [Styling] *Addition*: Added `parley_file_name_text_size` (for the styles `ParleyMessageUserStyle` and `ParleyMessageAgentStyle`).
- [Styling] *Addition*: Added `parley_file_name_text_color` (for the styles `ParleyMessageUserStyle` and `ParleyMessageAgentStyle`).
- [Styling] *Addition*: Added `parley_file_action_font_family` (for the styles `ParleyMessageUserStyle` and `ParleyMessageAgentStyle`).
- [Styling] *Addition*: Added `parley_file_action_font_style` (for the styles `ParleyMessageUserStyle` and `ParleyMessageAgentStyle`).
- [Styling] *Addition*: Added `parley_file_action_text_size` (for the styles `ParleyMessageUserStyle` and `ParleyMessageAgentStyle`).
- [Styling] *Addition*: Added `parley_file_action_text_color` (for the styles `ParleyMessageUserStyle` and `ParleyMessageAgentStyle`).
- [Strings] *Addition*: Added `parley_message_file_downloading`.
- [Strings] *Addition*: Added `parley_media_select`.
- [Strings] *Addition*: Added `parley_media_camera`.
- [Strings] *Addition*: Added `parley_media_gallery`.
- [Strings] *Addition*: Added `parley_media_document`.
- [Strings] *Addition*: Added `parley_general_open`.
- [Strings] *DELETION*: Removed `parley_photo`.
- [Strings] *DELETION*: Removed `parley_select_photo`.
- [Strings] *DELETION*: Removed `parley_take_photo`.
- [Api Version] *DELETION*: Removed support for clientApi version 1.0 and 1.1.

## 3.9.7 - 16 Jul 2024

- [Send Media] Fixed an issue that could cause media to be send twice when using Android 14 or higher.
- [Dependency] Updated TrustKit to version 1.1.5.

## 3.9.6 - 14 Jun 2024

- Fixed a crash that could happen when the ParleyView is not visible/attached.

## 3.9.5 - 12 Jun 2024

- Added `Parley.purgeLocalMemory()` method to clear local memory of Parley. Requires calling `configure()` again.
- Fixed the chat scrolling to bottom automatically when no new messages where added (only happened with a custom polling implementation).

## 3.9.4 - 28 May 2024

- Fixed an issue where the agent name could have an unintended shadow applied (introduced in 3.9.0).
- Updated comments concerning public keys for SSL pinning.

## 3.9.3 - 15 May 2024

- Fixed an exception when the error response does not align with the expected response of Parley when using a proxy. Since this is unexpected behavior from the proxy, this change will be reverted in a later version.

## 3.9.2 - 14 May 2024

- Added `Parley.setRequestNotificationPermission(enabled)` to control whether Parley should request notification permissions and handle the channels. 
  - By default this is `true`, where Parley will handle the permission request and create the notification channels when needed. 
  - **NOTE**: When disabling this, it's required to handle requesting the notification permission in another way, as well as creating the notification channels that are required for Parley to work properly.

## 3.9.1 - 16 Apr 2024

- Added `setInterceptor(interceptor)` to ParleyNetwork to be able to apply a custom interceptor.

## 3.9.0 - 15 Mar 2024

- **IMPORTANT**: Parley now targets API 34 (Android 14).
- **IMPORTANT**: Parley now has a minimum SDK of API 21 (Android 5).
- Removed `WRITE_EXTERNAL_STORAGE` permission usage for API < 20.
- Removed handling for connectivity changes for API < 20.
- Parley now uses the latest stable API version by default, which is now V1.7.
- Parley now returns error messages coming from the API instead of simple error messages based on the message of a response.
- When an image inside the chat contains an error, the error message will be shown in the chat.
- When selecting or sending an image that will fail to upload, an alert will be shown with the relevant error.
- Updated versions of dependencies.
- Updated Proguard rules when using R8.
- *Addition (styling)*: Added attribute `parley_notification_show_connection` to styling `ParleyNotificationView` to configure whether to show the offline notification view.
- *Addition (styling)*: Added attribute `parley_notification_show_notifications` to styling `ParleyNotificationView` to configure whether to show the notifications disabled view.
- *DELETION (deprecation)*: Removed deprecated `ParleyNetwork(String url, String path, @XmlRes Integer securityConfigResourceFile)`. Use the constructors that specify the API version instead.
- *DELETION (deprecation)*: Removed deprecated `ParleyNetwork(String url, String path, @XmlRes Integer securityConfigResourceFile, Map<String, String> headers)`. Use the constructors that specify the API version instead.
- *DELETION (deprecation)*: Removed deprecated `Parley.setFcmToken()`. Use `Parley.setPushToken(token, type)` instead.
- *DELETION (deprecation)*: Removed deprecated style `ParleyNotificationView.parley_icon`. Use `ParleyNotificationView.parley_icon_connection` instead.

## 3.8.0 - Released 1 Nov 2023 

- Added support for the Android accessibility features.
- Added support for TalkBack.
  - Read through the chat.
  - Interact with the chat.
  - Announcing received messages.
  - Fully supporting rich message types.
- Added support for font and display scaling.
- Improved nullability around the message `typeId`.
- Fixed a layout inconsistency in compose view.
- Fixed image viewer close icon tint for lower API levels.

## 3.7.1 - Released 27 Jun 2023

- Added new SSL certificate of [parley.nu](https://parley.nu) for SSL pinning.

## 3.7.0 - Released 19 Oct 2022

- **IMPORTANT**: Parley now targets Android 13 and requests notifications permission when needed.

### Upgrading:

- *DEPRECATION (styling)*: In the style `ParleyNotificationView`, the attribute `parley_icon` has been renamed to `parley_icon_connection`.

### Changes:

- Added requesting notifications permission when the `ParleyView` is visible to the user while the app doesn't handle this permissions yet.
- Added message when the notifications permission is missing. The user will not receive chat notifications.
- Updated source to target API 33.
- Updated dependencies.
- *Addition (styling)*: Added attribute `parley_notification_icon_notifications` to styling `ParleyNotificationView` to configure the icon when user denied the notifications permission.

## 3.6.1 - Released 18 Oct 2022

- **IMPORTANT**: Parley now targets Android 12.
- Added support for targeting Android 12 and higher.
- Notification channels are now created when showing the `ParleyView`, causing Android 12 to request notifications permission if needed.
- Updated source to target API 32.

## 3.6.0 - Released 29 Jul 2022

 - Added new SSL certificate of [parley.nu](https://parley.nu) for SSL pinning.
 - When setting or clearing the user information, Parley will now reconfigure itself when needed to show the contents of the corresponding chat.

## 3.5.0 - Released 2 May 2022

### Upgrading:

Parley now uses a unique device id per app installation as default setting.

**IMPORTANT**: When using anonymous chats, the chat now always starts empty after a new app installation by default.

_What's changed?_

- In Parley 3.4.5 and lower the Android `DEVICE_ID` was used as device id. This device id does not change per app installation, causing anonymous chats to continue with their existing chat even when the user deleted and reinstalled the app. 
- In Parley 3.5.0 and higher a random UUID is used as device id. This value is stored in the shared preferences by default and is generated once per installation. Updating the app won't result in a new device id as long as the cache isn't cleared. This ensures that new anonymous chats always start empty.

**Note**: This only affects the behavior of starting anonymous chats, chats that use the user authorization won't be affected by this change.

**Note**: This is the default behavior of Parley. When passing the device id to the configure method, Parley will use that as device id instead and won't store it in the shared preferences either.

### Changes:

- Device id is now unique per installation, instead of using the Android `DEVICE_ID`. Affects how anonymous chats are handled.
- Added optional `Parley.reset(callback)` method to reset Parley back to its initial state, clearing the user and chat data that is in memory.

## 3.4.5 - Released 7 Apr 2022

- Fixed a crash that could occur when returning to the app in case the ParleyView is being closed soon after opening.

## 3.4.4 - Released 25 Mar 2022

- Added optional `ParleyView.setLaunchCallback(launchCallback)` to support handling activity results directly inside a Fragment instead of the Activity. Check out the advanced steps in the [README.md](README.md). 

## 3.4.3 - Released 23 Mar 2022

- Compose input is now aligned centred compared to the send button.
- Fixed a crash when agent is typing.

## 3.4.2 - Released 11 Mar 2022

- Added optional `uniqueDeviceIdentifier` parameter to the configure method to override the default device identifier that Parley uses.
- Updated TouchImageView from version 2.2.0 to 3.1.1

## 3.4.1 - Released 20 Jan 2022

### Upgrading

- Add the `Parley.onRequestPermissionsResult(requestCode, permissions, grantResults);` method in the *Activity* that contains the *ParleyView* for a smooth interaction when Parley needs to request permissions. Check out the updated *Step 4* in [README.md](README.md#step-4-forward-activity-results).

### Changes:

- Request camera permission if needed 

## 3.4.0 - Released 15 Nov 2021

- Added option to configure where the notifications should be shown in the chat: `top` (default) or `bottom`.
  For example: to show them on the bottom:
  
  Override the value `parley_notifications_position`:
  ```xml
  <item name="parley_notifications_position" type="integer">bottom</item>
  ```

  Or programmatically by calling:
  ```java
  parleyView.setNotificationsPosition(ParleyPosition.Vertical.BOTTOM);
  ```
- Fixed a small animation issue with quick replies.

## 3.3.0 - Released 13 Oct 2021

### Upgrading:

Parley now uses the latest stable API version by default, which is now V1.6. In addition to this, an `apiVersion` field has been added to `ParleyNetwork` to be able to override this with a lower version. Along with this change, deprecating the methods that do not configure the API version.

- **DEPRECATION**: `ParleyNetwork(String url, String path, @XmlRes Integer securityConfigResourceFile)` is now deprecated, use `ParleyNetwork(String, String, ApiVersion, Integer)` instead.
- **DEPRECATION**: `ParleyNetwork(String url, String path, @XmlRes Integer securityConfigResourceFile, Map<String, String> headers)` is now deprecated, use `ParleyNetwork(String, String, ApiVersion, Integer, Map)` instead.

### Changes:

- Added `ApiVersion` parameter to ParleyNetwork to define the API version used. By default Parley uses the latest stable version.
- Added support for the Parley API V1.6 with the new media handling. When using API V1.2, the old method is being used.
- Added support for messages that only contain buttons.

## 3.2.2 - Released 22 Sep 2021

- Added support for buttons with types `webUrl`, `phoneNumber` and `reply`
- Quick replies now don't require a message to show anymore
- Carousel now doesn't require a message to show anymore
- Carousel now also shows the time inside the items
- Fixed an issue where quick replies could be hidden unintentionally
- Fixed an issue with visual diffing for messages with `buttons`, `carousel`, or `quickReplies`

## 3.2.1 - Released 31 Aug 2021

### Upgrading:

Parley now enforces SSL pinning by default. However, when overriding the pinning with a custom `network_security_config.xml`, please check your `network_security_config.xml` whether it contains the following:

```xml
<trustkit-config enforcePinning="true"/>
```

### Changes:

- Links inside messages that were not formatted as Markdown are now clickable as well
- Enforcing SSL pinning by default (SSL pinning was working before, but it is now enforced by default as intended)

## 3.2.0 - Released 30 Jul 2021

### Upgrading:

- **DEPRECATION**: `setFcmToken()` is now deprecated, use `setPushToken()` instead.

### Changes:

- Added support for sending (silent) messages
- Added support for setting the referrer
- Added support for setting the push type
- Not showing notifications for system messages
- Updated documentation to reflect JitPack usage since version 3.1.0

## 3.1.0 - Released 5 Jul 2021

### Upgrading:

- The artifacts are now published via JitPack. To migrate, update the `build.gradle` files as shown in the latest installation steps in the [README.md](README.md).

### Changes:

- Added support for titles in a message
  - *Addition (styling)*: Attribute `parley_title_font_family` for the title in agent messages
  - *Addition (styling)*: Attribute `parley_title_font_style` for the title in agent messages
  - *Addition (styling)*: Attribute `parley_agent_title` for the color of the title in agent messages
  - *Addition (styling)*: Attribute `parley_agent_title_text_size` for the title in agent messages
- Added support for buttons in the chat
  - *Addition (styling)*: Attribute `parley_agent_action_padding` for the padding of the actions in agent messages
  - *Addition (styling)*: Attribute `parley_action_font_family` for the actions in agent messages
  - *Addition (styling)*: Attribute `parley_action_font_style` for the actions in agent messages
  - *Addition (styling)*: Attribute `parley_agent_action_divider_margin` for the margin of the dividers in the actions in agent messages
  - *Addition (styling)*: Attribute `parley_agent_action_divider` for the color of the dividers in the actions in agent messages
  - *Addition (styling)*: Attribute `parley_agent_action` for the color of the actions in agent messages
  - *Addition (styling)*: Attribute `parley_agent_action_text_size` for the actions in the agent messages
- Added support for carousels in the chat
  - The carousel can be styled the same way as agent messages. By default it has the same styles as agent messages, but they can be overridden individually
  - With a few exceptions being the `background`, `margin`, `image_content_padding` and `action_divider_margin`, which have their own default values
  - *Additions (styling)*: Properties that are available for agent messages for the items that exist in the carousel, can be overridden by using the property named with `carousel` in it
  - For example: The text color for the normal agent message is named `parley_agent_text`. To override this for only the carousel messages, the attribute `parley_agent_carousel_text` is used
- Improved the placeholder image in case the image fails to load in a message
  - It is now handled as an icon
  - *Addition (styling)*: It can now be tinted by using `parley_image_placeholder_tint`
    - _`parley_user_image_placeholder_tint` for user messages, by default it is the user text color_
    - _`parley_agent_image_placeholder_tint` for agent messages, by default it is the agent text color_
- Added support for quick replies in the chat
  - *Additions (styling)*: Introduced the `ParleySuggestionViewStyle`. It can be customised by using the known attributes for `background`, `margin`, `content_padding`, `font` and `text`.
    - Also providing a default style. These values can be overridden by using the `suggestion` key in the name.
    - For example: The `parley_background` for a suggestion is set by using `parley_suggestion_background`.

For a complete styling overview, check out [parley_configuration.xml](parley/src/main/res/values/parley_configuration.xml) for all the available options.

## 3.0.1 - Released 25 Nov 2019

- Added support for a message containing an image as well as text
- *Deprecation (styling)*: Attributes `parley_message_meta_padding` and `parley_image_meta_padding` are now unified to a single property: `parley_meta_padding`
- *Deprecation (styling)*: Attribute `parley_image_name_padding` has been renamed to `parley_name_padding`

## 3.0.0 - Released 8 Oct 2019

The first release of version 3.0 of the Parley library
