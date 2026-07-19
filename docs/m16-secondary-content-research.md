# M16 secondary content route research

Research performed July 14, 2026 against the public navigation of the five configured station origins. No sign-in, form submission, message, request, profile change, or other mutation was performed. No cookies, account data, captured HTML, or browser history are retained in the repository.

> Historical evidence: M31 later found that the membership destinations advertise paid digital benefits and account
> registration. They are no longer present in the global Play candidate. The current catalog contains only the fixed
> Contact email handoff; this document preserves the public route research that supported M16 and M20.

## Verified HTTPS routes

StreamingSoundtracks.com, 1980s.FM, Adagio.FM, and Entranced.FM each exposed the following exact public destinations on their own HTTPS origin:

- `/` — station home/news surface;
- `/modules.php?name=Forums` — community forums;
- `/modules.php?name=Members_List` — public member directory;
- `/modules.php?name=Stats` — public station statistics;
- `/modules.php?name=Top100` — station Top 100;
- `/modules.php?name=Contact_Us` — station contact page;
- `/modules.php?name=VIP_Subscribe` — membership information.

Two station-specific routes were also directly observed:

- StreamingSoundtracks.com: `/modules.php?name=STM` for Soundtrack of the Month.
- Historical 1980s.FM discovery included `/modules.php?name=Games` and `80s_Awards`; the later catalog refinement removed both, and they are not shipped capabilities.

These pages are intentionally not parsed or recreated in Compose. They are secondary, browser-appropriate content and open only after the listener taps a clearly labelled native card.

## Death.FM HTTPS limitation

The configured `https://death.fm/` origin failed modern browser TLS negotiation with `ERR_SSL_VERSION_OR_CIPHER_MISMATCH` during this audit. M16 does not downgrade to HTTP, guess an alternate host, or expose broken cards. Death.FM therefore reports that no secure secondary pages are verified. Its live stream behavior is unchanged; the route can be enabled during M20 only after the HTTPS origin is repaired and reverified.

### M20 follow-up — July 15, 2026

The configured HTTPS origin now negotiates successfully. A fresh read-only audit verified the station root, Forums,
Members List, Stats, Top 100, Contact, and the Death.FM-specific `RIP_Subscribe` membership route. M20 therefore
enabled these exact same-station HTTPS cards under the then-current trust policy. This follow-up supersedes only the
temporary Death.FM availability conclusion above; it does not rewrite the historical M16 observation or relax any
trust rule.

## Trust and traffic policy

- The historical catalog contained the complete allowlist; user input and server redirects could not add entries. M31
  removed the browser routes from the current catalog and explicitly rejects Membership destinations.
- A launch is accepted only when the exact page object belongs to the selected station, the station capability is enabled, the target is HTTPS, the canonical host matches the selected station (with only a `www.` alias normalized), the port is default/443, and neither credentials nor a fragment are present.
- Website cards allow only the exact origin root. Module cards allow only the catalogued `/modules.php` URL.
- The app makes no background request, poll, retry, or mutation for these pages. One explicit tap launches one Android Custom Tab.
- Protected app-session cookies are never copied to the browser. Browser sign-in and retention are controlled independently by the user's browser.
- Existing native Player, Favorites, Chat, Queue/History, Requests, account, and request-activity destinations are not duplicated.
