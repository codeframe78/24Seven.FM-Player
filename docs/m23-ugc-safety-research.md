# M23.2 UGC safety and moderation research

Date: July 15, 2026
Status: research checkpoint complete; implementation boundary requires owner/station confirmation

## Objective

Determine what the Play-testing candidate must do with public user-generated content before adding moderation UI or connecting another station interface. This checkpoint does not claim Google Play compliance and does not authorize publication.

## Current in-app UGC surfaces

- **Chat:** anyone using the app can read the selected station's recent public Chat rows. A member signed in to that station can submit a 255-character message.
- **Queue and History:** public requester display names and optional request messages can appear beside station-selected tracks. Signed-in members can create new request messages where the station supports them.
- **Private Messages:** M17 remains deferred and is not part of the shipping application.

The base music catalog, playback metadata, and a song request with no user-authored message are not treated as public conversation. The requester identity and optional message attached to a request are UGC and must not be mistaken for ordinary track metadata.

## Current Google Play requirements

The current primary sources are Google's [User Generated Content policy](https://support.google.com/googleplay/android-developer/answer/9876937) and [moderation requirements guidance](https://support.google.com/googleplay/android-developer/answer/12923286).

For this application, they establish the following release gates:

1. Users must accept clear Terms of Use or community rules before they can create UGC. Acceptance cannot be skipped.
2. The rules must define and prohibit objectionable content and behavior.
3. Publicly accessible UGC needs readily accessible, clearly labeled, in-app actions to report content, report users, and block users. Report and block must not be presented as one ambiguous action.
4. Reports must reach an accountable moderation process that takes appropriate, timely action. A button that only hides content locally is not a report.
5. If incidental sexual content is allowed, it must be hidden by default behind the additional safeguards described by Google, including the required filtering/opt-out interaction, age screening, and accurate content-rating answers. Selecting an 18+ Play target audience alone does not implement those in-app safeguards.

## Station-side findings

All five supported stations currently publish a same-origin `/tos.php` page effective January 1, 2026. Each says its Terms cover forums, live Chat, and request systems; prohibits spam, malicious code, and harassment; and reserves the ability to ban violating accounts or IP addresses.

All five stations also expose the already-allowlisted Contact page at `/modules.php?name=Contact_Us`. The public page says the form contacts site administration and requires sender details, a message, and a CAPTCHA. This is a plausible moderation destination, but the repository has no evidence yet that:

- station administration authorizes this form for abuse reports originating in the Android app;
- the same handling process covers all five stations;
- reports containing a Chat/request snapshot are acceptable;
- the inbox is monitored with a moderation response appropriate for Google Play; or
- a harmless end-to-end test report may be submitted.

The legacy AdvancedChatlog pages expose a complaint action for saved quote bundles. That action cannot identify the live ClearChat messages used by the app: live rows contain author, text, and timestamp labels but no stable server report identifier. It is therefore not a valid report-content transport for the current native Chat feed.

No verified station-side user-block interface or endpoint was found. A block feature must therefore be explicit device-local behavior, scoped by station and normalized display identity, that immediately hides that user's Chat content and request attribution in this app. It must not claim to ban or mute the account on the station website.

## Required product decision

There are two defensible paths for the Play candidate:

### Retain public UGC

Obtain station-owner confirmation for the report destination and moderation handling, then implement one native vertical slice containing:

- versioned, locally persisted acceptance of app Terms/community rules before Chat posts or request messages;
- separate report-content and report-user actions on Chat messages and requester attribution;
- a native report form that includes the selected station and a bounded content snapshot, handles the station CAPTCHA, and confirms server acceptance without persisting reports;
- station-scoped device-local block/unblock behavior and a blocked-users management surface;
- privacy and retention disclosures for Terms acceptance, block preferences, and report transport;
- mature-content safeguards if the station permits incidental sexual content; and
- repository, ViewModel, Compose, parser/transport, accessibility, emulator, and physical-device verification.

### Exclude public UGC from the Play candidate

Disable public Chat read/post and suppress requester names and request messages in the Play release. Keep ordinary playback, Queue track metadata, and message-free song requests. Do not advertise excluded UGC in the Play listing or reviewer instructions. Development work must not rely on an unreviewed hidden route to restore those surfaces in the Play artifact.

## Confirmation needed before implementation

The station owner or accountable administrator must answer these questions if public UGC is to remain:

1. May the Android app submit abuse reports through each station's Contact form?
2. Will those reports be monitored and actioned for all five stations, and who owns that process?
3. What report categories and bounded evidence are acceptable, and what data must not be included?
4. May the project submit a clearly labeled, harmless end-to-end moderation test?
5. Does the network intend to permit mature/incidental sexual Chat content, or should the app filter/prohibit it?

Until those answers exist, no reporting endpoint will be implemented and M23.2 remains in progress.
