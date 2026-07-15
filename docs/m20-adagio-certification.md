# M20 Adagio.FM certification — in progress

Public and wired-device certification work was performed July 15, 2026 on
`agent/initial-android-scaffold`. M20 is not complete until its representative authenticated gate is resolved.

## Task assessment

- Task Complexity Level: 2 — Feature Logic & API Integration
- T-shirt size: M
- Estimated active duration: 4–7 hours
- Primary confidence variable: availability of a representative Adagio.FM account and user-entered CAPTCHA

## Completed public and device evidence

| Capability | Evidence | Current result |
| --- | --- | --- |
| Primary playback | The unchanged primary relay reached the native live-playing state on the wired Android 16 Razr with media volume temporarily muted | Pass |
| Source fallback | Both unchanged station-supplied relays delivered live audio bytes; the shared ordered Media3 fallback behavior remains covered by M2 and unit evidence | Pass without a destructive station-specific failure injection |
| Classical metadata/artwork | A fresh live run supplied composer plus the complete work/ensemble title, a same-station *Spectacular Dances* cover, `LIVE`, selected Adagio styling, and AAC/128 kbps | Pass; the raw ICY value remains preserved before presentation |
| Queue/History | The exact public extended endpoint returned HTTP 200, remained below the 512k bound, contained separate Queue and Played tables, and exposed 31 table rows; native `Up next` loaded classical rows without error under the 60-second limiter | Pass |
| Public Chat | Native Adagio Chat loaded current public messages without error and showed the correct signed-out posting boundary; the shared 30-second/memory-only behavior remains intact | Pass |
| Favorites boundary | Native Favorites is reachable and shows the Adagio-qualified sign-in requirement without leaking another station's data | Pass |
| Request browsing | The native least-played suggestion returned an album and track choices while retaining the signed-out submission boundary; no request was submitted | Pass |
| Authentication challenge | Native username/password fields, same-station CAPTCHA image, alphanumeric security-code field, sign-in action, and new-code action loaded without an error | Pass for read-only challenge; authenticated gate remains |
| Capability differences | Request messages and listener activity remain explicit `Not verified`; they do not inherit SST-only capability flags | Pass |
| Secondary pages | The trusted directory exposes the seven common Adagio pages; Forums opened at `adagio.fm` in a Chrome Custom Tab and Back returned to the native app | Pass |
| Navigation/accessibility | Player, Favorites, Chat, Queue, and More remain present with station-qualified semantics and the persistent mini-player on secondary destinations | Pass |

No production Chat post, song request, form submission, account mutation, or membership action was performed.
No credentials, CAPTCHA value, session material, private response, participant content, or captured HTML was stored.

## Focused hardening

`BootstrapStationRepositoryTest` now locks the independently verified Adagio.FM capability contract:

- authentication, Chat, Favorites, Queue, History, requests, and trusted secondary content remain enabled;
- SST-only request messages and listener activity remain disabled;
- the HTTPS station origin and exact seven-page common route set remain trusted catalog entries.

The focused unit test passes.

## Validation

- `:app:compileDebugKotlin` passed.
- All 107 debug unit tests passed.
- `:app:lintDebug` passed.
- All 21 connected instrumentation tests passed on the wired Android 16 Razr (128 total tests across both suites).
- The standalone debug APK was reinstalled after the connected-test harness and launched successfully.

## Wired evidence

The physical run left playback paused and restored the original media volume. No fatal application exception was
observed. The screenshot records the live classical title and same-station artwork after playback was paused; it
contains no account data.

![Adagio.FM Player evidence on the wired Razr](screenshots/m20-adagio-certification.png)

## Remaining authenticated gate

A representative Adagio.FM account and user-entered alphanumeric CAPTCHA are still required to prove, independently
of SST and 1980s.FM:

1. native sign-in, protected process-restart restoration, expiration classification, and station-only logout;
2. the signed-in member's own Favorites discovery and memory-only loading;
3. authenticated Chat composer and one harmless post only if a new post is necessary and explicitly appropriate;
4. request eligibility/cooldown behavior and one explicit user-approved request only if prior station evidence is
   insufficient;
5. whether Adagio.FM exposes reliable membership, personal request activity, or optional request-message behavior.

Until that gate is resolved, M20 remains in progress and the three unverified capabilities stay disabled rather
than being inferred from StreamingSoundtracks.com, 1980s.FM, or similar page structure.
