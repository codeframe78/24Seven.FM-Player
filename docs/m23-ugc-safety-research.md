# M23.2 UGC safety and moderation research

Date: July 15, 2026
Status: authorized implementation complete; one-shot report receipt confirmation pending

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

## Station-side findings and authorization

All five supported stations currently publish a same-origin `/tos.php` page effective January 1, 2026. Each says its Terms cover forums, live Chat, and request systems; prohibits spam, malicious code, and harassment; and reserves the ability to ban violating accounts or IP addresses.

All five stations expose the already-allowlisted Contact page at `/modules.php?name=Contact_Us`. The public page says the form contacts site administration and requires sender details, a message, and a case-sensitive three-character CAPTCHA. The network administrator confirmed on July 15, 2026 that the Android app may use these forms for user/content abuse reports across all five stations, the reports reach monitored authorized administrators, the bounded report fields are acceptable, and a clearly labeled harmless test is authorized. The exact authorization statement is preserved in `terms-of-participation.md` and in the packaged in-app terms.

The legacy AdvancedChatlog pages expose a complaint action for saved quote bundles. That action cannot identify the live ClearChat messages used by the app: live rows contain author, text, and timestamp labels but no stable server report identifier. It is therefore not a valid report-content transport for the current native Chat feed.

No verified station-side user-block interface or endpoint was found. A block feature must therefore be explicit device-local behavior, scoped by station and normalized display identity, that immediately hides that user's Chat content and request attribution in this app. It must not claim to ban or mute the account on the station website.

## Implemented product decision

The authorized Play candidate retains public UGC with one native vertical slice containing:

- an adult age screen, versioned locally persisted Terms acceptance, and a separate mature-content reveal before public community content is loaded or shown;
- separate report-content and report-user actions on Chat messages and requester attribution;
- a native report form that includes the selected station and a bounded content snapshot, handles the station CAPTCHA, and does not persist report data;
- station-scoped device-local block/unblock behavior and a blocked-users management surface;
- privacy and retention disclosures for Terms acceptance, block preferences, and report transport;
- mature-content safeguards that hide community content by default and allow it to be hidden again; and
- repository, ViewModel, Compose, parser/transport, accessibility, emulator, and physical-device verification.

The date of birth itself is never persisted. Only the adult/not-adult result, accepted Terms version, content-visibility preference, and station-scoped blocked identities are stored locally. Reporter contact information, content snapshots, CAPTCHA values, and submitted reports remain transient.

## Remaining external confirmation

Exactly one administrator-authorized harmless test was submitted to StreamingSoundtracks.com on July 15, 2026. The returned HTML did not expose a recognized success marker. Because the page also contains an unrelated sidebar security-code prompt, the result cannot safely be classified as rejection and the report was not retried. The app now distinguishes a returned report form (definite rejection) from an unrecognized confirmation page (indeterminate) and suppresses retry for indeterminate delivery.

The administrator only needs to confirm one of these outcomes:

- “I received the clearly labeled M23.2 harmless test report submitted on July 15, 2026.”
- “I did not receive it.”

No additional report should be submitted until that receipt check is complete. M23.2 must not be marked complete until the result is reconciled and the final transport behavior is verified.
