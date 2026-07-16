# M23.6 network-loss and playback-reconnection validation

Validated July 16, 2026 for the `0.1.0-alpha01` candidate.

## Outcome

Playback now recovers from a real device-network outage without another user tap. After Media3 exhausts its bounded
load handling and the station fallback, the Player presents `No network · playback will resume automatically`. When
Android reports a validated default network again, the failed item is prepared exactly once and playback resumes.

This is not a general retry loop. An online station/server failure remains an explicit error, and Pause, Stop, or a
station change cancels pending recovery.

## Implementation boundaries

- The application observes Android's default network through `ConnectivityManager`; it does not request Wi-Fi,
  cellular data, an unmetered transport, or background work.
- A pure recovery coordinator separates device-network state from Media3 operations and consumes each pending retry
  once.
- The Media3 controller retains the existing primary/fallback playlist. It calls `prepare()` only after an offline
  terminal failure and subsequent validated-network restoration.
- `WaitingForNetwork` is immutable playback UI state with an accessible status description and a Pause action so the
  user can cancel automatic recovery.
- `ACCESS_NETWORK_STATE` matches the network-state purpose already disclosed in the privacy notice.

Android documents that failed playback can be retried with `prepare()` in [Player events](https://developer.android.com/media/media3/exoplayer/listening-to-player-events),
and recommends a default `NetworkCallback` instead of connectivity polling in [Read network state](https://developer.android.com/develop/connectivity/network-ops/reading-network-state).

## Live emulator evidence

| Device | Evidence | Result |
| --- | --- | --- |
| API 35 phone | Confirmed `Playing live`; disabled the active interface and confirmed ping failure; observed buffering, fallback, and terminal waiting; restored the interface | Returned to `Playing live` without another play action. `m23-network-offline.png` preserves the compact waiting state. |
| API 35 Pixel Tablet | Confirmed `Playing live`; disabled both interfaces and confirmed no route; observed terminal waiting; restored the simulated mobile network | Returned to `Playing live` without another play action. `m23-network-recovered-tablet.png` preserves the expanded recovered state. |

## Automated verification

- 130/130 debug unit tests passed, including three focused recovery-policy cases.
- The focused connected waiting-state/Pause semantics test passed on the API 35 phone.
- 40/40 connected tests passed on the API 35 Pixel Tablet at restored normal settings.
- Debug lint passed.

The first complete 40-test attempt on the compact phone produced 39 passes and one expected device-profile mismatch:
the dedicated 701dp medium-navigation test cannot be displayed inside a 411dp phone root. The same unchanged test and
the complete suite passed on the Tablet profile; no assertion was weakened or skipped.
