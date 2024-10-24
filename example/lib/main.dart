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

  @override
  void initState() {
    super.initState();
    // initPlatformState();
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

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.purple,
        ),
        useMaterial3: true,
      ),
      home: Scaffold(
        appBar: AppBar(
          centerTitle: true,
          backgroundColor: Theme.of(context).colorScheme.inversePrimary,
          title: const Text('Shizuku Api'),
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () {
            initPlatformState();
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
                onPressed: () {
                  initPlatformState();
                },
                label: const Text('Request Shizuku Access'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
