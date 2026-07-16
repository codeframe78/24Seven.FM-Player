# Implementation plan

Updated July 15, 2026 after completing all five station-certification gates and reconciling the latest M23 device, library, Favorites, and notification checkpoint. Estimates are active Codex elapsed time in this environment, including inspection, implementation, Gradle validation, documentation, Git, and remote confirmation—not traditional human developer time.

## Planning model

- M1–M16 and M18 are complete and retain their existing evidence and published checkpoints.
- M17 is the remaining shared capability, using immutable Compose state, repository contracts, capability flags, and station-isolated protected sessions once its external blocker clears.
- M18–M22 certify the shared implementation against each station. They are hardening and evidence gates, not station-specific application forks.
- Each station's representative native login, protected restoration, isolation, authenticated surface, and logout flow is now a hard completion gate. Public/device evidence may be gathered early but does not advance the active station order.
- M23–M24 are the final distribution and publication gates.
- Private Messages remains numbered as M17 but deferred pending legacy server repair and verified production limits. Inbox and Sent Box discovery worked, while New Message selection remained suspect and a profile-originated MorgHubby test appeared to submit without delivery; the site owner has the reproduced result.
- Early M23 readiness artifacts are preserved, but they must be refreshed after M15–M22. The Play developer account is approved; Play App Signing/app setup, final upload signing, and explicit publication authorization remain dependencies.

## Task complexity protocol

Before code changes for every roadmap task, state its Task Complexity Level and a brief milestone-specific approach:

- Level 1 — Scaffold & Boilerplate: low reasoning; prioritize speed, Android best practices, clean architecture, concise production-ready code, and minimal explanation.
- Level 2 — Feature Logic & API Integration: medium reasoning; emphasize robust errors, immutable ViewModel state, coroutine/thread safety, implementation detail, test recommendations, and clear state-flow explanation.
- Level 3 — Architectural Refactoring & System Design: high reasoning; compare alternatives, justify the chosen scalable pattern, and assess performance, dependency-injection, maintenance, and side effects.

This classification supplements the required XS–XXL T-shirt size, rationale, estimated duration, confidence check, and high-level task breakdown. Every roadmap completion update must also include repository and Discord screenshots for significant visual changes. A backend-only milestone should explicitly state that it introduced no meaningful visual surface instead of reusing an unrelated image.

## Current milestone

### M23 — Alpha Test Distribution Readiness

- Task Complexity Level: 2 — Feature Logic & API Integration
- Size: M
- Estimated elapsed time: 1–2 focused days
- Usage intensity: Medium
- Confidence: Medium
- Outcome: refresh and finish Alpha privacy, versioning, signing guardrails, tester guidance, Play Console declarations, release artifact checks, and reproducible bundle validation after all feature and station work.
- Expected layers: release configuration, secure local signing handoff, privacy/data-safety reconciliation, bundle and APK inspection, Play checklist, tester documentation, and final repository/Discord evidence.
- Dependencies: approved Google Play developer account, selection/custody of the upload signing identity, Play App Signing setup, final app listing/configuration, and explicit authorization before any publication action.
- Principal risk: securely coordinating the upload key and Play Console declarations without committing secrets or creating an incompatible signing lineage.
- Completion gate: version/release configuration, privacy and tester materials, signing guardrails, reproducible release bundle validation, dependency/license review, final physical smoke test, documentation, focused commits, and remote publication evidence all pass; no store release is submitted during M23.
- Status: in progress. M18–M22 and all five representative station login gates are complete. Release assets/signing, adaptive layouts, notification navigation, multi-field library search, play-state sorting, and responsive 1,500-track Favorites handling are validated. Protected reviewer access, remaining Console declarations, tester setup, and Play-delivered device/update checks still gate completion. M17 Private Messages remains explicitly deferred for server repair.

## Latest completed milestone

M22 Entranced.FM certification completed July 15, 2026 after its public/device and representative authenticated gates passed. Native sign-in, Android-protected process-restart restoration, station isolation, authenticated empty Favorites loading, Chat composer availability, green request eligibility, explicit station-only logout, and persistent signed-out state after another restart all pass. A narrowly scoped ICY boundary correction maps defined Windows-1252 punctuation without transcoding valid Unicode; 110 unit tests, lint, Windows validation, 21 connected tests, standalone install, and live Media3 playback pass. No credential, CAPTCHA, session value, Chat post, or song request was retained or submitted. See [m22-entranced-certification.md](m22-entranced-certification.md).

## Shared feature milestones

| Milestone | Size | Estimate | Usage | Rationale and outcome | Primary confidence variable |
| --- | --- | --- | --- | --- | --- |
| M17 Private Messages | L (provisional) | 4–8 hours after server repair | High | Add native station-isolated inbox/read/compose/reply/refresh and explicit user-initiated send over existing protected sessions after the reproduced profile-send non-delivery is repaired | Website repair, delivery confirmation, production limits, and consistent authenticated forms |

Each shared milestone includes repository/ViewModel/UI work as applicable, lifecycle-safe state, accessibility semantics, focused tests, affected-module validation, physical Razr inspection, documentation, a focused commit, push, and remote confirmation.

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

M18–M22 are complete while M17 remains deferred; every in-scope station capability now has passing evidence or an explicit certified unavailable/unverified boundary.

## Distribution milestones

| Milestone | Size | Estimate | Usage | Rationale and outcome | Primary confidence variable |
| --- | --- | --- | --- | --- | --- |
| M23 Alpha Test Distribution Readiness | M | 1–2 focused days | Medium | Refresh privacy, tester guidance, signing guardrails, versioning, release artifacts, bundle checks, and Play readiness after M15–M22 | Custody/configuration of the upload signing identity and Console declarations |
| M24 Alpha publication completion | M | 1–3 hours after Console setup | Medium | Produce and verify the authorized signed Play bundle and internal/closed test release | Signing authorization, tester-track configuration, and release review outcome |

No item is classified XL. Any future phase that exceeds L will be divided before implementation.
