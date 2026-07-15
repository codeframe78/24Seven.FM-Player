# Implementation plan

Updated July 14, 2026 after auditing the committed M1–M10 implementation and protected M11/M12/Favorites worktree. Estimates are active Codex elapsed time in this environment, including inspection, implementation, Gradle validation, documentation, Git, and remote confirmation—not traditional human developer time.

## Milestone state

- M1–M10 retain their existing names, evidence, and completed/pushed status.
- M11 Adaptive Alpha UI is complete locally but remains part of protected unpublished work.
- M12 Alpha Test Distribution Readiness is locally prepared. External completion is blocked by Play Console activation and final upload signing; it also contains the completed Favorites enhancement already present in the worktree.
- The next unused milestone number is M13.
- M13 implementation and validation are complete; commit/push confirmation is the remaining milestone gate.

## Current milestone

### M13 — Queue-aware request availability

- Size: L
- Estimated elapsed time: best case 4–5 hours; most likely 5–7 hours; high-risk 8+ hours
- Usage intensity: High
- Confidence: Medium
- Outcome: every available library/Favorites track shows green `Request Now`, while recently played and currently queued tracks both show red `Track Recently Played`, remain internally distinct, and cannot be submitted without fresh station-scoped revalidation.
- Expected layers: domain status/matching model, queue/recent-play integration, request and Favorites repositories, ViewModel flows, Compose status component, parser/repository/ViewModel/UI tests, documentation.
- Dependencies: existing public Queue authorization and 60-second limit; existing request/Favorites parsers; authenticated session only for final mutation; no new endpoint required.
- Principal risk: queue rows do not currently expose stable track IDs, so a conservative normalized composite match must avoid both false positives and unsafe false availability.
- Completion gate: exact labels and reusable theme tokens; queue and recent reasons distinct internally; station-scoped matching; safe stale/error behavior; refresh-driven recalculation; pre-submit revalidation; targeted and broad tests; docs, focused commit, push, and remote confirmation.
- Validation result: implementation, unit tests, lint, debug assembly, 13/13 API 35 instrumentation tests, physical Razr inspection, and documentation are complete. Publication remains.

## Upcoming milestones

| Milestone | Size | Estimate | Usage | Objective | Dependencies/status |
| --- | --- | --- | --- | --- | --- |
| M14 Independent Accounts UX and isolation tests | L | 4–8 hours | High | Present all five account states separately and expand pairwise session/logout/expiration isolation tests | Existing station-scoped store; planned |
| M15 Local personalization and station preferences | M | 2–4 hours | Medium | Persist default/last station and clearly labeled local favorites without merging server collections | Storage design; planned |
| M16 Request history, cooldown, and membership state | L | 4–8 hours | High | Add station-scoped request history and accurate cooldown/VIP/RIP labels where verified | Further endpoint/account evidence; planned |
| M17 Secondary community/content access | M | 2–4 hours | Medium | Add safe capability-aware Custom Tab routes for selected public content without a WebView replacement | Product prioritization of modules; planned |
| M18 Alpha publication completion | M | 1–3 hours after activation | Medium | Create/verify signed Play bundle and internal/closed test release | Google developer account activation and explicit release authorization; blocked |
| Private Messages | L (provisional) | 4–8 hours after server fix | High | Native inbox/read/compose/reply using station-isolated sessions | Website server issues; deferred |

No item is classified XL. Any future phase that exceeds L will be divided before implementation.
