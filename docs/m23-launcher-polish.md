# M23.7 adaptive launcher and store polish

Date: July 15, 2026

Status: complete

## Delivered

- Preserved the established 24Seven.FM artwork as the launcher identity without generating replacement branding.
- Added density-specific legacy icons for mdpi through xxxhdpi so pre-adaptive launchers no longer load the 1,254-pixel in-app fallback artwork directly.
- Added an adaptive icon with the existing artwork inset inside the platform safe zone over the established deep-purple background.
- Added an Android 13 monochrome radio-wave layer for launchers that enable themed icons.
- Pointed both `android:icon` and `android:roundIcon` at the launcher resource while retaining `app_logo` as the in-app artwork/error fallback.
- Revalidated the existing 512×512 Play icon, feature graphic, and privacy-reviewed Player/Queue/device screenshots. No in-app recapture was needed because this milestone changes launcher presentation only.

## Verification

- Debug resource merge and APK assembly: passed.
- Debug Android-test Kotlin compilation: passed.
- Debug lint: passed.
- APK manifest inspection: application icon and round icon both resolve to `@mipmap/ic_launcher`.
- APK resource inspection: adaptive API 26 resource, API 33 monochrome resource, foreground/monochrome drawables, and all five legacy density PNGs are packaged.
- API 35 Pixel Tablet resource test: adaptive icon loaded and its monochrome layer was non-null.
- API 35 Pixel Tablet launcher-drawer inspection: circular platform mask retained the radio mark and readable 24Seven.FM identity without edge clipping.

No signing material, store credentials, or new third-party artwork was introduced.
