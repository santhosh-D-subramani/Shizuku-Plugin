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

  Future<bool?> requestPermission() {
    throw UnimplementedError('checkPermission() has not been implemented.');
  }

  Future<bool?> pingBinder() {
    throw UnimplementedError('pingBinder() has not been implemented.');
  }

  Future<bool?> checkPermission() {
    throw UnimplementedError('permissionBool() has not been implemented.');
  }

  Future<String?> runCommand(String command) {
    throw UnimplementedError('runCommand() has not been implemented. $command');
  }

  // Future<List<String>?> runCommands(String command) {
  //   throw UnimplementedError(
  //       'runCommands() has not been implemented. $command');
  // }
}
