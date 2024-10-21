import 'shizuku_api_platform_interface.dart';

class ShizukuApi {
  Future<bool?> checkPermission() {
    return ShizukuApiPlatform.instance.checkPermission();
  }
}
