# Comprehensive project portal validation

Validated July 18, 2026.

## Outcome

The GitHub Pages surface now provides a comprehensive, responsive project portal while preserving the canonical privacy notice at the Pages root. Its shared visual system includes light and dark themes, a compact mobile menu, scroll progress, active section navigation, copyable section links, reduced-motion support, an image lightbox, and a back-to-top control. The portal includes:

- a product overview and detailed feature/station boundaries;
- native architecture, source organization, engineering principles, and the milestone workflow;
- automated, emulator, physical-device, accessibility, network, performance, signing, and CI evidence;
- a tester-facing product workspace with bounded smoke, member, device, resilience, and adaptive sessions plus a structured public result form for passes, failures, notes, and blocked tests;
- all 27 achieved milestones in verified chronological order, including completed M24 Sleep Timer, M25 Audio-Output Selection, M26 In-App Diagnostics, and the M27.1 local Chat-mention foundation;
- the seven M23 Play-testing workstreams, deferred M17 boundary, completed M24–M26/M27.1, remaining M27.2 true-push gate, and M28 Alpha publication milestone;
- a public resource index for architecture, protocol research, station certification, Play readiness, release notes, testing, and contribution.

Repeated navigation and milestone history are generated from `privacy-site/_data/project.yml`. The existing `PRIVACY.md` generation and `/` permalink are unchanged.

## Verification

- Official `ghcr.io/actions/jekyll-build-pages:v1.0.13` container: pass.
- Eight generated HTML pages, including the root privacy notice: pass.
- Internal page, asset, and fragment link contract: pass.
- Liquid rendering and one active navigation destination per project page: pass.
- JavaScript syntax and progressive-enhancement contract: pass.
- Workflow/data YAML parsing and 27-entry chronological milestone contract: pass.
- Chromium review at 1440 px and 390 px with no horizontal overflow: pass.
- Theme selection and persistence, mobile navigation, roadmap search/status filters, and screenshot lightbox interactions: pass.
- Eager and viewport-triggered media loading with no browser-console errors: pass.
- Product-testing issue-form YAML with 17 blocks and 16 unique field IDs: pass.
- Roadmap review through M17 and the reordered M24–M28 sequence: pass.
- Every requested local stylesheet, icon, and screenshot returned HTTP 200 with no missing assets.
- `git diff --check`: pass.

This is a static documentation change. It does not alter Android application behavior, station traffic, permissions, signing material, or the release candidate.

## Visual evidence

<table>
  <tr>
    <td width="50%" align="center" valign="top"><img src="screenshots/project-portal-overview.png" alt="Responsive project portal overview with shared navigation, current program status, and native Android hero" width="560"><br><strong>Project overview</strong><br><sub>Current status, project entry points, station scope, and interactive screenshot gallery.</sub></td>
    <td width="50%" align="center" valign="top"><img src="screenshots/project-portal-development.png" alt="Responsive development process page describing evidence-backed vertical slices and native MVVM architecture" width="560"><br><strong>Development process</strong><br><sub>Architecture, principles, milestone lifecycle, source map, and toolchain.</sub></td>
  </tr>
  <tr>
    <td colspan="2" align="center" valign="top"><img src="screenshots/project-portal-roadmap.png" alt="Interactive roadmap page with program progress, searchable workstreams, status filters, and evidence links" width="760"><br><strong>Pre-Alpha roadmap</strong><br><sub>Search and status controls make the seven release gates, achieved history, and future milestones easier to inspect.</sub></td>
  </tr>
  <tr>
    <td colspan="2" align="center" valign="top"><img src="screenshots/project-portal-product-testing.png" alt="Product Testing workspace with preparation guidance, acceptance cases, sticky section navigation, and evidence links" width="760"><br><strong>Product testing workspace</strong><br><sub>Scannable acceptance cases and reproducible evidence before the structured public GitHub result form.</sub></td>
  </tr>
</table>
