# M3 background playback validation

Validation performed on July 13, 2026.

M3 is in progress. The behavioral checks below passed without requiring a production-code change.

## Motorola Razr 2023

Playback was started on Entranced.FM and verified through each lifecycle transition:

- pressing Home preserved `PLAYING` state, matching session metadata, the application process, the foreground `RadioPlaybackService`, and the media notification;
- locking the device and entering idle preserved playback and the foreground service;
- removing the application task from Recents removed the task while playback, the process, service, session, and notification continued;
- system pause, play, and headset-hook commands updated the MediaSession between `PAUSED` and `PLAYING` as expected;
- transient audio-focus loss paused playback and automatically resumed it after focus returned;
- permanent audio-focus loss paused playback and left it paused after the competing owner abandoned focus, requiring an explicit user resume.

The audio-focus checks used a temporary local helper application compiled against Android API 35. The helper was uninstalled and its source and build output were removed after validation.

## API 35 emulator

A rooted local API 35 emulator was used to send Android's protected `AUDIO_BECOMING_NOISY` broadcast. Active 1980s.FM playback transitioned from `PLAYING` to `PAUSED`, confirming Media3's noisy-output handling. Playback was stopped after the check.

## Remaining M3 work

- Exercise controls from real Bluetooth and wired-headset hardware.
- Disconnect a real Bluetooth or wired audio route during playback and confirm the same pause policy.
- Add focused automated coverage for service lifecycle behavior where practical.

Do not mark M3 complete until the remaining hardware and lifecycle coverage is either completed or explicitly descoped.
