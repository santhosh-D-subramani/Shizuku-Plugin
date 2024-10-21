
import 'shizuku_api_platform_interface.dart';

class ShizukuApi {
  Future<String?> getPlatformVersion() {
    return ShizukuApiPlatform.instance.getPlatformVersion();
  }
}
