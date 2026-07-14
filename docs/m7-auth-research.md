# M7 authentication research

## Authorization

On July 13, 2026, a station administrator authorized this unofficial, non-commercial native Android app to use
the public-facing login and session interfaces across all five stations. User-entered credentials may be
submitted, resulting sessions may be retained in Android-protected storage, and login research plus
least-privileged testing are permitted.

Accounts are currently station-specific, although the administrator noted that this may change. The app must
therefore keep authentication station-scoped while allowing a future shared-account implementation behind the
same repository contract.

This authorization does not yet cover chat reads/writes or song-request submission.

## Scope and current boundary

M7 adds native account authentication under the authorization above. The earlier authorization for public
queue and history polling remains separate and does not authorize chat or request submission.

The repository currently contains only safe groundwork:

- a station-scoped `AuthRepository` contract;
- immutable unavailable, signed-out, signing-in, signed-in, and error states;
- a no-operation implementation that remains unavailable;
- capability-aware UI reporting; and
- no concrete endpoint, cookies, credentials, tokens, or captured traffic.

Public station pages state that registered members can use chat and song requests. Similar page structure is
visible across the station sites, but this is not sufficient evidence that account databases, login behavior,
or browser sessions are shared. No login was attempted during this research.

## Public login protocol evidence

On July 13, 2026, the public home page for each station exposed the same login form shape:

| Station | Verified HTTPS origin | Method and public action |
| --- | --- | --- |
| StreamingSoundtracks.com | `https://streamingsoundtracks.com/` | `POST /modules.php?name=Your_Account` |
| 1980s.FM | `https://1980s.fm/` | `POST /modules.php?name=Your_Account` |
| Adagio.FM | `https://adagio.fm/` | `POST /modules.php?name=Your_Account` |
| Death.FM | `https://death.fm/` | `POST /modules.php?name=Your_Account` |
| Entranced.FM | `https://entranced.fm/` | `POST /modules.php?name=Your_Account` |

Each form uses `username`, `user_password`, `gfx_check`, a transient six-digit `random_num`, and the operation
`login`. The page renders a security-code image derived from the transient challenge, so native login must show
that image and require the user to enter its code. Challenge values must remain memory-only and must never be
logged or committed. A fresh anonymous page load did not set a cookie on any of the five origins.

The `www.streamingsoundtracks.com` HTTPS host presented a certificate-name mismatch to the Windows client. The
certificate-valid canonical origin is `https://streamingsoundtracks.com/`, which served the matching form. The
app must not disable certificate validation or send credentials to the mismatched `www` origin.

A least-privileged StreamingSoundtracks.com account was subsequently used through the visible in-app browser,
with credentials entered directly by the administrator. Successful login redirected to the account profile.
Both the profile and a later home-page navigation displayed the matching welcome identity and same-origin
logout action, while the anonymous password field was absent. These three conditions form the native success
rule; no cookie values or stored password data were inspected.

The legacy signed-in page also contained an unrelated link with an account-derived value in its URL. The app
must treat that value as sensitive, ignore unrelated links when classifying authentication, and never store,
log, or expose it. Authenticated cookie names and attributes, failure responses, expiry, and completed logout
behavior remain to be verified.

## Confirmed and remaining protocol questions

The administrator has confirmed login/session permission and station-specific accounts. Protocol research must
still determine:

1. Whether an app-specific API exists and is preferred over the legacy browser form.
2. Any login rate limit, CAPTCHA, multi-factor authentication, lockout, or automated-client restriction.
3. How long a session should
   remain valid.
4. The authoritative signals for successful login, expired sessions, disabled accounts, and logout.
5. Whether registration and password recovery must remain external website flows.
6. A least-privileged test account or an administrator-supervised device test. Credentials must be supplied
   out of band or typed directly on the device and must never enter source control, documentation, logs, or
   test fixtures.

Authorization for chat reads/writes and song requests will be documented separately in their own milestones.

## Intended implementation

- Compose owns only transient field state and emits sign-in and sign-out actions upward.
- `MainViewModel` will depend only on `AuthRepository`.
- The data layer will own redirects, CSRF handling, cookies, expiry detection, and response limits.
- Passwords will be used only for the active sign-in operation and will not be persisted or logged.
- Persisted session material will use Android platform-protected storage and will be cleared on logout or
  invalidation.
- Public playback and public queue/history will continue working while signed out or after session failure.
- Station authentication capability flags will remain false until the corresponding flow is verified.

## Validation plan

- Unit tests for state transitions, response classification, expiry, logout, and secret redaction.
- Integration tests against sanitized local fixtures; no captured live session values.
- Compose tests for validation, progress, errors, signed-in identity, and logout.
- A least-privileged physical-device login/logout/expiry pass on the Motorola Razr 2023.
- Regression validation for public playback and queue/history while signed out.

## Groundwork validation

The safe authentication groundwork passed unit tests, lint, debug and release compilation, and the repository's
Windows validation script. All four existing connected Android tests also passed on the Motorola Razr 2023
running API 35 after wireless ADB was restored. The GitHub Android build passed for commit `bb416d0`.

This validates the repository boundary and unavailable UI state only. No login request was made, and no live
authentication behavior should be considered validated.
