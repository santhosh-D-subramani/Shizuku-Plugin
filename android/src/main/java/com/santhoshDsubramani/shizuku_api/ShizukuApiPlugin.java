package com.santhoshDsubramani.shizuku_api;

import androidx.annotation.*;

import android.content.pm.PackageManager;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import rikka.shizuku.Shizuku;

import com.santhoshDsubramani.shizuku_api.ShizukuShell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ShizukuApiPlugin
 */
public class ShizukuApiPlugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private ShizukuShell mShizukuShell = null;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {

        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "shizuku_api");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("requestPermission")) {
            int requestCode = call.argument("requestCode");
            boolean isGranted = requestPermission(requestCode);
            result.success(isGranted);
        } else if (call.method.equals("runCommand")) {
            result.success(runCommand(call.argument("command")));
        } else if (call.method.equals("pingBinder")) {

            result.success(isBinderRunning());
        } else if (call.method.equals("checkPermission")) {
            boolean isGranted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
            result.success(isGranted);
        } else {
            result.notImplemented();
        }
    }

    /**
     * Run a command using Shizuku.
     *
     * @param command Shell Command to run
     * @return Outputs a String
     */
    private String runCommand(String command) {
        System.out.println("command = " + command);
        ShizukuShell shizukuShell = new ShizukuShell(command);
        if (mShizukuShell != null && mShizukuShell.isBusy()) {
            shizukuShell.destroy();
        }
        return shizukuShell.execCommands();
    }

    /**
     * Check if Shizuku is running.
     *
     * @return true if Shizuku is running, false if not
     */
    private boolean isBinderRunning() {
        return Shizuku.pingBinder();
    }

    /**
     * Request Shizuku permission.
     *
     * @param code Request code
     * @return true if permission granted, false if not
     */
    private boolean requestPermission(int code) {
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
