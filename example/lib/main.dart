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
  bool _shizukuApiAccess = false;
  final _shizukuApiPlugin = ShizukuApi();
  var singleOutputController =
      TextEditingController(text: 'pm uninstall --user 0 com.android.chrome');

  String outputString = '';
  @override
  void initState() {
    super.initState();
  }

  Future<void> initPlatformState() async {
    bool shizukuApiPermission;
    try {
      shizukuApiPermission = await _shizukuApiPlugin.checkPermission() ?? false;
    } on PlatformException {
      shizukuApiPermission = false;
    }

    if (!mounted) return;

    setState(() {
      _shizukuApiAccess = shizukuApiPermission;
    });
  }

  Future<bool> isBinderRunning() async {
    bool isBinderRunning = await _shizukuApiPlugin.pingBinder() ?? false;
    print('isBinderRunning$isBinderRunning');
    return isBinderRunning;
  }

  void runCommand(String command) async {
    outputString = await _shizukuApiPlugin.runCommand(command) ?? '';
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.purple,
        ),
        useMaterial3: true,
      ),
      darkTheme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          brightness: Brightness.dark,
          seedColor: Colors.purple,
        ),
        useMaterial3: true,
      ),
      home: Scaffold(
        appBar: AppBar(
          centerTitle: true,
          title: const Text('Shizuku Api'),
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () async {
            bool i = await isBinderRunning();
            if (i == true) {
              print(i);
              initPlatformState();
            }
          },
          child: const Icon(Icons.refresh_rounded),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text('Shizuku Access: $_shizukuApiAccess\n'),
              ElevatedButton.icon(
                icon: const Icon(Icons.chevron_right_rounded),
                onPressed: () async {
                  bool i = await isBinderRunning();
                  if (kDebugMode) {
                    print(i);
                  }
                  if (i == true) {
                    initPlatformState();
                  }
                },
                label: const Text('Request Shizuku Access'),
              ),
              TextField(
                decoration: const InputDecoration(
                    label: Text('Command'),
                    helperText: 'eg: pm uninstall --user 0 <packageName>'),
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
}
