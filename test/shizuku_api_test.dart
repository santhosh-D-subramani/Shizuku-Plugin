import 'package:flutter_test/flutter_test.dart';
import 'package:shizuku_api/shizuku_api.dart';
import 'package:shizuku_api/shizuku_api_platform_interface.dart';
import 'package:shizuku_api/shizuku_api_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockShizukuApiPlatform
    with MockPlatformInterfaceMixin
    implements ShizukuApiPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final ShizukuApiPlatform initialPlatform = ShizukuApiPlatform.instance;

  test('$MethodChannelShizukuApi is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelShizukuApi>());
  });

  test('getPlatformVersion', () async {
    ShizukuApi shizukuApiPlugin = ShizukuApi();
    MockShizukuApiPlatform fakePlatform = MockShizukuApiPlatform();
    ShizukuApiPlatform.instance = fakePlatform;

    expect(await shizukuApiPlugin.getPlatformVersion(), '42');
  });
}
