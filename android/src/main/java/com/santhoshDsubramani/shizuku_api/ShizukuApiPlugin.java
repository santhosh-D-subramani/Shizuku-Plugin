package com.santhoshDsubramani.shizuku_api;

import androidx.annotation.NonNull;
import  android.content.pm.PackageManager;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import rikka.shizuku.Shizuku;
/** ShizukuApiPlugin */
public class ShizukuApiPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "shizuku_api");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("checkPermission")) {
      int requestCode = call.argument("requestCode");
      boolean isGranted = checkPermission(requestCode);
      result.success(isGranted);
    } else {
      result.notImplemented();
    }
  }
  private boolean checkPermission(int code) {
    if (Shizuku.isPreV11()) {
      // Pre-v11 is unsupported
      return false;
    }

    if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
      // Granted
      return true;
    } else if (Shizuku.shouldShowRequestPermissionRationale()) {
      // User denied permission and chose "Don't ask again"
      return false;
    } else {
      // Request the permission
      Shizuku.requestPermission(code);
      return false;
    }
  }
  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
