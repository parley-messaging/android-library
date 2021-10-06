# Changelog

## 3.3.0 - Upcoming

### Upgrading:

Parley now uses the latest stable API version by default, which is V1.6 since version 3.3.0. In addition to this, an `apiVersion` field has been added to `ParleyNetwork` to be able to override this with a lower version. And with this change, deprecating the methods that do not configure the API version.

- **DEPRECATION**: `ParleyNetwork(String url, String path, @XmlRes Integer securityConfigResourceFile)` is now deprecated, use `ParleyNetwork(String, String, ApiVersion, Integer)` instead.
- **DEPRECATION**: `twork(String url, String path, @XmlRes Integer securityConfigResourceFile, Map<String, String> headers)` is now deprecated, use `ParleyNetwork(String, String, ApiVersion, Integer, Map)` instead.

### Changes:

- Added `ApiVersion` parameter to ParleyNetwork to define the API version used. By default Parley uses the latest stable version.

## 3.2.2 - Released 22 Sep 2021

- Added support for buttons with types `webUrl`, `phoneNumber` and `reply`
- Quick replies now don't require a message to show anymore
- Carousel now doesn't require a message to show anymore
- Carousel now also shows the time inside the items
- Fixed an issue where quick replies could be hidden unintentionally
- Fixed an issue with visual diffing for messages with `buttons`, `carousel`, or `quickReplies`

## 3.2.1 - Released 31 Aug 2021

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