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
    initPlatformState();
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
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Shizuku Api'),
        ),
        body: Center(
          child: Text('Shizuku Access: $_shizukuApiAccess\n'),
        ),
        floatingActionButton: FloatingActionButton(onPressed: () {
          initPlatformState();
        }),
      ),
    );
  }
}
