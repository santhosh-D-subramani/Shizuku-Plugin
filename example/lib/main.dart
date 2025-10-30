import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:shizuku_api/shizuku_api.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool _isShizukuPermissionGranted = false;
  final _shizukuApiPlugin = ShizukuApi();
  var singleOutputController = TextEditingController(text: 'wm size');

  String outputString = '';
  @override
  void initState() {
    super.initState();
  }

  Future<void> requestPermission() async {
    bool shizukuApiPermission;
    try {
      shizukuApiPermission =
          await _shizukuApiPlugin.requestPermission() ?? false;
      if (kDebugMode) {
        print(shizukuApiPermission);
      }
    } on PlatformException {
      shizukuApiPermission = false;
    }

    if (!mounted) return;

    setState(() {
      _isShizukuPermissionGranted = shizukuApiPermission;
    });
  }

  Future<bool> isBinderRunning() async {
    bool isBinderRunning = await _shizukuApiPlugin.pingBinder() ?? false;
    if (kDebugMode) {
      print('isBinderRunning $isBinderRunning');
    }
    return isBinderRunning;
  }

  Future<bool> checkPermission() async {
    bool isShizukuGranted = await _shizukuApiPlugin.checkPermission() ?? false;
    if (kDebugMode) {
      print('checkPermission() $isShizukuGranted');
    }
    return isShizukuGranted;
  }

  void runCommand(String command) async {
    outputString = await _shizukuApiPlugin.runCommand(command) ?? '';
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: lightMode,
      darkTheme: darkMode,
      home: Scaffold(
        appBar: buildAppBar(),
        body: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: () async {
                  bool isShizukuRunning = await isBinderRunning();
                  if (isShizukuRunning == true) {
                    _isShizukuPermissionGranted = await checkPermission();
                    setState(() {});
                  }
                },
                child: Text(
                    'Check Shizuku Permission Granted: $_isShizukuPermissionGranted'),
              ),
              ElevatedButton(
                onPressed: () async {
                  bool i = await isBinderRunning();
                  if (i == true) {
                    requestPermission();
                  }
                },
                child: const Text('Request Shizuku Permission'),
              ),
              TextField(
                decoration: const InputDecoration(
                    label: Text('Command'),
                    helperText:
                    'eg: pm uninstall --user 0 <packageName>, wm size'),
                controller: singleOutputController,
              ),
              ElevatedButton.icon(
                icon: const Icon(Icons.chevron_right_rounded),
                onPressed: () {
                  runCommand(singleOutputController.text);
                },
                label: const Text('Run Command'),
              ),
              Text(outputString)
            ],
          ),
        ),
      ),
    );
  }

  AppBar buildAppBar() {
    return AppBar(
      centerTitle: true,
      title: const Text('Shizuku Api'),
    );
  }
}

final lightMode = ThemeData(
  colorScheme: ColorScheme.fromSeed(
    seedColor: Colors.purple,
  ),
  useMaterial3: true,
);
final darkMode = ThemeData(
  colorScheme: ColorScheme.fromSeed(
    brightness: Brightness.dark,
    seedColor: Colors.purple,
  ),
  useMaterial3: true,
);
