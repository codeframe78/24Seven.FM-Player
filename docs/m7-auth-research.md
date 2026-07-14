# M7 authentication research

## Scope and current boundary

M7 will add native account authentication only after the station administrator confirms that the unofficial,
non-commercial Android app may use the login and session interfaces. The earlier authorization for public
queue and history polling does not authorize authentication, chat, or request submission.

The repository currently contains only safe groundwork:

- a station-scoped `AuthRepository` contract;
- immutable unavailable, signed-out, signing-in, signed-in, and error states;
- a no-operation implementation that remains unavailable;
- capability-aware UI reporting; and
- no concrete endpoint, cookies, credentials, tokens, or captured traffic.

Public station pages state that registered members can use chat and song requests. Similar page structure is
visible across the station sites, but this is not sufficient evidence that account databases, login behavior,
or browser sessions are shared. No login was attempted during this research.

## Administrator confirmation required

Before the network implementation begins, obtain written confirmation of:

1. Permission for this app to submit login requests and maintain authenticated sessions across each of the
   five station sites.
2. Whether one account works network-wide or each station has an independent account and session.
3. Whether an app-specific API exists and is preferred over the legacy browser form.
4. Any login rate limit, CAPTCHA, multi-factor authentication, lockout, or automated-client restriction.
5. Whether the app may retain session material in Android-protected storage and how long a session should
   remain valid.
6. The authoritative signals for successful login, expired sessions, disabled accounts, and logout.
7. Whether registration and password recovery must remain external website flows.
8. A least-privileged test account or an administrator-supervised device test. Credentials must be supplied
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
