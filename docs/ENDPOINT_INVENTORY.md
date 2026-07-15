# Endpoint inventory

Sanitized inventory updated July 14, 2026. No cookies, credentials, CAPTCHA tokens, authentication headers, personal identifiers, or message IDs belong in this document.

All relative HTTPS paths below are resolved independently against one of these five station origins: `streamingsoundtracks.com`, `1980s.fm`, `adagio.fm`, `death.fm`, or `entranced.fm`. Requests validate the expected origin before sending station sessions.

| Station(s) | Purpose | Endpoint/protocol | Format | Authentication | Stability/refresh/cache | Error behavior | Authorization/status |
| --- | --- | --- | --- | --- | --- | --- | --- |
| All five | Primary live audio | Station-provided `http://hi5.<station-domain>/;` | ICY AAC+ stream | None | Continuous Media3 playback; first priority | One controlled fallback | Verified PLS; implemented and permitted |
| All five | Fallback live audio | Station-provided `http://hi.<station-domain>/;` | ICY AAC+ stream | None | Used only after primary failure | Playback error after bounded fallback | Verified PLS; implemented and permitted |
| All five | ICY metadata | Audio response headers/metadata | ICY text | None | Event driven; no independent poll | Playback continues without metadata | Implemented/verified |
| All five | Current artwork | `/soap/FM24sevenJSON.php?action=GetCurrentlyPlaying` | Bounded JSON | None | Once per distinct ICY title; no interval poll | Omit artwork and retain station fallback | Implemented/verified |
| SST, 1980s, Adagio, Entranced | Queue and recent history | `/modules/Queue_Played/Queue_Played-gen.php`, Queue page referer | Bounded ISO-8859-1 HTML | None | No more than once per 60 seconds; memory last-known | Preserve prior ready state or show refresh error | Administrator-authorized; implemented |
| Death.FM | Compact queue/history | `/player.php?ajax_action=get_db_info&station=dfm&asin=` | Bounded JSON containing HTML fragments | None | Same shared 60-second limit | Same queue repository error policy | Administrator-authorized; implemented |
| All five | Login challenge/form discovery | Station HTTPS root; server-provided same-origin form action | Bounded HTML + CAPTCHA image | Credentials transient on POST | User initiated; restored session revalidated | New challenge and safe error | Administrator-authorized; implemented |
| All five | Logout | `/modules.php?name=Your_Account&op=logout` | HTML/redirect | Station session | User initiated | Local protected session cleared even on remote failure | Implemented |
| All five | Chat read | `/modules/ClearChat/block-files/view.php?username=&sort=desc` | Bounded ISO-8859-1 HTML | No | 30-second minimum; memory-only | Prior state/error, no persisted history | Administrator-authorized; implemented |
| All five | Chat post-form discovery | Station root plus server form at `/modules/ClearChat/block-files/input.php` | HTML form | Station session | User initiated | No mutation without validated form/session | Administrator-authorized; implemented |
| All five | Chat post | Validated ClearChat form action | Form POST | Station session | Explicit user action; one confirmation read | No automatic repost | Administrator-authorized; implemented |
| All five | Request search | `/modules.php?name=Requests` with station search parameters | Bounded HTML | No | User initiated; no polling | Clear loading/error/empty state | Administrator-authorized; implemented |
| All five | Album/request eligibility | `/modules.php?name=Album&asin=<validated-id>` | Bounded HTML | No for browse | User initiated and re-read immediately before mutation | Reject untrusted/malformed actions or changed eligibility before submission | Implemented with M12 queue-aware resolver |
| All five | Random suggestions | `/modules.php?name=Requests` with verified `random` or `randomleast` parameter | Bounded HTML | No for browse | User initiated only | Empty/error state | Implemented |
| All five | Song request mutation | `/modules.php?name=Req&asin=<validated-id>&songID=<validated-id>` | HTML/redirect | Station session | Explicit confirmation; never automatically retried | Indeterminate results direct user to Queue | Administrator-authorized; implemented |
| SST only | Optional request message | Server-returned same-origin `/modules.php?name=Album&action=submitmessage...` form | Form POST | SST session | Only after explicit accepted request; 80 chars | Message failure never repeats song request | Administrator-authorized and verified |
| All five | Favorites discovery | `/modules.php?name=Favorites` | Bounded authenticated HTML | Station session | On destination open/manual refresh; no polling | Sign-in-specific or generic load error | Administrator-authorized; implemented |
| All five | Favorites list | Strict same-origin `/modules/Favorites/thelist.php?user2view=<discovered-id>` | Bounded HTML | Station session | Loaded after discovery; memory-only | Reject cross-origin/malformed list URL | Implemented; SST live verified |
| All five | Public secondary modules | Account, PM, forums, members, queue, history, news and station-specific module links | Legacy HTML | Mixed | No native polling configured | Open only through a future documented native/Custom Tab policy | Audited; mostly planned/deferred |

## Caching and safety policy

- Queue, chat, request catalog, and Favorites data are memory-only today. Queue retains its last ready state when a later refresh fails.
- Artwork URL acceptance is restricted to the selected station host. Authentication, request, chat, and Favorites actions require same-origin validation.
- Public queue polling must remain at or below once per 60 seconds per selected station. Chat reads must remain at or below once per 30 seconds.
- M12 fails closed when queue or fresh eligibility cannot be established before a request; it never infers `Request Now` solely from stale display state.
