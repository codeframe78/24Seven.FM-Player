# Milestone ID migration ledger

The roadmap was renumbered on July 18, 2026 after a complete evidence, implementation, security, Google Play, release,
and operations audit. Canonical IDs now follow dependency order and use zero padding below M10. Git commits and prior
published evidence retain their historical text; this ledger is the permanent translation layer.

## Completed milestone mapping

| Legacy ID | Canonical ID | Milestone |
| --- | --- | --- |
| M1 | M01 | Buildable baseline |
| M2 | M02 | Five-station playback |
| M3 | M03 | Background and system media |
| M4 | M04 | Now Playing |
| M5 | M05 | Native navigation |
| M6 | M06 | Queue and History |
| M7 | M07 | Authentication |
| M8 | M08 | Chat |
| M9 | M09 | Song requests |
| M10–M16 | M10–M16 | IDs unchanged |
| M18 | M17 | StreamingSoundtracks.com certification |
| M19 | M18 | 1980s.FM certification |
| M20 | M19 | Adagio.FM certification |
| M21 | M20 | Death.FM certification |
| M22 | M21 | Entranced.FM certification |
| M23.3 | M22 | Android 16 and API 36 readiness |
| M23.7 | M23 | Adaptive launcher and store polish |
| M24–M26 | M24–M26 | IDs unchanged |
| M27.1 | M27 | Local Chat-Mention Notifications |

## Active, planned, and deferred mapping

| Legacy ID/scope | Canonical ID | Milestone |
| --- | --- | --- |
| M23.2 | M28 | UGC Safety and Ongoing Moderation |
| M23.4 | M29 | Play Declarations, Privacy, and Data Safety |
| M23.5 | M30 | Brand, Content, and Distribution Rights |
| Audit addition | M31 | Payments, External Links, and Account Lifecycle Compliance |
| Audit addition | M32 | Session, Controller, Network, and Supply-Chain Security |
| Audit addition | M33 | Request Transaction Integrity |
| M23.6 | M34 | Device and Accessibility Acceptance |
| M23.1 | M35 | Release Signing and Console Eligibility |
| M27.2 authorization slice | M36 | Notification Event-Source Authorization |
| M27.2 implementation slice | M37 | Secure Closed-App Notification Delivery |
| M27.2 validation slice | M38 | Notification Lifecycle and Privacy Certification |
| M28 final-candidate slice | M39 | Alpha Candidate and Documentation Freeze |
| M28 Play-delivery slice | M40 | Play Alpha Delivery and Pre-launch Remediation |
| M28 publication slice | M41 | Explicit Alpha Publication |
| Audit addition | M42 | Closed-Test Operations and Stabilization |
| Audit addition | M43 | Production Access and Policy Approval |
| Audit addition | M44 | Production Release and Staged Rollout |
| Audit addition | M45 | Operational Reliability and Recertification |
| Audit addition | M46 | Architecture Sustainability |
| M17 repair gate | M47 | Private Messages Server Repair and Protocol Certification |
| M17 read slice | M48 | Native Private Message Reading |
| M17 mutation slice | M49 | Private Message Compose, Reply, and Send |
| M17 certification/notification slice | M50 | Private Message Five-Station and Notification Certification |
| Forum F1 | M51 | Verified Forum Links |
| Forum F2 | M52 | Native Forum Read-Only Foundation |
| Forum F3 | M53 | Authenticated Forum Participation |
| Forum F4 | M54 | Forum Five-Station and Notification Certification |
| M25 gated Cast boundary | M55 | Google Cast Feasibility and Certification |
| Future station research | M56 | Extended Station Capability Certification |
| Future account routes | M57 | Account Registration, Recovery, and Management Access |

## Traceability rules

- New issues, commits, documentation, tester reports, and Discord updates use canonical IDs only.
- Historical commit messages are not rewritten.
- When an old document or external link cannot be renamed, its title must state the canonical ID and may note the legacy
  ID once.
- The GitHub Project `Roadmap ID` field stores only canonical IDs; item bodies may include `Legacy ID:`.
- Product Test IDs (`PT-01`, `PT-02`, and so on) are stable evidence identifiers and are not renumbered with milestones.
