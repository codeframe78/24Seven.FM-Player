# Authentication matrix

Updated July 14, 2026. The five account systems are independent even where their legacy pages and field names are similar. No credential, active cookie, CAPTCHA token, private identifier, or authentication header is recorded here.

| Station | Login discovery | Registration | Password recovery | Account management | Logout | Form/session behavior | Native suitability and limitations |
| --- | --- | --- | --- | --- | --- | --- | --- |
| StreamingSoundtracks.com (`sst`) | HTTPS root supplies the current same-origin login form and alphanumeric CAPTCHA | `/modules.php?name=Your_Account&op=new_user` | Public account flow; exact recovery route not yet separately verified | `/modules.php?name=Your_Account` and `op=edituser` | `op=logout` | `username`, transient password, `gfx_check`, station challenge token; station cookies; same-origin redirects; encrypted restored session | Native login implemented and physically verified; VIP status is not yet modeled |
| 1980s.FM (`1980s`) | Independent 1980s.FM root/form/CAPTCHA | Same station-relative registration module | Exact recovery route not yet separately verified | Same station-relative account/edit module | Same station-relative logout | Separate station ID, expected host, cookie manager entry, encrypted storage key, and auth state | Native adapter implemented; requires live account verification independent of SST |
| Adagio.FM (`adagio`) | Independent Adagio.FM root/form/CAPTCHA | Same station-relative registration module | Exact recovery route not yet separately verified | Same station-relative account/edit module | Same station-relative logout | Fully station-scoped session and host validation | Native adapter implemented; requires independent live account verification |
| Death.FM (`death`) | Independent Death.FM root/form/CAPTCHA | Same station-relative registration module | Exact recovery route not yet separately verified | Same station-relative account/edit module | Same station-relative logout | Fully station-scoped session and host validation | Native adapter implemented; membership branding differs (`RIP`) and is not yet modeled |
| Entranced.FM (`entranced`) | Independent Entranced.FM root/form/CAPTCHA | Same station-relative registration module | Exact recovery route not yet separately verified | Same station-relative account/edit module | Same station-relative logout | Fully station-scoped session and host validation | Native adapter implemented; requires independent live account verification |

## Isolation implementation

- `StationId` keys every authentication state, challenge, cookie manager, encrypted session record, and encrypted display identity.
- Each network call resolves an exact station origin and rejects cross-origin authentication destinations.
- Protected cookies are filtered to the expected domain before storage and again during restore.
- Signing out clears only the selected station's cookie manager and protected record. Other station sessions remain intact.
- Passwords and CAPTCHA answers are transient method parameters and are not written to Room, DataStore, preferences, logs, resources, build files, or documentation.
- Chat, requests, and Favorites load only the selected station's protected session for that station's expected host.

## Current gaps and required tests

- The UI shows the selected station's account rather than a consolidated five-account dashboard.
- Registration, recovery, and account-management actions are not yet native destinations or documented Custom Tab actions.
- Session expiration is detected during restore/login-dependent operations, but explicit cross-station expiration and logout regression coverage must be expanded.
- Membership/VIP status and per-station request cooldown state are not modeled.
- Required future security coverage: pairwise host/session isolation, one-station logout preservation, one-station expiration, station-scoped Favorites/request history, and station-scoped queue effects.
