# 24Seven.FM Player

An unofficial, community-built native Android client for the 24seven.FM internet-radio network.

The application is written in Kotlin with Jetpack Compose and Jetpack Media3. It does not use a WebView and is not affiliated with or endorsed by 24seven.FM or its stations.

## Supported stations

- StreamingSoundtracks.com
- 1980s.FM
- Adagio.FM
- Death.FM
- Entranced.FM

## Current progress

M1 through M11 are complete. M12 Alpha distribution readiness is prepared locally and is waiting for Play Console activation and the final upload-signing ceremony. M13 Queue-aware request availability is implemented and validated, with publication confirmation remaining. The Alpha has a responsive, dark-first Now Playing dashboard, a system-aware light theme, a persistent modern mini-player, centralized station identities, and an adaptive compact/expanded presentation without changing the service-owned Media3 playback path. Signed-in members can browse and filter their own favorite tracks on all five stations. Available tracks use green `Request Now`; recently played and currently queued tracks use red `Track Recently Played` while remaining distinct internally. Requests revalidate Queue and fresh album eligibility before the one-shot mutation. Unit tests, lint, debug assembly, 13/13 API 35 emulator tests, and physical Razr inspection are green.

| Milestone | Status | Delivered |
| --- | --- | --- |
| M1 | Complete | Buildable native Android baseline, Gradle wrapper, CI, and Windows setup validation |
| M2 | Complete | Verified playback for all five stations, atomic switching, and one-step stream fallback |
| M3 | Complete | Media3 background playback, notification/lock-screen controls, audio focus, and route-change handling |
| M4 | Complete | Live ICY titles plus station-hosted current-track artwork in Compose and MediaSession metadata |
| M5 | Complete | Native Player, Chat, Queue, and More destinations; persistent mini-player; adaptive phone/tablet navigation |
| M6 | Complete | Live Queue and History with artwork and a shared 60-second limit; up to 30 rows on extended station interfaces and 10 on Death.FM's compact feed |
| M7 | Complete | Native station-scoped authentication, alphanumeric security challenge, and Android-protected session restoration |
| M8 | Complete | Native station chat reading and protected-session posting, with memory-only history and a 30-second read limit |
| M9 | Complete | Native catalog search, album/eligibility browsing, explicit confirmation, and one-shot song requests with no automatic retries |
| M10 | Complete | Requester identity/message display, random and least-played suggestions, and the exact 80-character StreamingSoundtracks.com message form using the station-generated message-record ID; verified end to end on a VIP request |
| M11 | Complete | Adaptive Alpha interface, 24Seven.FM launcher/fallback artwork, five-station carousel and wrapped station controls, responsive full/mini players, system light/dark themes, accessible connection states, and double-Back exit confirmation |
| M12 | Waiting for Play Console | `0.1.0-alpha01` / version code 2, native/privacy documentation, Play upload-signing guardrails, tester and Console guides, unsigned release verification, debug upgrade smoke test, five-station favorite-track browsing, and 13 passing API 35 emulator tests |
| M13 | Validation complete; publishing | Queue-aware request availability, exact accessible status labels, station-scoped matching, stale-data safety, and pre-submission revalidation |

## Project Roadmap

Detailed scope, estimates, dependencies, and risks are maintained in [the implementation plan](docs/IMPLEMENTATION_PLAN.md).

### Completed Milestones

| Milestone | Status | Completed | Commit | Outcome |
| --- | --- | --- | --- | --- |
| M1 Buildable baseline | ✅ Complete | 2026-07-12 | [`9184afd`](https://github.com/codeframe78/24Seven.FM-Player/commit/9184afd) | Native Gradle/Compose baseline and Windows build path |
| M2 Five-station playback | ✅ Complete | 2026-07-13 | [`5cdade0`](https://github.com/codeframe78/24Seven.FM-Player/commit/5cdade0) | Verified streams, atomic switching, and fallback |
| M3 Background/system media | ✅ Complete | 2026-07-13 | [`4206fee`](https://github.com/codeframe78/24Seven.FM-Player/commit/4206fee) | Service-owned playback, MediaSession, focus, and route controls |
| M4 Now playing | ✅ Complete | 2026-07-13 | [`0d9e6b3`](https://github.com/codeframe78/24Seven.FM-Player/commit/0d9e6b3) | Live ICY metadata and station artwork |
| M5 Native navigation | ✅ Complete | 2026-07-13 | [`8133695`](https://github.com/codeframe78/24Seven.FM-Player/commit/8133695) | Adaptive native destinations and mini-player |
| M6 Queue and history | ✅ Complete | 2026-07-13 | [`3babdf9`](https://github.com/codeframe78/24Seven.FM-Player/commit/3babdf9) | Bounded station-scoped Queue/History polling |
| M7 Authentication | ✅ Complete | 2026-07-13 | [`a897794`](https://github.com/codeframe78/24Seven.FM-Player/commit/a897794) | Native station login and protected sessions |
| M8 Chat | ✅ Complete | 2026-07-13 | [`f99635d`](https://github.com/codeframe78/24Seven.FM-Player/commit/f99635d) | Native memory-only station chat |
| M9 Song requests | ✅ Complete | 2026-07-13 | [`e748f27`](https://github.com/codeframe78/24Seven.FM-Player/commit/e748f27) | Native search, confirmation, and one-shot submission |
| M10 Request attribution/messages | ✅ Complete | 2026-07-14 | [`73a0d89`](https://github.com/codeframe78/24Seven.FM-Player/commit/73a0d89) | Queue attribution and verified SST request messages |
| M11 Adaptive Alpha UI | ✅ Complete locally | 2026-07-14 | Unpublished protected worktree | Responsive branded UI, previews, accessibility, and exit flow |

### Current Milestone

| Milestone | Size | Status | Objective | Estimate | Usage | Blockers |
| --- | --- | --- | --- | --- | --- | --- |
| M13 Queue-aware request availability | L | 🚧 Publishing | Green `Request Now`; red `Track Recently Played` for recent or queued tracks; safe station-scoped revalidation | 5–7 hours most likely | High | Implementation/validation complete; commit and remote confirmation remain |

M12 Alpha Test Distribution Readiness is ⛔ blocked only at its external publication boundary: Google must activate the developer account before final upload signing and Play internal/closed testing. Its local implementation is protected and validated.

### Upcoming Milestones

| Milestone | Size | Estimate | Objective | Dependencies | Status |
| --- | --- | --- | --- | --- | --- |
| M14 Independent Accounts UX | L | 4–8 hours | Five separately visible account states and expanded isolation tests | Existing station-scoped sessions | ⏳ Planned |
| M15 Local personalization | M | 2–4 hours | Default/last station and clearly local favorites/preferences | Persistence design | ⏳ Planned |
| M16 Request history/membership | L | 4–8 hours | Station-specific history, cooldown, VIP/RIP state | Further verified account evidence | ⏳ Planned |
| M17 Secondary content access | M | 2–4 hours | Capability-aware Custom Tab routes for selected public modules | Product prioritization | ⏳ Planned |
| M18 Alpha publication | M | 1–3 hours after activation | Signed Play bundle and internal/closed test release | Play activation and explicit release approval | ⛔ Blocked |
| Private Messages | L provisional | 4–8 hours after server repair | Native station-isolated inbox and compose/reply | Legacy server fixes | 🧊 Deferred |

The app is fully native and uses immutable Compose UI state, repository boundaries, and station capability flags. It includes play, pause, stop, live metadata and artwork, a persistent mini-player, signed-in favorite-track browsing/filtering, capability-aware screens, native loading/error/empty states, and Android Keystore-backed account sessions. Remote data stays bounded to the documented station interfaces and their approved refresh rules.

## Screenshots

These captures are from the physical API 35 Razr using live StreamingSoundtracks.com data, so track and chat content will naturally change over time. The Player and Queue captures show the M11 Alpha shell; Chat and Requests retain their already-working native M8–M10 content presentation.

<table>
  <tr>
    <td align="center"><img src="docs/screenshots/player.png" alt="Adaptive native player with 24Seven.FM artwork, station carousel, and live controls" width="300"><br><strong>Adaptive Player and station carousel</strong></td>
    <td align="center"><img src="docs/screenshots/queue.png" alt="Native upcoming queue with artwork and the persistent M11 mini-player" width="300"><br><strong>Queue and persistent mini-player</strong></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/screenshots/chat.png" alt="Native station chat" width="300"><br><strong>Live native chat</strong></td>
    <td align="center"><img src="docs/screenshots/requests.png" alt="Native station library search" width="300"><br><strong>Song-request library search</strong></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/screenshots/favorites.png" alt="Native favorite-track list with accessible green available and red unavailable stoplights" width="300"><br><strong>Favorite tracks and request availability</strong></td>
    <td align="center"><img src="docs/screenshots/vip-request-message-success.png" alt="Verified native VIP request with requester attribution and exact message in Queue" width="300"><br><strong>Verified request attribution and message</strong></td>
  </tr>
</table>

Audio stream addresses come from station-provided playlists and remain subject to device verification. Remote interfaces are added only after source verification and permission review. See the milestone research and validation documents under [docs](docs) for authorization, protocol evidence, limits, and device results.

Planned follow-up work includes a native Private Messages experience after its authorization and behavior are settled. See [docs/future-scope.md](docs/future-scope.md).

Alpha testers and distributors should read [the privacy notice](PRIVACY.md), [Alpha testing guide](docs/alpha-testing.md), [release notes](docs/releases/0.1.0-alpha01.md), [Play Console checklist](docs/play-console-checklist.md), and [M12 signing handoff](docs/m12-alpha-readiness.md). Development debug APKs are not intended for external distribution.

## Building

Open the repository in a current Android Studio release with JDK 17. The project targets Android 15 (API 35), matching the primary Motorola Razr 2023 test device, and compiles against API 36 as required by its AndroidX dependencies. It supports Android 8.0 (API 26) and newer.

From PowerShell, validate the project with:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleRelease
```

If the repository is inside a OneDrive-synced directory and Gradle stalls on file operations, direct app build outputs to a local directory before running Gradle:

```powershell
$env:TWENTYFOURSEVEN_ANDROID_BUILD_DIR="$env:TEMP\24seven-android-build"
```

See [CONTRIBUTING.md](CONTRIBUTING.md) before contributing.

For migration to another Windows development machine, follow [docs/CODEX_HANDOFF.md](docs/CODEX_HANDOFF.md) and run `powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\validate-windows.ps1` after cloning.

