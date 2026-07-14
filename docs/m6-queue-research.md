# M6 queue and history research

Observed on July 13, 2026. This note contains no cookies, credentials, private messages, device identifiers, track values, or undocumented endpoint addresses.

## Scope

M6 aims to provide native upcoming-queue and recently-played lists for each station. A source is suitable only if it is station-owned, structured, reliable across the supported network, and permitted for an unofficial client.

## Official evidence

All five station-owned `player.php` pages visibly offer Queue and History. Their legacy network-player launchers redirect to those pages. Adagio's public `studio.php` page also renders both lists.

The player client code first reads a public current-track JSON document. That document contains current-track metadata only; it does not contain queue or history collections. The client then makes an undocumented same-page request using the current album identifier. Its response embeds queue and history as HTML fragments inside JSON rather than returning stable track objects.

A sanitized, unauthenticated check found:

- no cookies were set or required by the player page or current-track document;
- the internal queue/history request returned HTTP 200 for StreamingSoundtracks.com;
- the equivalent request returned HTTP 500 for 1980s.FM, Adagio.FM, Death.FM, and Entranced.FM at the same observation time;
- the one successful response contained ten three-cell rows for each list;
- Adagio's Live Studio refreshes and reparses the complete HTML page rather than using a structured public feed;
- no station-owned developer or API documentation describing queue/history reuse was found;
- the shared robots policy does not disallow the public player page, but robots policy is not integration authorization;
- the published terms permit personal, non-commercial use of the services but do not expressly authorize third-party application reuse of internal page data.

## Decision

The internal HTML-fragment response and full-page Live Studio HTML are **not suitable for implementation**. Depending on either would introduce unsupported scraping, fail four supported stations today, and conflict with the requirement not to commit undocumented private endpoints.

M6 therefore keeps every queue/history capability disabled. The app now has a station-scoped `QueueRepository` contract, immutable unavailable/loading/ready/error state, refresh actions, and native queue/history rendering that can accept a supported implementation without changing Compose or playback ownership.

## Unblocking requirement

Real queue/history data needs one of:

1. a documented, stable, structured station-owned feed with stated client-use terms; or
2. written authorization from 24seven.FM identifying the supported endpoint, fields, polling limits, and station coverage.

When either exists, recheck all five stations, document pagination and refresh behavior, enable only verified capability flags, and add parser, repository, ViewModel, UI, failure, and device tests.
