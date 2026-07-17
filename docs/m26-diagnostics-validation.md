# M26 In-App Diagnostics validation

Status: Complete.

Implementation commit: `7aedbfc` (`feat: add privacy-safe in-app diagnostics`).

## Delivered scope

- More → **In-app diagnostics** exposes a native, collapsed support card. The user must open it before the snapshot is generated for display, and the complete text is visible for review before either export action.
- **Copy** writes the reviewed text to Android's clipboard. **Share** opens Android's system chooser with a plain-text subject and body; the app never selects a recipient or sends the snapshot automatically.
- The fixed allowlist includes app version/code and build type, Android release/API, coarse manufacturer/model, selected station, bounded playback/error category, validated-network availability, broad audio-output category, and at most five recent non-sensitive playback transitions.
- Network availability and route category flow through immutable playback/UI state. Compose receives immutable diagnostics context and emits copy/share actions upward; Android package/device metadata and platform intents remain owned by the Activity.
- Recent transitions contain only playback-status, network-availability, and broad output-category enums. Sleep-timer ticks and metadata changes cannot add arbitrary or community-supplied text.

## Redaction contract

The snapshot has no input field for credentials, cookies, station sessions, CSRF values, usernames, account identities, Chat or Private Message bodies, request/report content, titles/artists, URLs, endpoints, signing data, stable device identifiers, raw exceptions, or raw logs. In particular:

- `PlaybackState.errorMessage` is reduced to **None**, **Network unavailable**, or **Playback failure**.
- The Android audio route's human-readable `displayName` is reduced to **This device**, **Bluetooth**, **Wired or USB**, or **System-managed remote output**.
- Environment and station labels have control characters removed and are bounded to 80 characters.
- Transition history is allowlisted, deduplicated, memory-only, and bounded to five entries.

No diagnostic snapshot is persisted by the app, sent to a developer backend, added to analytics, or included in background traffic.

## Privacy and Google Play result

Google Play's current Data Safety guidance says on-device access/processing is not collection when it remains on the device. It also identifies on-device transfer to another app as sharing, but provides an exception for a specific user-initiated action where sharing is reasonably expected. M26 therefore keeps the report local by default, labels the disclosure and actions clearly, and opens Android's recipient chooser only after an explicit **Share** tap. The final Console answer still must be reconciled against the active form and the complete release artifact.

Official references:

- [Google Play Data Safety definitions](https://support.google.com/googleplay/android-developer/answer/10787469)
- [Google Play User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311)

## Automated verification

| Check | Result |
| --- | --- |
| Allowlist/redaction, control-character, length, and transition-bound unit tests | Pass |
| Full `:app:testDebugUnitTest` suite | Pass, 141 tests |
| `:app:lintDebug` | Pass, 0 errors |
| Debug Kotlin and Android-test Kotlin compilation | Pass |
| Focused `RadioAppTest#diagnosticsPreviewUsesRedactedStateAndEmitsExplicitCopyAndShareActions` on Motorola Razr 2023, Android 16 | Pass, 1/1 |

The focused connected test injects a private-looking URL/token into `errorMessage` and a personal-looking Bluetooth route name into `displayName`, verifies neither reaches the preview/callback text, and exercises both explicit export actions.

## Physical Razr integration

On the physical Motorola Razr 2023 running Android 16:

1. The installed debug application exposed **In-app diagnostics** in the compact More screen without hiding Device preferences or the existing privacy surface.
2. The expanded report showed `0.1.0-alpha01-debug` / version code 2, debug build type, Android 16 / API 36, coarse Motorola model, StreamingSoundtracks.com, Idle, no error, validated network available, device output, and one allowlisted transition.
3. The report displayed no account identity, station/community content, endpoint, route display name, stable device identifier, exception, or log data.
4. **Copy** produced Android's clipboard confirmation.
5. **Share** opened the Android system chooser with the generated text; the chooser was dismissed without sending to a recipient.

## Remaining release checks

- Reconfirm the diagnostics preview and chooser in the protected signed release candidate because the displayed build type/version will differ from debug.
- Reconcile the final Data Safety and privacy-policy answers against the exact Play Console form and all active artifacts.
- A foldable-specific regression is not required for completion because the feature reuses the already validated scrollable More shell and passed on the compact physical Razr; it remains covered by the broader M23.6 device matrix before publication.
