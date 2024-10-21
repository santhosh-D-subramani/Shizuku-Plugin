import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'shizuku_api_platform_interface.dart';

/// An implementation of [ShizukuApiPlatform] that uses method channels.
class MethodChannelShizukuApi extends ShizukuApiPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('shizuku_api');

  @override
  Future<bool?> checkPermission() async {
    const int requestCode = 123;
    final isGranted = await methodChannel
        .invokeMethod<bool>('checkPermission', {'requestCode': requestCode});
    return isGranted;
  }
}
