# Dedicated-domain project-site validation

Validated in production July 22, 2026. Owner-approved activation is complete.
This record does not authorize any later infrastructure or repository change.

## Outcome

The existing Jekyll portal was retained, refined, and deployed as a static site
at `https://player.jamesjennison.net`. It uses clean routes, James-Jennison
identity, curated public content, a GitHub-only privacy contact, a custom 404,
six security headers, and an HTML-only `no-transform` cache directive.

The live artifact was built from local branch `codex/player-site-migration` at
source checkpoint `2cb59c8`. The owner's separate main checkout was not used.
The checkpoint and completion record remain local until push approval.

## Reproducible validation

Run from the repository root:

```bash
./scripts/validate-project-site.sh
python3 -m http.server 4173 --directory _site
# In a second terminal:
node ./scripts/test-project-site-browser.mjs http://127.0.0.1:4173
node ./scripts/test-project-site-firefox.mjs http://127.0.0.1:4173
```

The static build uses the digest-pinned image
`ghcr.io/actions/jekyll-build-pages@sha256:6791ebfd912185ed59bfb5fb102664fa872496b79f87ff8b9cfba292a7345041`.

The production `_site/` artifact contains 25 files totaling 3,587,618 bytes,
including nine HTML documents. Its deterministic inventory digest is:

```text
9ba4d8e103f23201d5705ce7bc300d0c3247f585cc2be9674cd068f3d8863396
```

The digest was calculated by sorting each relative path and content SHA-256
into a second SHA-256. Local, promoted, and live-origin inventories matched.

## Automated results

| Validation | Result |
| --- | --- |
| Jekyll production build | Pass |
| Dedicated-domain artifact validator | Pass: 25 files, 9 HTML documents |
| GitHub Pages transition validator | Pass: 8 `noindex` compatibility pages |
| Internal routes, fragments, and assets | Pass |
| Canonical, Open Graph, social, sitemap, manifest, robots, and structured data | Pass |
| JavaScript syntax, including both browser suites | Pass |
| Shell script static analysis and workflow YAML parsing | Pass |
| Sensitive-file, symlink, credential-marker, source-map, and private-content boundary | Pass |
| Artifact-local response contract | Pass: six security headers plus HTML `no-transform`; HSTS intentionally absent |
| Chromium production suite | Pass: 320, 390, 768, 1440, and 1920 pixel viewports |
| Firefox production suite | Pass: 500, 768, 1024, and 1440 pixel viewports |
| `git diff --check` | Pass |

Chromium exercised all nine routes at both mobile widths and key routes at the
three larger widths. Firefox exercised all nine routes at its practical
500-pixel headless minimum and key routes at larger widths. The suites verified
one primary heading per route, navigation, overflow, images, themes, pointer
and keyboard interaction, local-only test progress, reduced motion, forced
colors, no-JavaScript fallback, lazy-image loading, and absence of browser
errors.

## Production Lighthouse

| Category or metric | Mobile | Desktop |
| --- | ---: | ---: |
| Performance | 98 | 100 |
| Accessibility | 100 | 100 |
| Best Practices | 100 | 96 |
| SEO | 92 | 92 |
| First Contentful Paint | 930.2 ms | 262.6 ms |
| Largest Contentful Paint | 2455.2 ms | 510.6 ms |
| Cumulative Layout Shift | 0 | 0.0001277 |
| Total Blocking Time | 0 ms | 0 ms |

Performance, Accessibility, and Best Practices meet the approved targets. The
owner approved the SEO measurement exception. Lighthouse's only failed SEO
audit is its internal `robots.txt` request being denied by the deliberate
`connect-src 'none'` CSP. Public retrieval of `robots.txt` and the sitemap
passes; canonical URLs and indexing directives are valid. The stricter CSP is
retained instead of weakening it to improve a synthetic score.

## Production infrastructure validation

| Check | Result |
| --- | --- |
| Webuzo domain | Pass: isolated subdomain owned by `jamesjen` |
| Document root | Pass: `/home/jamesjen/player.jamesjennison.net`; no overlap with another site |
| Server architecture | Pass: Webuzo 4.7.4 and Apache 2.4.68; syntax valid; Webuzo and MariaDB active |
| Runtime isolation | Pass: static files only; no application process, port, reverse proxy, database, or new scheduled task |
| DNS | Pass: exactly one proxied Player A record, TTL Auto; no public alias, AAAA, CNAME, or wildcard |
| Cloudflare TLS | Pass: Full (Strict), valid edge certificate, no 526 |
| Origin TLS | Pass: dedicated Let's Encrypt SAN only for `player.jamesjennison.net` |
| Certificate renewal | Pass: next renewal registered for September 20, 2026; daily Webuzo renewal job retained |
| Canonical routes | Pass: eight content routes return 200 |
| Custom 404 | Pass: unknown paths retain status 404 with the Player error page |
| HTTP redirect | Pass: HTTPS destination preserves path and query string |
| Sensitive paths | Pass: `.git`, `.env`, keys, source documentation, and `.htaccess` are not publicly readable |
| Security and cache headers | Pass on successful HTML and custom 404 responses; no edge-injected script |
| Backups | Pass: `85b7382c`, `11ad18e9`, and `1f28541a` passed Restic checks and streamed restores |
| Service isolation | Pass: master, `www`, status, QuireForge, webmail, mail DNS, and organizational Pages behavior retained |
| Production logs | Pass: no Player 5xx response in the reviewed access-log window |

Webuzo's generated `www.player` and `mail.player` aliases remain non-resolving
and are not approved public hostnames. No generated virtual-host file was
edited and no shared service was restarted.

The immediate prior release remains at
`/home/jamesjen/.player-previous-m16b-20260723T040350Z`. Production snapshot
`1f28541a` preserves the final document root, Webuzo and Apache state,
certificate state, and database export; five streamed restore comparisons
passed.

## Public-content and transition boundary

- The canonical privacy notice is published at `/privacy/`.
- Public contact remains limited to the approved GitHub issue route.
- The historical `codeframe78.github.io` identity is absent and retired.
- No private repository material, credential, environment file, key, log,
  source map, dependency tree, or Git metadata is deployed.
- The organizational GitHub Pages site remains unchanged and available.
- `PLAYER_PAGES_TRANSITION_APPROVED` remains absent.
- The Play Console privacy URL remains an owner-timed later change.

## Remaining approval gates

- Push the local branch or open a pull request
- Add Player to the live master-site registry and navigation
- Add Player to public status monitoring, if desired
- Update Play Console when the app is ready for testing submission
- Activate and eventually retire the guarded GitHub Pages transition
- Make any later DNS, Cloudflare, SSL, Webuzo, HSTS, redirect, or general cache change
