import 'shizuku_api_platform_interface.dart';

class ShizukuApi {
  /// To request shizuku access use below code
  /// * Shizuku must be installed on the device and running
  /// ```dart
  ///  final shizukuApiPlugin = ShizukuApi();
  /// await shizukuApiPlugin.requestPermission()
  /// ```
  ///
  /// Calling this again while the permission dialog is still open doesn't show
  /// a second dialog; every call gets the answer of the one being shown.
  /// Returns false if Shizuku isn't running.
  Future<bool?> requestPermission() {
    return ShizukuApiPlatform.instance.requestPermission();
  }

  Future<bool?> pingBinder() {
    return ShizukuApiPlatform.instance.pingBinder();
  }

  /// Returns false if Shizuku isn't running.
  Future<bool?> checkPermission() {
    return ShizukuApiPlatform.instance.checkPermission();
  }

  Future<String?> runCommand(String command) {
    return ShizukuApiPlatform.instance.runCommand(command);
  }

  /// True if the user denied the permission and chose "Don't ask again".
  /// [requestPermission] won't show the dialog again; send the user to the
  /// Shizuku app ([openShizukuApp]) to grant it there.
  Future<bool?> shouldShowRequestPermissionRationale() {
    return ShizukuApiPlatform.instance.shouldShowRequestPermissionRationale();
  }

  /// True if the running Shizuku server is older than v11, which this plugin
  /// doesn't support.
  Future<bool?> isPreV11() {
    return ShizukuApiPlatform.instance.isPreV11();
  }

  /// API version of the running Shizuku server, or null if it isn't running.
  Future<int?> getVersion() {
    return ShizukuApiPlatform.instance.getVersion();
  }

  /// Patch version of the running Shizuku server, or null if it isn't running.
  Future<int?> getServerPatchVersion() {
    return ShizukuApiPlatform.instance.getServerPatchVersion();
  }

  /// Latest Shizuku server API version this plugin was built against.
  /// Compare with [getVersion] to tell the user to update Shizuku.
  Future<int?> getLatestServiceVersion() {
    return ShizukuApiPlatform.instance.getLatestServiceVersion();
  }

  /// UID the Shizuku server runs as: 0 for root, 2000 for adb (shell).
  /// Null if it isn't running.
  Future<int?> getUid() {
    return ShizukuApiPlatform.instance.getUid();
  }

  /// SELinux context of the Shizuku server, or null if it isn't running.
  Future<String?> getSELinuxContext() {
    return ShizukuApiPlatform.instance.getSELinuxContext();
  }

  /// True if the Shizuku app is installed.
  Future<bool?> isShizukuInstalled() {
    return ShizukuApiPlatform.instance.isShizukuInstalled();
  }

  /// Opens the Shizuku app, e.g. so the user can start the service.
  /// Returns false if it isn't installed.
  Future<bool?> openShizukuApp() {
    return ShizukuApiPlatform.instance.openShizukuApp();
  }

  /// Emits whether the Shizuku binder is alive: the current state as soon as
  /// it's listened to, then true when Shizuku starts and false when it stops.
  Stream<bool> get binderStatus {
    return ShizukuApiPlatform.instance.binderStatus;
  }

  // Future<List<String>?> runCommands(String command) {
  //   return ShizukuApiPlatform.instance.runCommands(command);
  // }
}
