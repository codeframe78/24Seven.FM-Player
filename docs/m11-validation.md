# M11 Adaptive Alpha UI validation

Validated July 14, 2026 on the existing `agent/initial-android-scaffold` implementation. M11 is a presentation-layer evolution over M1–M10; no playback, stream, repository, protocol, persistence, package, module, toolchain, or dependency contract was replaced.

## Delivered interface

- The selected 24Seven.FM artwork is the launcher/round icon and the in-app fallback whenever current-track artwork is absent or fails to load.
- The Player is a dark-first atmospheric dashboard with system-selected light-mode support, a visible LIVE treatment, prominent parsed title/artist presentation, station identity, stream quality, and distinct ready/connecting/buffering/playing/paused/fallback/error copy.
- Previous and next controls wrap through the five-station catalog by dispatching the existing station-selection action. They never navigate Media3's internal primary/fallback playlist.
- Compact layouts use a horizontally scrolling, semantically selected station carousel. Artwork is capped responsively and the carousel has explicit trailing scroll clearance so the fixed navigation bar cannot cover it.
- Expanded layouts use a navigation rail and a two-pane Player arrangement instead of stretching the phone column.
- The persistent mini-player uses the same immutable playback/now-playing state and callbacks as the full Player.
- Player, Chat, Queue, and More remain the primary destinations. Authentication and complete request flows remain under More; queue/history attribution and messages remain under Queue.
- The first Back press warns the user. A second press within two seconds opens an exit dialog. Canceling preserves playback; confirming explicitly stops playback before removing the task.

## Preservation result

The complete evidence matrix and pre-edit migration plan are in `docs/m11-ui-preservation-plan.md`. All working M1–M10 surfaces remain reachable. The Media3 service, controller, repositories, session store, network security rules, PLS resources, verified stream addresses, polling limits, authentication flow, chat behavior, request behavior, and request-message workflow are unchanged.

Favorites, sleep timer, Share, Cast/audio-output selection, user settings, diagnostics, and native Private Messages were confirmed absent from the current implementation, so M11 does not display dead controls or invent backing behavior. Private Messages remain intentionally deferred because of the underlying website/server issues.

## Accessibility and adaptive behavior

- Interactive controls meet or exceed 48dp, including 56dp station navigation and a 76dp primary play/pause target.
- Playback, station navigation, artwork, app options, and mini-player controls have descriptive semantics.
- Station selection exposes a radio-button role and selected state; identity is conveyed with names and descriptions rather than color alone.
- Long metadata is bounded with multi-line ellipsis, and dedicated large-font/long-title previews cover a 1.5x font scale.
- No decorative animation or fake visualizer was added, so reduced-motion users incur no continuous motion.
- The shell reacts to available window width, system bars, cutouts, multi-window, and resizing. Compact layouts use bottom navigation; wider layouts use a rail; 840dp-and-wider Player content uses two panes.

## Previews and focused coverage

Compose previews cover compact playing, phone-landscape paused, expanded buffering, fallback reconnection with missing artwork, long metadata at large font scale, and a light-mode playback error.

Focused tests cover:

- wrapped previous/next station selection;
- play/pause action dispatch;
- the two-second double-Back gate;
- persistent mini-player behavior and legacy accessibility labels;
- compact and rail navigation;
- artwork, short-wide controls, queue/history, account, chat, and explicit request confirmation.

## Validation results

- `:app:compileDebugKotlin` — successful after the final presentation refactor.
- `:app:testDebugUnitTest` — successful, including the new station-wrap and exit-gate tests.
- `:app:lintDebug` — successful.
- `:app:assembleDebug` — successful.
- `:app:connectedDebugAndroidTest` — all 11 tests passed on the Motorola Razr 2023 running API 35.
- Physical visual inspection confirmed the compact Player, fully unobstructed station carousel, Queue, persistent mini-player, launcher icon, first-Back warning, and second-Back confirmation dialog.

The first connected run exposed two legacy mini-player semantics regressions. The implementation restored the exact established station text and artwork description, and the complete rerun passed without weakening the tests.

## Files and areas intentionally unchanged

Unchanged: Gradle and dependency definitions, Kotlin/JDK/Compose/Media3 versions, domain contracts, repository implementations, network transports/parsers, playback service/controller, session persistence, station catalog, PLS files, stream URLs, and network security configuration.

The internal content layouts for Chat, Queue, authentication, capabilities, and Requests remain intentionally familiar. They inherit the new color system and adaptive shell while retaining their proven input, polling, confirmation, and error behavior.

## Known limitations and verification boundaries

- No physical tablet or foldable posture was available. Expanded and short-wide arrangements are covered by Compose previews/tests; the Razr was physically verified in its normal compact display state. Hinge-specific split avoidance is not claimed beyond responding to the available Compose content area.
- Structured song title and artist fields are not provided by the existing now-playing contract. The presentation splits the conventional `Artist - Title` ICY form and otherwise preserves the full raw title as the song title.
- A fresh instrumentation install does not retain a user's station login; protected-session restoration itself remains covered by its existing automated and prior device validation.
- Chat/Requests were not subjected to new production posts during M11 because their protocols and behavior were intentionally unchanged.

All pre-existing tracked and untracked work was preserved. No clean, reset, restore, checkout, dependency upgrade, commit, push, or pull-request operation was performed for M11.
