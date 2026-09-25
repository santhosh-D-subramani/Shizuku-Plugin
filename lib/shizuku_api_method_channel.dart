import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'shizuku_api_platform_interface.dart';

/// An implementation of [ShizukuApiPlatform] that uses method channels.
class MethodChannelShizukuApi extends ShizukuApiPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('shizuku_api');

  /// The event channel that reports whether the Shizuku binder is alive.
  @visibleForTesting
  final binderStatusChannel = const EventChannel('shizuku_api/binder_status');

  Stream<bool>? _binderStatus;

  @override
  Future<bool?> requestPermission() async {
    const int requestCode = 123;
    final isGranted = await methodChannel
        .invokeMethod<bool>('requestPermission', {'requestCode': requestCode});
    return isGranted;
  }

  @override
  Future<bool?> pingBinder() async {
    final isRunning = await methodChannel.invokeMethod<bool>('pingBinder');
    return isRunning;
  }

  @override
  Future<bool?> checkPermission() async {
    final isRunning = await methodChannel.invokeMethod<bool>('checkPermission');
    return isRunning;
  }

  @override
  Future<String?> runCommand(String command) async {
    final output = await methodChannel
        .invokeMethod<String>('runCommand', {'command': command});
    return output;
  }

  @override
  Future<bool?> shouldShowRequestPermissionRationale() {
    return methodChannel
        .invokeMethod<bool>('shouldShowRequestPermissionRationale');
  }

  @override
  Future<bool?> isPreV11() {
    return methodChannel.invokeMethod<bool>('isPreV11');
  }

  @override
  Future<int?> getVersion() {
    return methodChannel.invokeMethod<int>('getVersion');
  }

  @override
  Future<int?> getServerPatchVersion() {
    return methodChannel.invokeMethod<int>('getServerPatchVersion');
  }

  @override
  Future<int?> getLatestServiceVersion() {
    return methodChannel.invokeMethod<int>('getLatestServiceVersion');
  }

  @override
  Future<int?> getUid() {
    return methodChannel.invokeMethod<int>('getUid');
  }

  @override
  Future<String?> getSELinuxContext() {
    return methodChannel.invokeMethod<String>('getSELinuxContext');
  }

  @override
  Future<bool?> isShizukuInstalled() {
    return methodChannel.invokeMethod<bool>('isShizukuInstalled');
  }

  @override
  Future<bool?> openShizukuApp() {
    return methodChannel.invokeMethod<bool>('openShizukuApp');
  }

  @override
  Stream<bool> get binderStatus {
    return _binderStatus ??= binderStatusChannel
        .receiveBroadcastStream()
        .map((event) => event as bool);
  }

  // @override
  // Future<List<String>?> runCommands(String command) async {
  //   final List<dynamic>? output =
  //       await methodChannel.invokeMethod<List<dynamic>>(
  //     'runCommands',
  //     {'command': command},
  //   );
  //
  //   final List<String>? typedOutput =
  //       output?.map((item) => item.toString()).toList();
  //
  //   return typedOutput;
  // }
}
