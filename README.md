# 24Seven.FM Player

An unofficial, community-built native Android client for the 24seven.FM internet-radio network.

The application is written in Kotlin with Jetpack Compose and Jetpack Media3. It does not use a WebView and is not affiliated with or endorsed by 24seven.FM or its stations.

## Supported stations

- StreamingSoundtracks.com
- 1980s.FM
- Adagio.FM
- Death.FM
- Entranced.FM

## Alpha status

Milestones M1–M12 are complete and published on the development branch. M13 Independent Accounts UX is next and has not started. Alpha distribution and Play publication are intentionally deferred to M17–M18, after the remaining feature milestones.

The current Alpha provides a responsive native player, service-owned Media3 playback, five-station navigation, live metadata and artwork, queue/history, station-scoped authentication, chat, song requests, and signed-in favorite-track browsing. Request availability is conservatively revalidated against fresh station and Queue data before submission. Unit tests, lint, debug assembly, 13/13 API 35 emulator tests, and physical Razr inspection are green.

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
| M11 Adaptive Alpha UI | ✅ Complete | 2026-07-14 | [`4735f13`](https://github.com/codeframe78/24Seven.FM-Player/commit/4735f13) | Responsive branded UI, previews, accessibility, and exit flow |
| M12 Queue-aware request availability | ✅ Complete | 2026-07-14 | [`4735f13`](https://github.com/codeframe78/24Seven.FM-Player/commit/4735f13) | Conservative station-scoped availability, accessible exact labels, and fresh pre-submit checks |

### Current Milestone

| Milestone | Size | Status | Objective | Estimate | Usage | Blockers |
| --- | --- | --- | --- | --- | --- | --- |
| M13 Independent Accounts UX | L | ⏳ Preflight ready; not started | Show all five independent account states and strengthen pairwise isolation coverage | 4–8 hours | High | Live verification depends on station-specific test accounts and challenge flows |

Early Alpha distribution preparation is preserved on this branch, but M17 will not be considered active or complete until M13–M16 are finished. Google Play activation remains an external dependency for the final M17–M18 release work.

### Upcoming Milestones

| Milestone | Size | Estimate | Objective | Dependencies | Status |
| --- | --- | --- | --- | --- | --- |
| M14 Local personalization | M | 2–4 hours | Default/last station and clearly local favorites/preferences | Persistence design | ⏳ Planned |
| M15 Request history/membership | L | 4–8 hours | Station-specific history, cooldown, VIP/RIP state | Further verified account evidence | ⏳ Planned |
| M16 Secondary content access | M | 2–4 hours | Capability-aware Custom Tab routes for selected public modules | Product prioritization | ⏳ Planned |
| M17 Alpha distribution readiness | M | 1–2 focused days | Refresh and finalize privacy, signing, tester, bundle, and Play-readiness work after feature completion | M13–M16 complete and Play activation | ⏸ Deferred |
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

Alpha testers and distributors should read [the privacy notice](PRIVACY.md), [Alpha testing guide](docs/alpha-testing.md), [release notes](docs/releases/0.1.0-alpha01.md), [Play Console checklist](docs/play-console-checklist.md), and [M17 signing handoff](docs/m17-alpha-readiness.md). Development debug APKs are not intended for external distribution.

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

