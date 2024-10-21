import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'shizuku_api_method_channel.dart';

abstract class ShizukuApiPlatform extends PlatformInterface {
  /// Constructs a ShizukuApiPlatform.
  ShizukuApiPlatform() : super(token: _token);

  static final Object _token = Object();

  static ShizukuApiPlatform _instance = MethodChannelShizukuApi();

  /// The default instance of [ShizukuApiPlatform] to use.
  ///
  /// Defaults to [MethodChannelShizukuApi].
  static ShizukuApiPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ShizukuApiPlatform] when
  /// they register themselves.
  static set instance(ShizukuApiPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
