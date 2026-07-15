# M23 modern Android device compatibility matrix

Validated July 15, 2026 for the `0.1.0-alpha01` candidate. The product requirement is adaptive support across
current Android phones, tablets, foldables, multi-window, and freely resized windows. This is a window-size contract,
not a list of hard-coded device models.

## Supported runtime and layout contract

- Android 8.0 / API 26 is the minimum runtime; Android 15 / API 35 is the target; API 36 is the compile level.
- Compact windows below 600dp use bottom navigation. Medium and expanded windows use a navigation rail.
- The Player switches from a vertically scrollable compact layout to a two-pane artwork/details layout at 840dp.
- Selected station, destination, playback state, and the service-owned media session are not recreated or duplicated
  when the available window size changes.
- Edge-to-edge content, system bars, display cutouts, large text, portrait, landscape, and freeform resizing are handled
  from the available Compose window rather than from device-name checks.

## Runtime evidence

| Device/runtime | Effective window | Evidence | Result |
| --- | --- | --- | --- |
| Motorola Razr 2023, Android 16 / API 36 | 411×731dp compact portrait | `phone-player-live-playing.png`, `phone-queue-live.png`, `phone-stations.png` | Bottom navigation, artwork, metadata, playback, mini-player, live Queue, and horizontally scrollable station cards remain reachable. |
| API 35 Pixel 7 emulator | 411×731dp compact portrait | `phone-more.png`; connected UI suite | Compact navigation and supporting destinations render at an exact 1080×1920 Play screenshot size. |
| API 35 Pixel 7 emulator resized as a 7-inch class window | 720×1280dp portrait | `tablet-7-player2.png` | Navigation rail replaces bottom navigation; Player controls and station cards remain visible. |
| API 35 Pixel 7 emulator resized as a 10-inch class window | 960×1707dp portrait | `tablet-10-player.png` | Expanded two-pane Player and navigation rail render without stretching the compact layout. |
| API 35 Pixel 7 emulator, expanded landscape/freeform | 1707×960dp landscape | `tablet-landscape-player.png` | Two-pane Player, navigation rail, controls, and all five station cards are simultaneously reachable. |

All display overrides were temporary. The Razr returned to its physical 1080×2640 display and 420dpi density; the
emulator returned to 1080×2400 and 420dpi after capture.

## Automated coverage

- Compact width selects the bottom navigation and excludes the rail.
- Medium/tablet width selects the rail.
- A live width change from compact to medium preserves the active Queue destination.
- Short-wide layout keeps playback controls scroll-reachable.
- 1.5× font scale keeps the compact Player's primary action reachable.
- Existing previews cover compact playing, phone landscape, expanded buffering, long metadata, missing artwork,
  reconnecting/error states, light mode, and large type.
- The API 35 connected suite passes 24/24 tests, including compact/medium navigation selection, live width-change state
  preservation, short-wide scrolling, and 1.5× font-scale reachability.

## Foldable and OEM boundary

The Razr validates the app on current foldable hardware in its normal main-display state. The layout responds to the
available window and naturally leaves the center gutter between expanded Player panes, but a mechanically changed
half-open/tabletop posture was not available during this unattended pass. Hinge-posture inspection remains an Alpha
test item; it is not represented as independently certified here.

No application can guarantee behavior on every future OEM configuration. For Alpha, "all modern Android devices"
means the supported API range and adaptive window classes above are release requirements, regressions in those classes
are defects, and newly reported hinge/cutout/OEM cases are added to this matrix rather than handled with device-model
conditionals.
