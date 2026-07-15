# M13 Queue-aware request availability validation

Validated July 14, 2026 on `agent/initial-android-scaffold`.

## Delivered behavior

- Request availability is structured domain state: Available, InCurrentQueue, RecentlyPlayed, AuthenticationRequired, UserCooldown, MembershipRequired, RequestLimitReached, StationUnavailable, RequestsUnavailable, or Unknown.
- Available library and Favorites tracks show a green stoplight, exact `Request Now` text, an enabled action, and TalkBack copy stating that the track is currently available.
- InCurrentQueue and RecentlyPlayed remain distinct internally but both show a red stoplight, exact `Track Recently Played` text, and a disabled action. Queue-specific TalkBack copy explains that the track is already in the station queue.
- Authentication, membership, cooldown, request-limit, station, and generic availability failures retain separate labels and are not falsely classified as recently played.
- Queue is observed while Queue, Favorites, or More/Requests is visible. One repository/flow still enforces the authorized 60-second minimum for scheduled and manual reads.
- A failed Queue refresh preserves cached rows for viewing, visibly marks them stale, and prevents `Request Now` until fresh data returns.

## Matching and mutation safety

- All comparisons are station-scoped. A queue from another station cannot alter the selected station's request state.
- Matching prefers a stable song ID when both sides expose one, then album ID plus title, then a normalized composite requiring title and at least artist/composer or album. Title alone never marks a track queued.
- Normalization handles case, whitespace, accents, and safe punctuation without discarding mix, movement, or version text.
- If a Queue/History row shares a title but lacks enough metadata for a safe match, the UI reports Requests Temporarily Unavailable instead of guessing queued status or exposing an unsafe green action.
- Immediately before mutation, the ViewModel refreshes Queue through the shared rate limiter. The request repository requires a ready, non-stale same-station snapshot, re-loads the album page, finds the exact song ID, re-resolves Queue/History status, and only then sends the existing one-shot request.
- Any failed refresh, missing track, changed eligibility, queued/recent match, or ambiguous match stops before mutation. No-retry behavior remains unchanged.

## Automated validation

- Focused parser/repository/resolver/ViewModel tests pass.
- Full `:app:testDebugUnitTest`, `:app:lintDebug`, and `:app:assembleDebug` pass.
- Focused Favorites Compose instrumentation passes on the API 35 emulator.
- Full `:app:connectedDebugAndroidTest` passes: 13/13 tests, zero failures, on `Codex_API_35`.

Coverage includes exact labels, green/red status roles, accessibility descriptions, queued/recent internal distinction, server restriction classification, station isolation, stable/composite matching, no title-only match, ambiguous-title fail-closed behavior, stale Queue behavior, reactive Favorites recalculation, fresh album revalidation, queued-track suppression, recently-played persistence after leaving Queue, and mutation no-retry behavior.

## Physical Razr verification

The validated debug APK was installed in place with `adb install -r` on the API 35 Motorola Razr 2023, preserving the protected SST session. Favorites loaded all 1,500 tracks. Live rows displayed red `Track Recently Played` with station countdown text and green `Request Now` for an eligible track. No confirmation dialog was opened and no request was submitted. The updated capture is `docs/screenshots/favorites.png`.

## Known limitations

- Extended Queue rows expose album identifiers but generally not song IDs. Death.FM's compact feed may have less metadata, so ambiguous matches intentionally disable requests instead of guessing.
- Live account verification of Favorites/request eligibility remains strongest on SST; the other four stations share the audited module structure and station-isolated adapter contract but still need independent account/device smoke tests.
- A request can still be rejected by station rules that change after final validation. The app does not retry or bypass the station response.
