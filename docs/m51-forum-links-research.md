# M51 historical Forum links research

Date: July 19, 2026

Status: retired by project decision on July 19, 2026. This audit is historical evidence only; the Player will not
expose Forum links, Custom Tabs, native retrieval, participation, or notifications.

## Authorization and scope

An authorized site administrator approved verification and Player exposure of the public Forum pages for
StreamingSoundtracks.com, 1980s.FM, Adagio.FM, Death.FM, and Entranced.FM through station-scoped HTTPS Custom Tabs.
The authorization covers public Forum links only. It does not authorize native retrieval, automated polling,
authentication/session transfer, posting, administrator access, private content, or payment behavior.

The audit used credential-free GET requests and isolated headless-browser rendering. It supplied no cookie, account,
CAPTCHA response, form value, or referrer; performed no mutation; and retained no response body, Forum content, member
identifier, or browser profile in the repository.

## Five-station route evidence

| Station | Exact candidate route | Transport result | Browser rendering |
| --- | --- | --- | --- |
| StreamingSoundtracks.com | `https://streamingsoundtracks.com/modules.php?name=Forums` | HTTP/2 200 over verified TLS 1.2 or newer; no redirect | Title rendered as `StreamingSoundtracks - Forums` |
| 1980s.FM | `https://1980s.fm/modules.php?name=Forums` | HTTP/2 200 over verified TLS 1.2 or newer; no redirect | Title rendered as `1980s.FM - Forums` |
| Adagio.FM | `https://adagio.fm/modules.php?name=Forums` | HTTP/2 200 over verified TLS 1.2 or newer; no redirect | Title rendered as `Adagio.FM - Forums` |
| Death.FM | `https://death.fm/modules.php?name=Forums` | HTTP/2 200 over verified TLS 1.2 or newer; no redirect | Title rendered as `Death.FM - Forums` |
| Entranced.FM | `https://entranced.fm/modules.php?name=Forums` | HTTP/2 200 over verified TLS 1.2 or newer; no redirect | Title rendered as `Entranced.FM - Forums` |

The non-`www` URL is intentional. The `www` HTTPS Forum URL for SST, 1980s.FM, Adagio.FM, and Death.FM currently
redirects to plain HTTP on the non-`www` host; `www.entranced.fm` did not resolve. The Player must never launch or
follow that downgrade. The exact non-`www` HTTPS destinations above do not redirect and remain compatible with the
existing canonical-host comparison.

## Reachable-navigation findings

The verified Forum landing page for every station includes same-origin navigation to:

- the station account surface;
- VIP subscription and gift pages, or RIP equivalents for Death.FM;
- station privacy and Terms pages; and
- public Forum boards, profiles, searches, and threads.

Representative public thread requests remained same-origin HTTPS for SST, 1980s.FM, Adagio.FM, and Death.FM. The
sampled Entranced.FM thread redirected to its same-origin account page. No signed-out report-user, report-content, or
block-user control was detected in the sampled public thread markup. Because posts are user-generated, their links are
not restricted to the station origin; one sampled public post included a plain-HTTP third-party link. No external link
was followed.

These are properties of the browser destination, not of the native trust policy. `StationPageTrustPolicy` can approve
the exact initial HTTPS Forum URL, but a Custom Tab then allows the station page and its UGC to navigate independently.

## Play and safety decision

The current global Play candidate must not add these Forum cards yet:

1. Google Play policy coverage applies to content an app displays or links to, including linked UGC.
2. The Payments policy prohibits leading users to an alternate payment method, including a webpage that can
   eventually lead to one, unless an applicable enrolled program and its requirements are satisfied.
3. The current Forum pages directly expose VIP/RIP digital-subscription routes that M31 removed from the Player.
4. Apps linking users to publicly accessible UGC must provide effective Terms, moderation, report-content/report-user,
   and block-user behavior. The current Custom Tab handoff cannot bind a displayed Forum post/author to the Player's
   native report and local-block controls, and adequate signed-out site controls were not verified.
5. Arbitrary downstream UGC links also prevent the app's exact-entry HTTPS policy from governing what the browser can
   reach after launch.

Site-administrator authorization resolves permitted station use, but it does not supersede Google Play Payments or UGC
requirements. Adding the cards now would regress M31 and make the M29 declarations inaccurate.

## Superseded remediation options

Before the retirement decision, M51 could have resumed when one of these designs was approved and verified:

- a dedicated public HTTPS Forum experience for Player users whose complete reachable navigation cannot lead to
  VIP/RIP purchase, gift, registration, or another alternate-payment path; keeps navigation HTTPS; and provides
  effective Terms, report-content, report-user, and block-user controls; or
- an applicable Google Play external-link/billing program is approved and fully integrated, while the linked-UGC
  moderation and unsafe-downstream-link requirements are independently resolved.

M52 remains a separate option only after explicit native-retrieval authorization defines permitted fields, cadence,
pagination, sanitized fixtures, and reporting/blocking behavior. M51 does not authorize M52 or M53.

## Final implementation boundary

- The bootstrap catalog remains Contact-only.
- No Forum, payment, registration, recovery, management, or deletion card is exposed.
- No WebView, network adapter, parser, cookie bridge, new permission, or new dependency was added.
- The Forum page kind and its dedicated trust-policy test were removed. The generic Custom Tab boundary remains only
  for separately authorized non-Forum future scope and must not be used for Forum access.
- PT-18 remains the current Contact-only regression. Forum-specific future cases were removed from the Product Testing
  catalog; M51–M54 remain retired historical IDs.

Official policy references:

- [Google Play policy coverage](https://support.google.com/googleplay/android-developer/answer/10146128)
- [Google Play Payments policy](https://support.google.com/googleplay/android-developer/answer/9858738)
- [Understanding Google Play's Payments policy](https://support.google.com/googleplay/android-developer/answer/10281818)
- [Google Play User-generated content policy](https://support.google.com/googleplay/android-developer/answer/9876937)
- [UGC moderation guidance](https://support.google.com/googleplay/android-developer/answer/12923286)

This is a conservative technical release decision, not legal advice or a guarantee of Play approval.
