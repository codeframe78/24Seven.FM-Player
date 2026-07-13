# Windows Codex handoff

## Mission

Complete **24Seven.FM Player**, an unofficial, fully native Android client for the 24seven.FM radio network. The app must use Kotlin, Jetpack Compose, MVVM, and Jetpack Media3. It must not use a WebView.

Repository: `https://github.com/codeframe78/24Seven.FM-Player`

Working branch: `agent/initial-android-scaffold`

Draft pull request: `https://github.com/codeframe78/24Seven.FM-Player/pull/1`

## Start here

```powershell
git clone https://github.com/codeframe78/24Seven.FM-Player.git
cd 24Seven.FM-Player
git switch agent/initial-android-scaffold
git status
```

Open this directory in Android Studio. Read these files before editing:

1. `AGENTS.md`
2. `README.md`
3. `CONTRIBUTING.md`
4. `docs/architecture.md`
5. `docs/endpoint-research-template.md`
6. This handoff

Inspect the existing implementation before proposing a rewrite. Preserve the station-first domain model and the separation between Compose, ViewModels, domain interfaces, data implementations, and Media3.

## Current state

The initial scaffold contains:

- Android application configuration compiling against API 36, targeting API 35, and supporting API 26+
- Kotlin and Jetpack Compose configuration
- a phone-oriented Now Playing shell with station picker and bottom navigation
- domain models for stations, capabilities, and stream variants
- a bootstrap catalog for all five stations
- an MVVM station-selection flow
- a Media3 `MediaSessionService` owning `ExoPlayer` and `MediaSession`
- station PLS files stored in `res/raw`
- a domain-restricted cleartext network policy
- initial station-catalog unit tests
- architecture and contributor documentation

The project has **not yet been compiled**. The browser-based Work environment did not have Gradle, Kotlin, an Android SDK, an emulator, or a device. Treat compilation as the first source of truth.

The repository does not yet contain a Gradle wrapper. Add one with a Gradle version compatible with the selected Android Gradle Plugin, commit the complete wrapper files, and use `gradlew.bat` for repeatable Windows and CI builds.

## Station catalog and streams

Station-provided PLS files supplied the following exact values:

| Station | Primary relay | Source fallback |
| --- | --- | --- |
| StreamingSoundtracks.com | `http://hi5.streamingsoundtracks.com/;` | `http://hi.streamingsoundtracks.com/;` |
| 1980s.FM | `http://hi5.1980s.fm/;` | `http://hi.1980s.fm/;` |
| Adagio.FM | `http://hi5.adagio.fm/;` | `http://hi.adagio.fm/;` |
| Death.FM | `http://hi5.death.fm/;` | `http://hi.death.fm/;` |
| Entranced.FM | `http://hi5.entranced.fm/;` | `http://hi.entranced.fm/;` |

The semicolon is present in the supplied playlist URLs. Do not remove it without testing both forms and documenting the result.

The playlists identify live, indefinite streams but do not identify codec or bitrate. Do not label them MP3, AAC, AAC+, or a particular bitrate until headers or successful playback verify that information.

HTTPS equivalents were not verified. Cleartext traffic is disabled globally and enabled only for the five station domains in `network_security_config.xml`. Prefer verified HTTPS endpoints if the service supports them; do not enable global cleartext access.

## Immediate objective: M1 buildable baseline

1. Confirm installed JDK and Android SDK versions.
2. Review dependency versions for compatibility rather than blindly upgrading everything.
3. Add the Gradle wrapper.
4. Sync in Android Studio.
5. Run unit tests, lint, and `assembleDebug`.
6. Fix every compilation, resource, manifest, and dependency error.
7. Launch the app on an emulator or physical device.
8. Record the exact validation commands and results in PR #1.
9. Add a GitHub Actions workflow using JDK 17 and the Gradle wrapper.

Suggested validation after the wrapper exists:

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

Do not mark PR #1 ready or merge it merely because Gradle sync succeeds. The debug app must launch and the tests and lint results must be understood.

## M2: working multi-station playback

After M1 is clean:

1. Define a domain-facing `PlaybackRepository` or `PlaybackController` interface.
2. Connect the Activity/ViewModels to `RadioPlaybackService` using Media3 `MediaController`.
3. Keep the `ExoPlayer` instance exclusively inside the service.
4. Load the selected station's primary relay as a `MediaItem`.
5. Implement Play, Pause, and Stop.
6. Switch stations atomically so two streams never play simultaneously.
7. Attempt the source stream after bounded failure of the primary relay.
8. Expose immutable playback state: idle, connecting, buffering, playing, paused, retrying, and error.
9. Update notification metadata when the station changes.
10. Verify all five stations on a real device when possible.

For a live radio stream, do not show a seekable progress bar. Show `LIVE`, connection state, and elapsed track time only if trustworthy track timing becomes available.

## Later milestones

Proceed in this order unless evidence requires a dependency change:

1. **M3 — Background playback:** notification, lock screen, headset/Bluetooth controls, audio focus, noisy-output handling, service lifecycle.
2. **M4 — Now Playing:** ICY or supported metadata, track/album/composer fields, artwork strategy, stream quality based on evidence.
3. **M5 — Native navigation:** working Player, Chat, Queue, and More destinations; persistent mini-player; adaptive tablet layout.
4. **M6 — Queue/history:** document and implement supported station-scoped metadata endpoints.
5. **M7 — Authentication:** native login, CSRF/cookie behavior, secure session storage, logout and expiry recovery.
6. **M8 — Chat:** determine whether the actual transport is polling, long polling, SSE, or WebSocket before implementing it.
7. **M9 — Requests:** search, eligibility/cooldowns, submission, confirmation, and error handling.
8. **M10–M12 — Polish, security, testing, and automation.**
9. **M13 — Beta release.**
10. **M14 — Stable 1.0 release.**

Use `docs/endpoint-research-template.md` for protocol research. Never commit credentials, cookies, CSRF values, authorization headers, private messages, or unsanitized HAR captures. Verify whether a supported developer API exists before depending on scraped HTML or undocumented private endpoints.

## Architectural invariants

- No WebView.
- No direct ExoPlayer calls from composables or screen ViewModels.
- No Retrofit/OkHttp/WebSocket/cookie objects in composables.
- State flows downward; user actions flow upward.
- Every station-specific operation accepts or derives a `StationId`.
- Unsupported station features are represented by capability flags, not crashes or hard-coded UI assumptions.
- Only one station may play at a time.
- Public listening should not be unnecessarily blocked behind login.
- Authentication and chat failures must not stop public audio playback.
- Do not claim the project is official or endorsed by 24seven.FM.

## Git workflow

Continue on `agent/initial-android-scaffold` until PR #1 reaches a genuinely buildable baseline. Keep commits focused and descriptive. Push changes to the existing branch so PR #1 updates.

Before each commit:

```powershell
git status --short
git diff
```

Do not commit Android SDK paths, `.idea` state, secrets, build outputs, APKs, or captured account data. Do not force-push shared work without confirming that nobody else has added commits.

When M1 is verified, update PR #1 with:

- Android Studio version
- JDK version
- compile/target SDK
- emulator or device used
- test, lint, and assemble commands
- results and remaining warnings

## Definition of completion

The project is complete when:

- all five stations play reliably with primary/fallback behavior;
- background playback and system media controls work correctly;
- station switching is clean and race-free;
- now-playing data is useful and evidence-based;
- supported chat, login, queue/history, and requests work natively;
- the UI is usable on phones and adapts appropriately to larger screens;
- secrets and sessions are handled safely;
- automated tests and CI protect critical behavior;
- a signed beta has been tested before the stable 1.0 release;
- documentation enables outside contributors to build and extend the project.

Begin by reporting the current branch, repository status, Android Studio/JDK/SDK environment, and the first Gradle sync/build result. Then fix M1 autonomously, stopping only for a decision that materially changes product scope or requires credentials/authorization.
