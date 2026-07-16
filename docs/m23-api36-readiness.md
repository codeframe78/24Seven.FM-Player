# M23.3 Android 16 / API 36 readiness

Completed July 15, 2026 as the first implementation slice of the expanded M23 Play-testing readiness program.

## Outcome

- `targetSdk` is 36 while `compileSdk` remains 36 and the supported minimum remains API 26.
- The existing `enableEdgeToEdge()` and Compose `BackHandler` integration were retained; no compatibility opt-out or
  legacy Back interception was introduced.
- The API 36 target migration required no production behavior or architecture change. The native Compose/MVVM and
  service-owned Media3 boundaries remain unchanged.
- A stable `favorite_tracks_list` test tag and lazy-container scrolling replaced an invalid test assumption that every
  Favorites card would already be composed. This improves viewport-independent accessibility coverage without changing
  visible behavior.

## Android 16 behavior review

- Edge-to-edge was already enabled explicitly and the compact API 36 Player kept content clear of system bars.
- The app uses `androidx.activity.compose.BackHandler`, not `Activity.onBackPressed()` or raw Back key dispatch. A cold
  API 36 launch followed by two Back actions opened the existing `Exit 24Seven.FM Player?` dialog with both actions
  reachable.
- The manifest has no orientation, resizability, or aspect-ratio restrictions. Existing compact, medium, expanded,
  freeform, and folded-window coverage remains applicable to Android 16's adaptive-layout behavior.
- The media foreground service remains declared as `mediaPlayback`; its service/session connected tests passed on the
  API 36 runtime.

## Verification

- `:app:compileDebugKotlin` — passed.
- Focused `PlayerExperienceTest` plus `:app:compileDebugAndroidTestKotlin` — passed.
- Initial API 36 connected run — 26/27 passed and exposed one off-screen lazy-test lookup.
- Focused corrected Favorites accessibility test — passed.
- Final `:app:connectedDebugAndroidTest` on `24Seven_API_36` — 27/27 passed.
- Installed debug manifest — version code 2, minimum API 26, target API 36.
- Manual API 36 cold launch and two-Back flow — passed; exit dialog and edge-to-edge Player were visually inspected.
- `:app:testDebugUnitTest` — all 117 tests passed.
- `:app:lintDebug` — passed.
- `:app:bundleRelease` including release lint-vital checks — passed with signing inputs intentionally absent.
- Merged debug and release manifests both report target API 36.
- `git diff --check` — passed.

The unsigned release AAB produced for this migration was 17,323,660 bytes with SHA-256
`1B26162260868900B083DE5828F1431E3E203ABF74887345B0649EBEC66077CF`. It is local build evidence only and must not be
distributed. M23.1 still requires a fresh protected signed candidate from the final current HEAD.

## Remaining boundaries

- API 26 minimum-runtime, 16 KB page-size runtime, genuine tablet/unfolded foldable, physical Razr hinge, Play-delivered
  installation/update, and Play pre-launch evidence remain M23.6 work.
- No meaningful visual feature changed in M23.3, so no new repository or Discord screenshot was created.
- M23.2 UGC safety/moderation is the recommended next milestone and must begin with an authorized reporting/moderation
  destination rather than an invented private endpoint.
