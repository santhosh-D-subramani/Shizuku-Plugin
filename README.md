# Shizuku API Flutter Plugin

A Flutter plugin to interact with the Shizuku API, allowing your application to execute shell commands with system or ADB privileges.

## About
This plugin is used in [System App Remover](https://play.google.com/store/apps/details?id=com.santhoshDsubramani.systemappremover), a Play Store application for removing system apps (bloatware) without requiring root access or a computer (Android 10 and below still require a computer to start Shizuku).

## Installation
Add the dependency to your project:

```bash
flutter pub add shizuku_api
```

## Requirements
* The [Shizuku](https://shizuku.rikka.app/) app must be installed and running on the target device.

## Configuration

### app/build.gradle
Ensure that the minimum SDK version (`minSdk`) is set to `24` or higher.

### AndroidManifest.xml
Add the Shizuku provider definition inside the `<application>` tag:

```xml
<application>
    <provider
        android:name="rikka.shizuku.ShizukuProvider"
        android:authorities="${applicationId}.shizuku"
        android:multiprocess="false"
        android:enabled="true"
        android:exported="true"
        android:permission="android.permission.INTERACT_ACROSS_USERS_FULL" />
</application>
```

## Usage

### 1. Verify Shizuku Service
Before executing any plugin commands, verify that the Shizuku service is running:

```dart
// Check if the Shizuku binder service is active
bool isBinderRunning = await _shizukuApiPlugin.pingBinder() ?? false;
```

### 2. Check Permissions
Check if Shizuku permissions have been granted to your application:

```dart
final _shizukuApiPlugin = ShizukuApi();

// Returns true if permission is granted, false if denied or not requested yet
bool hasPermission = await _shizukuApiPlugin.checkPermission();
print(hasPermission);
```

### 3. Request Permissions
Request permissions from the user via the Shizuku system dialog:

```dart
final _shizukuApiPlugin = ShizukuApi();

// Triggers the Shizuku permission dialog
// Returns true if permission is granted, false if declined
bool permissionGranted = await _shizukuApiPlugin.requestPermission();
print(permissionGranted);
```

### 4. Run Commands
Execute ADB shell commands:

* **Note:** Execution within a root environment (`su`) is untested.
* Standard ADB shell commands are supported.

```dart
String command = 'pm uninstall --user 0 com.android.chrome';
// Returns success if command executed and system app is uninstalled, or failure
await _shizukuApiPlugin.runCommand(command);
```
