import 'shizuku_api_platform_interface.dart';

class ShizukuApi {
  /// To request shizuku access use below code
  /// * Shizuku must be installed on the device and running
  /// ```dart
  ///  final shizukuApiPlugin = ShizukuApi();
  /// await shizukuApiPlugin.checkPermission()
  /// ```
  Future<bool?> checkPermission() {
    return ShizukuApiPlatform.instance.checkPermission();
  }
}
