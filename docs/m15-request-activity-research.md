# M15 request activity and membership research

Research performed July 14, 2026 under the existing administrator authorization for authenticated public-facing account and song-request interfaces. No request, message, profile change, or other mutation was submitted. No cookies, credentials, challenge answers, member identifiers, captured HTML, or session material are retained.

## Verified StreamingSoundtracks.com evidence

The authenticated navigation exposes `Your Requests` at `/modules.php?name=Your_Requests`. The page contains a table headed `Your Last 10 Requests` with at most ten explicit rows. Each row supplies a position, a station-rendered album/track/artist summary, and a requested-at label. The native parser preserves that combined summary instead of guessing field boundaries that the legacy markup does not provide.

The same authenticated page supplies same-origin discovery links for two additional read-only records:

- `/modules/VIP_Subscribe/vip_req_timer.php` reports `Your Request` and `Request Wait` values. Representative VIP evidence reported `Ready` and `0 Minutes`.
- A same-origin forum profile link with a discovered numeric member identifier leads to a profile table that separately reports community rank and an explicit VIP badge. The app reads only the VIP/RIP membership badge and does not treat administrator or moderator rank as membership.

Every discovered URL is restricted to HTTPS, the selected station origin, an exact allowed path, and recognized bounded parameters. Cross-origin, malformed, or unrecognized timer/profile links are ignored. A returned login form is treated as expired authentication.

## Product and traffic policy

- Loading is user initiated by opening More or tapping Refresh while signed in; there is no polling.
- One SST refresh performs one request-history read and, when the page supplies trusted sources, one timer read and one profile read.
- Results remain memory-only and are cleared from the interface when that station signs out.
- History is capped at ten entries; summaries and timestamps are bounded plain text.
- No request mutation is performed and no failure is automatically retried.
- Missing evidence remains `Unknown`; the app never infers VIP/RIP from administrative rank, newsletter subscription text, or generic site navigation.

Only StreamingSoundtracks.com is enabled for the M15 native surface. The other four stations expose similar public navigation, but authenticated timer/profile behavior has not yet been independently verified. Their capability remains unavailable until M18–M21 certification provides representative evidence. Death.FM's RIP branding is modeled but not enabled without that verification.
