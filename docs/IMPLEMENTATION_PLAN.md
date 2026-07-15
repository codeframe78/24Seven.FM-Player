# Implementation plan

Updated July 14, 2026 after completing and publishing M13. Estimates are active Codex elapsed time in this environment, including inspection, implementation, Gradle validation, documentation, Git, and remote confirmation—not traditional human developer time.

## Milestone state

- M1–M10 retain their existing names, evidence, and completed/pushed status.
- M11 Adaptive Alpha UI is complete and published in `4735f13`.
- M12 Alpha Test Distribution Readiness is prepared and published. External completion is blocked by Play Console activation and final upload signing; it also contains the completed Favorites enhancement.
- M13 Queue-aware request availability is complete, validated, and published in `4735f13`.
- M14 is the next milestone and has not started.

## Current milestone

### M14 — Independent Accounts UX and isolation tests

- Size: L
- Estimated elapsed time: best case 4–5 hours; most likely 5–7 hours; high-risk 8+ hours
- Usage intensity: High
- Confidence: Medium
- Outcome: present all five station account states separately, support station-specific sign-in/sign-out actions, and expand pairwise host/session/logout/expiration isolation coverage without changing protected-session boundaries.
- Expected layers: aggregate immutable account UI state, station account cards/actions, repository-contract integration, pairwise isolation and restoration tests, accessibility/UI tests, documentation, device smoke tests.
- Dependencies: existing station-scoped session store and authentication repositories. Implementation can proceed with fakes; complete live verification depends on suitable non-sensitive test accounts and each station's security-challenge flow.
- Principal risk: live authentication behavior and account capabilities may differ by station, while credentials and challenge responses must never enter source, logs, or fixtures.
- Completion gate: every station state remains independently visible and actionable; sign-out/expiration on one host cannot affect another; restoration is host scoped; unit/UI tests and available device smoke tests pass; documentation, focused commit, push, and remote confirmation are complete.
- Status: preflight ready; implementation has not started.

## Upcoming milestones

| Milestone | Size | Estimate | Usage | Objective | Dependencies/status |
| --- | --- | --- | --- | --- | --- |
| M15 Local personalization and station preferences | M | 2–4 hours | Medium | Persist default/last station and clearly labeled local favorites without merging server collections | Storage design; planned |
| M16 Request history, cooldown, and membership state | L | 4–8 hours | High | Add station-scoped request history and accurate cooldown/VIP/RIP labels where verified | Further endpoint/account evidence; planned |
| M17 Secondary community/content access | M | 2–4 hours | Medium | Add safe capability-aware Custom Tab routes for selected public content without a WebView replacement | Product prioritization of modules; planned |
| M18 Alpha publication completion | M | 1–3 hours after activation | Medium | Create/verify signed Play bundle and internal/closed test release | Google developer account activation and explicit release authorization; blocked |
| Private Messages | L (provisional) | 4–8 hours after server fix | High | Native inbox/read/compose/reply using station-isolated sessions | Website server issues; deferred |

No item is classified XL. Any future phase that exceeds L will be divided before implementation.
