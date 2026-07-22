# Dedicated-domain project-site validation

Validated July 22, 2026. Production deployment is not authorized by this
record.

## Outcome

The existing Jekyll portal was retained, refined, and prepared as a static
artifact for `https://player.jamesjennison.net`. The implementation uses the
approved clean route model, James-Jennison identity, curated public content,
and GitHub-only privacy contact. It makes no Webuzo, DNS, Cloudflare, SSL,
GitHub setting, GitHub Pages, staging, or production change.

Source was isolated on local branch `codex/player-site-migration` from commit
`c54057151f34e5505f238391d08a5520d8e45c06`. The owner's separate dirty main
checkout was not used for implementation.

## Reproducible validation

Run from the repository root:

```bash
./scripts/validate-project-site.sh
node ./scripts/test-project-site-browser.mjs
```

The static build uses the digest-pinned image
`ghcr.io/actions/jekyll-build-pages@sha256:6791ebfd912185ed59bfb5fb102664fa872496b79f87ff8b9cfba292a7345041`.

The final `_site/` artifact contains 24 files totaling approximately 3.42 MiB,
including nine HTML documents. Its deterministic inventory digest, calculated
by sorting each relative file path and content SHA-256 into a second SHA-256,
is:

```text
db2d0047cc43168c1d5483df4afc88d0a016da34c81136bb76a27e06ab7e7a7b
```

The digest is a local validation checkpoint, not a signed release provenance
record. It must be recalculated from the approved deployment commit before
staging or production.

## Automated results

| Validation | Result |
| --- | --- |
| Jekyll production build | Pass |
| Dedicated-domain artifact validator | Pass: 24 files, 9 HTML documents |
| GitHub Pages transition validator | Pass: 8 `noindex` compatibility pages |
| Internal routes, fragments, and assets | Pass |
| Canonical, Open Graph, social, sitemap, manifest, robots, and structured data | Pass |
| JavaScript syntax | Pass |
| Shell script static analysis | Pass |
| Workflow YAML parsing | Pass |
| Sensitive-file, symlink, credential-marker, source-map, and private-content boundary | Pass |
| `git diff --check` | Pass |

The browser suite exercised all nine routes at 390 by 844 pixels and key
routes at 768 by 1024, 1440 by 1000, and 1920 by 1080. It verified one primary
heading per route, canonical metadata, navigation, overflow, images, themes,
the mobile menu, site explorer, screenshot lightbox, local-only test progress,
resource and privacy search, reduced motion, forced colors, no-JavaScript
fallback, and absence of browser errors.

## Lighthouse

The final mobile homepage audit reported:

| Category or metric | Result |
| --- | ---: |
| Performance | 99 |
| Accessibility | 100 |
| Best Practices | 100 |
| SEO | 100 |
| First Contentful Paint | 1.35 s |
| Largest Contentful Paint | 1.80 s |
| Cumulative Layout Shift | 0 |
| Total Blocking Time | 0 ms |

An immediately preceding mobile run scored 100 in all four categories, and the
desktop homepage audit also scored 100 in all four categories. Privacy, Product
Testing, Roadmap, and Resources each scored 100 for accessibility, best
practices, and SEO in route-level audits.

Lighthouse results are controlled local measurements, not guarantees of
production field performance. Webuzo, Cloudflare, network, cache, and origin
behavior require separate staging and production validation.

## Public-content review

- The canonical privacy notice remains generated from `PRIVACY.md` and is
  published at `/privacy/`.
- The public privacy contact is limited to the approved GitHub issue route.
- The old `codeframe78` site identity and repository destinations are absent
  from the deployment artifact.
- Internal planning details and speculative delivery forecasts are absent
  from the rendered site.
- The resource library is narrowed to 20 durable, public-facing references.
- Current-development and roadmap statements carry dated verification context.
- No `.git`, `.env`, credential, private key, development dependency, log,
  source map, internal configuration, or repository source document is in the
  artifact.

## Deployment safety

The production artifact is `_site/`; source and dependencies must remain
outside every public document root. The Project Site workflow validates both
the dedicated-domain artifact and a temporary GitHub Pages transition, but its
deploy job cannot run unless the separately approval-gated repository variable
`PLAYER_PAGES_TRANSITION_APPROVED` equals `true`.

Webuzo remains authoritative for the exact user, document root, ownership,
permissions, active server chain, certificate, logs, and backup. Those values
are deliberately unresolved until read-only Webuzo discovery and an exact
subdomain proposal are approved. The complete deployment, health, transition,
and rollback contract is in
[project-site-migration.md](project-site-migration.md).

## Remaining gates

- Review and approval of this local commit
- GitHub push or pull request
- Read-only Webuzo, DNS, Cloudflare, SSL, backup, and existing-content discovery
- Exact Webuzo user and isolated document root
- Subdomain creation and SSL/DNS changes
- Staging deployment and validation
- Production deployment and validation
- Play Console privacy URL change
- GitHub Pages transition activation and eventual retirement

None of these actions occurred during this milestone.
