# M4 metadata research

Research performed on July 13, 2026 against the ten stream URLs already verified and committed from station-provided PLS files. No endpoint was guessed or added.

Each relay was requested with `Icy-MetaData: 1`. The servers use a legacy ICY response, so headers and the first metadata block were decoded from a short-lived byte sample. Temporary samples were deleted immediately after extraction.

| Station | Relay | Content type | Advertised bitrate | Metadata interval | Non-empty title |
| --- | --- | --- | ---: | ---: | --- |
| StreamingSoundtracks.com | Primary | `audio/aacp` | 128 kbps | 32,768 bytes | Yes |
| StreamingSoundtracks.com | Source | `audio/aacp` | 128 kbps | 8,192 bytes | Yes |
| 1980s.FM | Primary | `audio/aacp` | 128 kbps | 32,768 bytes | Yes |
| 1980s.FM | Source | `audio/aacp` | 128 kbps | 8,192 bytes | Yes |
| Adagio.FM | Primary | `audio/aacp` | 128 kbps | 32,768 bytes | Yes |
| Adagio.FM | Source | `audio/aacp` | 128 kbps | 8,192 bytes | Yes |
| Death.FM | Primary | `audio/aacp` | 128 kbps | 32,768 bytes | Yes |
| Death.FM | Source | `audio/aacp` | 128 kbps | 8,192 bytes | Yes |
| Entranced.FM | Primary | `audio/aacp` | 128 kbps | 32,768 bytes | Yes |
| Entranced.FM | Source | `audio/aacp` | 128 kbps | 8,192 bytes | Yes |

The sampled primary and source title matched for every station. This is a point-in-time protocol check, not a guarantee that relays will always be synchronized.

## Field constraints

The ICY `StreamTitle` is a single composite string. Samples resembled performer, album, title, and duration separated by punctuation, but ICY supplies no trustworthy field boundaries. The application must display the raw title and must not split it heuristically into artist, album, composer, or duration.

Media3 1.10.1 exposes the metadata through `Player.Listener.onMetadata` and `IcyInfo.title`. `IcyInfo.url` is not used because no semantics or permitted artwork behavior have been verified for it.

The response content type verifies AAC-family audio and the catalog records `StreamFormat.Aac`. The 128 kbps value is server-advertised ICY evidence. Artwork and separately structured track, album, artist, and composer fields remain unverified.

## Initial implementation validation

The service-owned player publishes non-empty `IcyInfo.title` values into immutable, station-scoped domain state. The Compose Now Playing screen displays the raw title and verified `AAC • 128 kbps` quality. The same title is explicitly merged into MediaSession metadata while the station identity remains the artist/album context.

The debug and release unit tests, Android lint, debug APK, and instrumentation APK passed. The API 35 MediaSession service stop/reconnect test also passed after the metadata integration was added.

On the Motorola Razr 2023, all five stations displayed a fresh title after selection, replaced the previous station's title, remained in `PLAYING`, and completed an isolated rerun with no playback or fatal application errors. A separate check confirmed that the Compose title and Android MediaSession title matched. Playback was stopped after each validation run.

A transient malformed AAC packet occurred during an earlier rapid all-station pass; the existing fallback policy recovered to `PLAYING`, and the error did not reproduce in the isolated rerun. This is recorded as transient stream behavior rather than hidden.

Primary-to-source fallback was verified under a controlled primary-only network failure on a rooted local API 35 emulator. The source item reached `PLAYING`, published a non-empty ICY title through MediaSession, and recorded the expected primary-item failure. Both verified production URLs remained unchanged, and no temporary firewall rule remained after cleanup.

A continuous-playback check on the Motorola Razr 2023 observed one station until its raw title changed. The updated Compose title and the application MediaSession title matched, both remained in `PLAYING`, the session reported no error, and log inspection found no player or fatal application error. The observation allowed up to ten minutes rather than assuming a short track boundary; the verified transition occurred after 40 seconds in this run. The active speaker route was read back at 0/15 before playback, and the original unmuted 7/15 volume plus the original screen timeout were restored afterward.

These results complete M4. Artwork and separately structured artist, album, composer, and duration remain intentionally unavailable because the verified source supplies only one composite ICY title. Playback continues independently when metadata is absent.

## Current-track artwork addendum (2026-07-14)

The public native station player was later verified to read `ASIN` and `CoverLink` from the station-owned `soap/FM24sevenJSON.php?action=GetCurrentlyPlaying` response. The same contract exists across all five station domains. StreamingSoundtracks.com, 1980s.FM, Adagio.FM, and Entranced.FM returned a live same-station cover during verification; Death.FM returned no current artwork at that observation time.

The Android app now makes one bounded, unauthenticated current-track read when a distinct ICY title arrives. It validates HTTPS and the selected station host, follows the player site's explicit 500-pixel ASIN cover convention, and publishes optional artwork to immutable Compose state and MediaSession metadata. It does not add a timer, infer track fields, or make playback depend on this response. Missing or failed artwork remains a valid title-only state.

On the API 35 Motorola Razr 2023, a live 1980s.FM track displayed its station-hosted album cover on both the main Player and persistent mini-player while the title matched across both surfaces. Playback was stopped after validation. The full unit suite, Android lint, release assembly, and nine connected Compose/service tests passed.

Death.FM exposed a slower-response race during physical-device validation: MediaSession's metadata-only replacement emitted a transition before artwork enrichment completed, so the pending cover was discarded even though the public response and image were valid. The service now distinguishes an unchanged media ID from a real station or relay transition. Metadata-only replacements preserve pending artwork; station switches and primary-to-source fallback still clear stale state.

The same foldable-device pass placed the Razr in a short, wide layout and showed that the larger Player artwork could move controls outside the viewport. Player content is now vertically scrollable, with connected coverage proving the Stop control remains reachable in a constrained wide layout.
