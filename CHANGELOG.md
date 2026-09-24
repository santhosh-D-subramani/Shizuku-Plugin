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