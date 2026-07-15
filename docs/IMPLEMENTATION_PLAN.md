# Implementation plan

Updated July 15, 2026 after completing the Death.FM authenticated certification gate. Estimates are active Codex elapsed time in this environment, including inspection, implementation, Gradle validation, documentation, Git, and remote confirmation—not traditional human developer time.

## Planning model

- M1–M16 and M18 are complete and retain their existing evidence and published checkpoints.
- M17 is the remaining shared capability, using immutable Compose state, repository contracts, capability flags, and station-isolated protected sessions once its external blocker clears.
- M18–M22 certify the shared implementation against each station. They are hardening and evidence gates, not station-specific application forks.
- Each station's representative native login, protected restoration, isolation, authenticated surface, and logout flow is now a hard completion gate. Public/device evidence may be gathered early but does not advance the active station order.
- M23–M24 are the final distribution and publication gates.
- Private Messages remains numbered as M17 but deferred pending legacy server repair and verified production limits.
- Early M23 readiness artifacts are preserved, but they must be refreshed after M15–M22. The Play developer account is approved; Play App Signing/app setup, final upload signing, and explicit publication authorization remain dependencies.

## Task complexity protocol

Before code changes for every roadmap task, state its Task Complexity Level and a brief milestone-specific approach:

- Level 1 — Scaffold & Boilerplate: low reasoning; prioritize speed, Android best practices, clean architecture, concise production-ready code, and minimal explanation.
- Level 2 — Feature Logic & API Integration: medium reasoning; emphasize robust errors, immutable ViewModel state, coroutine/thread safety, implementation detail, test recommendations, and clear state-flow explanation.
- Level 3 — Architectural Refactoring & System Design: high reasoning; compare alternatives, justify the chosen scalable pattern, and assess performance, dependency-injection, maintenance, and side effects.

This classification supplements the required XS–XXL T-shirt size, rationale, estimated duration, confidence check, and high-level task breakdown. Every roadmap completion update must also include repository and Discord screenshots for significant visual changes. A backend-only milestone should explicitly state that it introduced no meaningful visual surface instead of reusing an unrelated image.

## Current milestone

### M22 — Entranced.FM authenticated completion and final station gate

- Task Complexity Level: 2 — Feature Logic & API Integration
- Size: M overall; approximately 1–3 active hours remain after the account/CAPTCHA is available
- Estimated elapsed time: 4–7 hours overall
- Usage intensity: High
- Confidence: Medium-Low
- Outcome: close the already-passing Entranced.FM public/device milestone with independent native sign-in, protected restoration, station isolation, authenticated Favorites/Chat/request behavior, corrected legacy ICY punctuation, and station-only logout without inheriting other stations' rules.
- Expected layers: user-entered sign-in/CAPTCHA, protected process-restart restoration, independent station selection, signed-in surface checks without unnecessary mutations, explicit logout, evidence reconciliation, and publication.
- Dependencies: the existing verified Entranced.FM playback/metadata/extended-Queue/chat interfaces plus a representative Entranced.FM account and user-entered CAPTCHA.
- Principal risk: account availability, undocumented Entranced membership/request behavior, and live metadata variability needed to reconfirm the punctuation boundary on-device.
- Completion gate: playback/fallback, metadata/artwork, Queue/History, account isolation, chat, Favorites, requests, membership/request-activity support, secondary pages, and explicit unavailable states are independently evidenced; validators, physical-device verification, documentation, and publication pass.
- Status: interaction-ready. M21 is complete after representative Morgue sign-in/restore/isolation/Favorites/Chat/
  request/logout evidence. M22's public playback, metadata/artwork, extended Queue/History, Chat, request browsing,
  capability boundaries, trusted pages, and focused legacy-punctuation unit evidence already pass; only its hard
  authenticated gate and final reconciliation remain. The in-progress M22 implementation and tests are preserved
  unstaged until this gate closes.

## Latest completed milestone

M21 Death.FM certification completed July 15, 2026 after its existing public/device pass was closed with a representative Morgue session. Native sign-in, Android-protected process-restart restoration, station isolation, authenticated empty Favorites loading, Chat composer availability, green request eligibility, explicit station-only logout, and persistent signed-out state after another restart all pass. No credential, CAPTCHA, session value, Chat post, or song request was retained or submitted. RIP membership remains a separately trusted browser route; request messages, listener activity, and native membership remain explicitly unverified. See [m21-death-certification.md](m21-death-certification.md).

## Shared feature milestones

| Milestone | Size | Estimate | Usage | Rationale and outcome | Primary confidence variable |
| --- | --- | --- | --- | --- | --- |
| M17 Private Messages | L (provisional) | 4–8 hours after server repair | High | Add native station-isolated inbox/read/compose/reply/refresh and explicit user-initiated send over existing protected sessions | Website repair, production limits, and consistent authenticated forms |

Each shared milestone includes repository/ViewModel/UI work as applicable, lifecycle-safe state, accessibility semantics, focused tests, affected-module validation, wired Razr inspection, documentation, a focused commit, push, and remote confirmation.

## Station certification milestones

The certification program would be XL if treated as one unit, so it is split into five reviewable S–L milestones. Total expected active elapsed time is 20–35 hours, approximately 3–6 focused days. Confidence is Medium-Low because live accounts, CAPTCHA, station rules, rate limits, metadata quality, and legacy server behavior can differ.

| Milestone | Size | Estimate | Usage | Rationale and focus | Primary confidence variable |
| --- | --- | --- | --- | --- | --- |
| M18 StreamingSoundtracks.com certification | S | 2–4 hours | Medium | Most live coverage already exists; certify VIP/non-admin behavior, 30-row Queue, request messages, Favorites, chat, and authenticated workflows | Legacy PM stability and privileged accounts masking ordinary-member restrictions |
| M19 1980s.FM certification | M | 4–7 hours | High | Establish equivalent live evidence for playback/fallback, metadata, account isolation, Queue/history, chat, Favorites, requests, and membership behavior | Availability of a representative station account and undocumented rules |
| M20 Adagio.FM certification | M | 4–7 hours | High | Certify classical metadata presentation plus playback/fallback, account isolation, Queue/history, chat, Favorites, requests, and membership behavior | Metadata shape and availability of a representative station account |
| M21 Death.FM certification | L | 6–10 hours | High | Harden the compact Queue feed, sparse metadata/artwork behavior, RIP membership differences, playback/fallback, chat, Favorites, and requests | Reduced identifiers/metadata and station-specific membership behavior |
| M22 Entranced.FM certification | M | 4–7 hours | High | Establish live evidence for playback/fallback, metadata, account isolation, Queue/history, chat, Favorites, requests, and membership behavior | Availability of a representative station account and undocumented rules |

### Common station task breakdown and completion gate

1. Reconfirm permitted primary/fallback playback and Media3 behavior without changing working URLs casually.
2. Verify metadata, artwork, Queue/history limits, stale/error handling, and station-scoped parsing.
3. Verify independent authentication, restoration, expiration, logout, and account capability presentation.
4. Verify chat read/post limits, Favorites discovery, requests, eligibility, cooldown, attribution/messages, and Private Messages when available.
5. Preserve unsupported differences as explicit capability-unavailable states; never guess endpoints or rules.
6. Add or update parser, repository, ViewModel, Compose, accessibility, and isolation tests for station-specific evidence.
7. Run focused and broad validators, perform a physical Razr smoke test, update matrices/handoff, commit, push, and confirm the remote branch.

Unblocked M19–M22 work may proceed while M17 is deferred, but a station cannot receive final certification until every in-scope capability passes or the user explicitly changes Alpha scope.

## Distribution milestones

| Milestone | Size | Estimate | Usage | Rationale and outcome | Primary confidence variable |
| --- | --- | --- | --- | --- | --- |
| M23 Alpha Test Distribution Readiness | M | 1–2 focused days | Medium | Refresh privacy, tester guidance, signing guardrails, versioning, release artifacts, bundle checks, and Play readiness after M15–M22 | Custody/configuration of the upload signing identity and Console declarations |
| M24 Alpha publication completion | M | 1–3 hours after Console setup | Medium | Produce and verify the authorized signed Play bundle and internal/closed test release | Signing authorization, tester-track configuration, and release review outcome |

No item is classified XL. Any future phase that exceeds L will be divided before implementation.
