# M05 native navigation validation

M05 replaces the decorative phone navigation shell with immutable destination state and working native Player, Chat, Queue, and More destinations.

## Implemented behavior

- `MainViewModel` owns the selected destination and exposes it through immutable `MainUiState`.
- Every destination receives state and emits actions upward; no screen owns playback, Media3, or network implementations.
- Player retains the verified playback and ICY now-playing experience.
- Chat and Queue render explicit station-scoped unavailable states while their capability flags remain false. No endpoint or transport was guessed.
- More shows station information and the current Chat, Queue, History, and Request capability status.
- Non-Player destinations retain a mini-player with the station, current raw ICY title when available, play/pause, and a route back to Player.
- Widths below 600 dp use bottom navigation. Widths at or above 600 dp use a navigation rail and content pane.

## Automated validation

Unit coverage verifies that destination changes flow through immutable ViewModel state without bypassing domain contracts.

Compose instrumentation coverage verifies all four destinations, capability-aware Chat and Queue content, the persistent mini-player, return to Player, and selection of the navigation-rail branch under an 800 dp constraint. Both instrumentation tests passed on the Motorola Razr 2023 running API 35.

The full debug and release unit-test suites, Android lint, debug APK assembly, and instrumentation APK assembly passed after the navigation implementation.

## Device rendering

The debug build was installed on the Motorola Razr 2023. Player and Chat were visually inspected on the 1080 × 2640 main display; all four navigation actions and the mini-player were also exercised by the Compose device test. Playback was not started for the navigation visual pass.

The API 35 emulator accepted a temporary 800 dp display override, but its headless framebuffer capture stalled. The override was reset to the original size and density. Adaptive branch selection is therefore covered deterministically by instrumentation rather than claimed as a tablet screenshot. A physical tablet or interactive tablet AVD visual pass can be added later without blocking the tested M05 layout behavior.
