# Master M16B — Player public activation and Automatic SSL

Status: **Complete**

Completed July 22, 2026. The owner approved the Player public activation,
dedicated Webuzo Automatic SSL enrollment, Player-only HTML `no-transform`
correction, and retention of the strict CSP with the documented Lighthouse SEO
exception.

## Scope delivered

- Activated only `player.jamesjennison.net`.
- Created exactly one proxied Cloudflare A record with TTL Auto.
- Retained Cloudflare Full (Strict).
- Issued a dedicated Webuzo-managed Let's Encrypt certificate.
- Promoted the reviewed static artifact to its isolated Webuzo document root.
- Added an artifact-local HTML `no-transform` directive after separate owner
  approval.
- Validated production routes, redirects, TLS, headers, browser behavior,
  Lighthouse, logs, backup restoration, rollback, and unaffected services.
- Left GitHub Pages, the master site, status monitoring, Play Console,
  unrelated DNS, mail, and shared server configuration unchanged.

## Production identity

| Field | Value |
| --- | --- |
| Public URL | `https://player.jamesjennison.net/` |
| Canonical hostname | `player.jamesjennison.net`; no `www` variant |
| Source branch | `codex/player-site-migration` |
| Artifact source checkpoint | `2cb59c8` |
| Artifact digest | `9ba4d8e103f23201d5705ce7bc300d0c3247f585cc2be9674cd068f3d8863396` |
| Artifact inventory | 25 files; 3,587,618 bytes |
| Webuzo user | `jamesjen` |
| Document root | `/home/jamesjen/player.jamesjennison.net` |
| Server | Webuzo 4.7.4; Apache 2.4.68 |
| Runtime | None; static site |
| Cloudflare | One proxied A record, TTL Auto; Full (Strict) |
| Origin SSL | Dedicated Let's Encrypt SAN only for `player.jamesjennison.net` |
| Certificate validity | Through October 21, 2026 |
| Registered next renewal | September 20, 2026 |

The origin target, Cloudflare identifiers, account identifiers, certificate
private material, tokens, DNS mail values, and backup credentials are
intentionally omitted.

## Activation evidence

The following passed:

- eight canonical routes return 200;
- an unknown route returns the custom Player 404 with status 404;
- HTTP upgrades to HTTPS while preserving path and query string;
- edge and direct-origin certificate chain and hostname validation;
- exact Full (Strict) origin operation without a 526 response;
- public `.git`, `.env`, private-key, repository-documentation, and internal
  path probes return 404;
- HTML and custom-404 responses carry the six approved security headers plus
  the approved cache directive;
- no Cloudflare-injected script or browser-console error remains;
- Apache syntax, Webuzo, MariaDB, and the existing backup timer are healthy;
- no dedicated Node, Jekyll, application, reverse-proxy, or other background
  process is required.

The temporary HTTP-01 validation file was randomized, used only to prove the
Cloudflare-to-origin challenge path, and removed before completion. Webuzo's
daily renewal job remains present exactly once.

## Browser and quality results

Chromium production tests passed at 320, 390, 768, 1440, and 1920 pixel
viewports. Firefox production tests passed at 500, 768, 1024, and 1440 pixels.
The two suites covered every route, responsive overflow, images, navigation,
pointer and keyboard interactions, reduced motion, forced colors,
no-JavaScript behavior, lazy content, and browser errors.

| Lighthouse category or metric | Mobile | Desktop |
| --- | ---: | ---: |
| Performance | 98 | 100 |
| Accessibility | 100 | 100 |
| Best Practices | 100 | 96 |
| SEO | 92 | 92 |
| First Contentful Paint | 930.2 ms | 262.6 ms |
| Largest Contentful Paint | 2455.2 ms | 510.6 ms |
| Cumulative Layout Shift | 0 | 0.0001277 |
| Total Blocking Time | 0 ms | 0 ms |

The owner approved the SEO exception. The only failed SEO audit is
Lighthouse's internal attempt to fetch `robots.txt` while the page's strict
`connect-src 'none'` directive is active. The public robots file, sitemap,
canonical metadata, and indexing directives independently pass. The CSP was
not weakened merely to raise the synthetic score.

## Backup and rollback

| Checkpoint | Purpose | Result |
| --- | --- | --- |
| `85b7382c` | Pre-activation state | Restic check and four streamed restores passed |
| `11ad18e9` | Post-Automatic-SSL state | Restic check and four streamed restores passed |
| `1f28541a` | Final production state | Restic check and five streamed restores passed |

The immediate previous release is retained at:

```text
/home/jamesjen/.player-previous-m16b-20260723T040350Z
```

Rollback removes only the Player Cloudflare record, atomically restores the
retained release or the required state from snapshot `1f28541a`, uses Webuzo's
supported certificate workflow only if certificate restoration is necessary,
and then repeats Player, master, `www`, status, QuireForge, mail, and Pages
checks. No shared service restart or generated virtual-host edit is part of
rollback.

## Isolation evidence

- The Player record was the zone's only DNS addition.
- Mail-related DNS JSON was identical before and after activation.
- No `www.player`, `mail.player`, AAAA, CNAME, or wildcard record exists.
- Apex, `www`, status, QuireForge, webmail, autoconfig, autodiscover, and the
  organizational GitHub Pages site retained their recorded behavior.
- The Player access-log review found no 5xx response.
- No `.git`, `.env`, credential, private key, source map, internal log,
  dependency tree, or private repository content is in the artifact.

## Remaining approval gates

- Push this local branch or open a pull request.
- Add Player to the live master-site project registry and navigation.
- Add Player to public status monitoring, if desired.
- Update the Play Console privacy URL when the app is ready for testing.
- Enable the guarded GitHub Pages transition and later retire Pages.
- Approve any future DNS, Cloudflare, SSL, Webuzo, HSTS, redirect, general
  cache, analytics, or process change.
