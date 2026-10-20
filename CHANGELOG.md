## 1.3.1

- Fixed crash (`NullPointerException` in `ShizukuRemoteProcess.destroy()`) when a command finishes or the remote binder disconnects before process destruction.
- Hardened background command execution in `ShizukuApiPlugin` against uncaught worker thread exceptions.

## 1.3.0

- `requestPermission()` no longer stacks permission dialogs: calls made while the dialog is open share its answer. A request older than 2 minutes with no answer is retried.
- Fixed crash ("Reply already submitted") when `Shizuku.requestPermission()` threw after the call was queued; every reply is now sent at most once.
- `checkPermission()` and `requestPermission()` return false when Shizuku isn't running, instead of throwing a `PlatformException`. Pending permission requests return false if Shizuku stops while the dialog is open.
- `runCommand()` runs on a background thread instead of blocking the UI thread (ANR risk). Several commands can run at once; a new command no longer kills the one already running. Running commands are stopped when the engine detaches.
- Fixed `runCommand()` hanging forever when a command wrote a lot to stderr.
- New: `shouldShowRequestPermissionRationale()`, `isPreV11()`, `getVersion()`, `getServerPatchVersion()`, `getLatestServiceVersion()`, `getUid()`, `getSELinuxContext()`, `isShizukuInstalled()`, `openShizukuApp()`, and a `binderStatus` stream that reports when Shizuku starts or stops.
- The plugin manifest declares a `<queries>` entry for the Shizuku package so `isShizukuInstalled()` and `openShizukuApp()` work on Android 11+.

## 1.2.4

- Fixed crash on Android 7.x after answering the permission dialog (`UnsupportedOperationException` from `Shizuku.removeRequestPermissionResultListener`). The plugin now registers a single permission listener once and never removes it.
- Fixed Android build failing with "Inconsistent JVM Target Compatibility" (Java 11 vs Kotlin 21) on AGP 8, or on AGP 9 with `android.builtInKotlin=false`: `kotlin-android` is applied again when AGP's built-in Kotlin isn't active, and the Kotlin JVM target is set to 11 directly on the compile tasks.

## 1.2.3

- Improved README documentation structure and formatting


- gradle updates

## 1.2.1

- fixed requestPermission() giving result without listening to user action

## 1.2.0

- separated requesting permission into checkPermission(), requestPermission()

## 1.1.0

- converted List<String> output to String

## 1.0.1

- added check for Shizuku Binder attached

## 1.0.0

- can run basic ADB commands 

## 0.0.2

- docs update

## 0.0.1

- initial commit
- shizuku api working