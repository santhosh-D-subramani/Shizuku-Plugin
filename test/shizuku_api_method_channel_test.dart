import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:shizuku_api/shizuku_api_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelShizukuApi platform = MethodChannelShizukuApi();
  const MethodChannel channel = MethodChannel('shizuku_api');
  final calls = <MethodCall>[];

  setUp(() {
    calls.clear();
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        calls.add(methodCall);
        switch (methodCall.method) {
          case 'getVersion':
          case 'getLatestServiceVersion':
            return 13;
          case 'getUid':
            return 2000;
          case 'getSELinuxContext':
            return 'u:r:shell:s0';
          case 'runCommand':
            return 'ok';
          default:
            return true;
        }
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('requestPermission sends a request code', () async {
    expect(await platform.requestPermission(), true);
    expect(calls.single.arguments, {'requestCode': 123});
  });

  test('runCommand sends the command', () async {
    expect(await platform.runCommand('wm size'), 'ok');
    expect(calls.single.arguments, {'command': 'wm size'});
  });

  test('server info methods', () async {
    expect(await platform.getVersion(), 13);
    expect(await platform.getLatestServiceVersion(), 13);
    expect(await platform.getUid(), 2000);
    expect(await platform.getSELinuxContext(), 'u:r:shell:s0');
  });

  test('bool methods', () async {
    expect(await platform.shouldShowRequestPermissionRationale(), true);
    expect(await platform.isPreV11(), true);
    expect(await platform.isShizukuInstalled(), true);
    expect(await platform.openShizukuApp(), true);
    expect(calls.map((c) => c.method), [
      'shouldShowRequestPermissionRationale',
      'isPreV11',
      'isShizukuInstalled',
      'openShizukuApp',
    ]);
  });
}
