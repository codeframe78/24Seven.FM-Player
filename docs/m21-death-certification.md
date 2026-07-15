# M21 Death.FM certification — in progress

Public and wired-device certification work was performed July 15, 2026 on
`agent/initial-android-scaffold`. M21 is not complete until its representative authenticated gate is resolved.

## Task assessment

- Task Complexity Level: 2 — Feature Logic & API Integration
- T-shirt size: L
- Estimated active duration: 6–10 hours
- Rationale: Death.FM combines a compact HTML-fragment Queue contract, sparse live metadata/artwork, independently
  scoped account and request behavior, and RIP-specific membership presentation. Its previously unavailable HTTPS
  routes also required fresh protocol and trust-policy evidence before being enabled.
- Primary confidence variable: availability of a representative Death.FM account and user-entered CAPTCHA, plus the
  station-specific RIP membership/request rules exposed after sign-in

## High-level task breakdown

1. Reconfirm the unchanged primary/source relays, Media3 state, metadata, and artwork on the wired Razr.
2. Verify the compact Queue/History, public Chat, request catalog, Favorites gate, and sign-in challenge without mutation.
3. Reaudit Death.FM's recovered HTTPS pages and model the proven RIP membership difference behind station data.
4. Lock the capability and exact-route contract with focused repository tests.
5. Run unit, lint, connected-device, install, and Windows validators; preserve screenshots and update roadmap evidence.
6. Commit and publish the focused checkpoint while leaving unverified authenticated capabilities disabled.

## Completed public and device evidence

| Capability | Evidence | Current result |
| --- | --- | --- |
| Primary playback | The unchanged primary relay reached Media3 `PLAYING` on the wired Android 16 Razr with media volume temporarily muted | Pass |
| Source fallback | Both unchanged station-supplied relays delivered live audio bytes; the shared ordered Media3 fallback behavior remains covered by M2 and unit evidence | Pass without a destructive station-specific failure injection |
| Sparse metadata/artwork | A fresh live run presented the Death.FM title and artist separately, loaded same-station artwork, showed `LIVE`, and reported AAC/128 kbps | Pass; the raw ICY value remains preserved before presentation |
| Compact Queue/History | The exact public compact JSON feed returned HTTP 200 with ten upcoming and ten played rows; native `Up next` and `Recently played` loaded without inventing absent extended fields | Pass under the shared 60-second limiter |
| Public Chat | Native Death.FM Chat loaded current public messages without error and showed the correct signed-out posting boundary; shared 30-second/memory-only behavior remains intact | Pass |
| Favorites boundary | Native Favorites is reachable and shows the Death.FM-qualified sign-in requirement without leaking another station's data | Pass |
| Request browsing | The native least-played suggestion returned public catalog choices while retaining the signed-out submission boundary; no request was submitted | Pass |
| Authentication challenge | Native username/password fields, same-station CAPTCHA image, alphanumeric security-code field, sign-in action, and new-code action loaded without an error | Pass for read-only challenge; authenticated gate remains |
| Capability differences | Request messages and listener activity remain explicit `Not verified`; compact Queue rows do not invent requester/message or extended metadata fields | Pass |
| Secondary pages | HTTPS recovered after M16. The trusted directory exposes the station website, Forums, Members, Stats, Top 100, Contact, and Death-specific RIP membership route; RIP membership opened at `death.fm` in a Chrome Custom Tab | Pass under the unchanged M16 trust policy |
| Navigation/accessibility | Player, Favorites, Chat, Queue, and More remain present with station-qualified semantics and the persistent mini-player on secondary destinations | Pass |

No production Chat post, song request, form submission, account mutation, or membership action was performed.
No credentials, CAPTCHA value, session material, private response, participant content, or captured HTML was stored.

## Focused hardening

`BootstrapStationRepository` now enables Death.FM secondary content only after the configured HTTPS origin recovered
and its exact routes were reverified. The common immutable page catalog remains shared, while the station model supplies
the proven `RIP membership` title and `RIP_Subscribe` module instead of another station's VIP terminology.

`BootstrapStationRepositoryTest` locks the independently verified Death.FM capability contract:

- authentication, Chat, Favorites, compact Queue/History, requests, and trusted secondary content remain enabled;
- SST-only request messages and listener activity remain disabled;
- the HTTPS station origin and exact seven-page route set remain trusted catalog entries;
- the membership card is exactly `RIP membership` and targets `https://death.fm/modules.php?name=RIP_Subscribe`.

## Validation

- `:app:compileDebugKotlin` passed.
- All 108 debug unit tests passed.
- `:app:lintDebug` passed.
- All 21 connected instrumentation tests passed on the wired Android 16 Razr (129 total tests across both suites).
- The standalone debug APK was reinstalled after the connected-test harness and launched successfully.

## Wired evidence

The physical run left playback paused and restored the original media volume. No fatal application exception was
observed. The first screenshot records the live Death.FM title and same-station artwork after playback was paused.
The second records the exact Death.FM public directory, including RIP membership. Neither contains account data.

![Death.FM Player evidence on the wired Razr](screenshots/m21-death-certification.png)

![Death.FM trusted secondary pages and RIP membership](screenshots/m21-death-rip-pages.png)

## Remaining authenticated gate

A representative Death.FM account and user-entered alphanumeric CAPTCHA are still required to prove, independently
of the other stations:

1. native sign-in, protected process-restart restoration, expiration classification, and station-only logout;
2. the signed-in member's own Favorites discovery and memory-only loading;
3. authenticated Chat composer and one harmless post only if a new post is necessary and explicitly appropriate;
4. request eligibility/cooldown behavior and one explicit user-approved request only if prior station evidence is
   insufficient;
5. whether Death.FM exposes reliable RIP membership, personal request activity, or optional request-message behavior.

Until that gate is resolved, M21 remains in progress and the three unverified capabilities stay disabled rather
than being inferred from StreamingSoundtracks.com or similar page structure.
