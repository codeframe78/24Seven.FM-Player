# M28 UGC safety and moderation research

Date: July 18, 2026
Status: fixed-recipient email handoff implemented; physical composer/receipt check pending

## Objective

Determine what the Play-testing candidate must do with public user-generated content before adding moderation UI or connecting another station interface. This checkpoint does not claim Google Play compliance and does not authorize publication.

## Current in-app UGC surfaces

- **Chat:** anyone using the app can read the selected station's recent public Chat rows. A member signed in to that station can submit a 255-character message.
- **Queue and History:** public requester display names and optional request messages can appear beside station-selected tracks. Signed-in members can create new request messages where the station supports them.
- **Private Messages:** M47 remains deferred and is not part of the shipping application.

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

All five stations expose a public Contact page at `/modules.php?name=Contact_Us`. The network administrator confirmed on July 15, 2026 that the Android app could use those forms for bounded user/content abuse reports. A one-shot test returned an indeterminate response, and receipt could not be confirmed without risking a duplicate.

On July 18, 2026, the project owner instead designated and verified a monitored email destination for 24Seven.FM Player moderation reports, then explicitly directed the app's Contact Us route to use that address. The original confirmation and destination receipt are retained privately. The fixed address is a public product configuration value, not an administrator credential, session, endpoint, or silent-send capability.

The legacy AdvancedChatlog pages expose a complaint action for saved quote bundles. That action cannot identify the live ClearChat messages used by the app: live rows contain author, text, and timestamp labels but no stable server report identifier. It is therefore not a valid report-content transport for the current native Chat feed.

No verified station-side user-block interface or endpoint was found. A block feature must therefore be explicit device-local behavior, scoped by station and normalized display identity, that immediately hides that user's Chat content and request attribution in this app. It must not claim to ban or mute the account on the station website.

## Implemented product decision

The authorized Play candidate retains public UGC with one native vertical slice containing:

- an adult age screen, versioned locally persisted Terms acceptance, and a separate mature-content reveal before public community content is loaded or shown;
- separate report-content and report-user actions on Chat messages and requester attribution;
- a native report form that includes the selected station and a bounded content snapshot, prepares a fixed-recipient email draft, and does not persist report data;
- station-scoped device-local block/unblock behavior and a blocked-users management surface;
- privacy and retention disclosures for Terms acceptance, block preferences, and report transport;
- mature-content safeguards that hide community content by default and allow it to be hidden again; and
- repository, ViewModel, Compose, parser/transport, accessibility, emulator, and physical-device verification.

The date of birth itself is never persisted. Only the adult/not-adult result, accepted Terms version, content-visibility preference, and station-scoped blocked identities are stored locally. Reporter-entered names, content snapshots, optional details, and prepared email drafts remain transient. The Player never receives email credentials; the chosen email app supplies the sender account.

## Selected moderation-routing implementation

Exactly one administrator-authorized harmless test was submitted through the legacy StreamingSoundtracks.com Contact Us adapter on July 15, 2026. The returned HTML did not expose a recognized success marker. Because the page also contained an unrelated sidebar security-code prompt, the result could not safely be classified as rejection and the report was not retried. That legacy network adapter has now been replaced by the user-reviewed email handoff described below.

On July 18, 2026, the project owner confirmed that a dedicated mailbox is monitored for 24Seven.FM Player moderation reports and that it received a separately sent, sanitized harmless report. The original confirmation is retained privately. This establishes an accountable operator and monitored destination.

The owner then explicitly selected a Player email-client handoff. The implementation:

1. exposes Contact Us for all five station contexts as an allowlisted fixed-recipient `mailto:` action;
2. prepares bounded, station-labeled report subjects and bodies behind the existing report-content/report-user actions;
3. uses Android `ACTION_SENDTO` so only compatible email apps handle the draft;
4. requires the user to review, optionally edit, and explicitly send the message in the selected email app;
5. never accesses email credentials, silently sends mail, persists the draft, or claims that opening the composer proves sending or delivery; and
6. retains same-origin HTTPS Custom Tabs for approved non-Contact station pages.

The transport change materially updates the Terms and privacy notice, so `CURRENT_COMMUNITY_TERMS_VERSION` is now `2026-07-18` and existing adult users must accept the revised Terms before viewing or contributing community content.

M28 remains in progress until a physical-device test confirms that Contact Us and a newly authorized harmless report open an email composer with the exact recipient, station-scoped subject, and bounded body, and the owner confirms receipt after the tester explicitly sends the report once. Cancellation must produce no email, and the app must continue to describe composer opening as a handoff rather than delivery.
