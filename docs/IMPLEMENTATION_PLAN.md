# Implementation plan

Updated July 14, 2026 after adopting explicit certification milestones for all five stations. Estimates are active Codex elapsed time in this environment, including inspection, implementation, Gradle validation, documentation, Git, and remote confirmation—not traditional human developer time.

## Planning model

- M1–M16 and M18 are complete and retain their existing evidence and published checkpoints.
- M17 is the remaining shared capability, using immutable Compose state, repository contracts, capability flags, and station-isolated protected sessions once its external blocker clears.
- M18–M22 certify the shared implementation against each station. They are hardening and evidence gates, not station-specific application forks.
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

### M19 — 1980s.FM certification

- Task Complexity Level: 2 — Feature Logic & API Integration
- Size: M
- Estimated elapsed time: 4–7 hours
- Usage intensity: High
- Confidence: Medium-Low
- Outcome: establish independent 1980s.FM evidence for every shared Alpha capability without inheriting SST account, membership, or request behavior.
- Expected layers: public protocol and physical playback/metadata checks, exact capability audit, independent account/session evidence where interaction is available, targeted parser/repository/UI fixes only for proven differences, validation, and publication.
- Dependencies: the existing verified stream/Queue/chat interfaces and, for authenticated checks, a representative 1980s.FM account plus user-entered CAPTCHA.
- Principal risk: no representative 1980s.FM protected session is currently available, and its membership/request rules may differ from SST despite similar legacy pages.
- Completion gate: playback/fallback, metadata/artwork, Queue/History, account isolation, chat, Favorites, requests, membership/request-activity support, secondary pages, and explicit unavailable states are independently evidenced; validators, wired-device verification, documentation, and publication pass.
- Status: in progress. Public playback, fallback evidence, metadata/artwork, Queue/History, Chat, request browsing,
  sign-in challenge loading, capability boundaries, and a Games Custom Tab round trip pass. Representative
  authenticated account evidence remains required; see [m19-1980s-certification.md](m19-1980s-certification.md).

## Latest completed milestone

M18 StreamingSoundtracks.com certification completed July 14, 2026 without a production-code or stream-address change. Existing ordinary-member and VIP live evidence was reconciled with fresh silent playback, artwork, Queue, Chat, Favorites-gate, navigation, and signed-out checks on the wired Razr. Unit tests, lint, debug install, and 21/21 wired instrumentation tests pass. Private Messages remain explicitly excluded under the M17 server-repair deferral. See [m18-sst-certification.md](m18-sst-certification.md).

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
7. Run focused and broad validators, perform a wired Razr smoke test, update matrices/handoff, commit, push, and confirm the remote branch.

Unblocked M19–M22 work may proceed while M17 is deferred, but a station cannot receive final certification until every in-scope capability passes or the user explicitly changes Alpha scope.

## Distribution milestones

| Milestone | Size | Estimate | Usage | Rationale and outcome | Primary confidence variable |
| --- | --- | --- | --- | --- | --- |
| M23 Alpha Test Distribution Readiness | M | 1–2 focused days | Medium | Refresh privacy, tester guidance, signing guardrails, versioning, release artifacts, bundle checks, and Play readiness after M15–M22 | Custody/configuration of the upload signing identity and Console declarations |
| M24 Alpha publication completion | M | 1–3 hours after Console setup | Medium | Produce and verify the authorized signed Play bundle and internal/closed test release | Signing authorization, tester-track configuration, and release review outcome |

No item is classified XL. Any future phase that exceeds L will be divided before implementation.
