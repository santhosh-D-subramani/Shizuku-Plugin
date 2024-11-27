package com.santhoshDsubramani.shizuku_api;

import androidx.annotation.*;

import android.content.pm.PackageManager;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import rikka.shizuku.Shizuku;

/**
 * ShizukuApiPlugin
 */
public class ShizukuApiPlugin implements FlutterPlugin, MethodCallHandler {
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
            int REQUEST_CODE = call.argument("requestCode");
            requestPermissionAsync(REQUEST_CODE, result);
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
        //System.out.println("command = " + command);
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
     * @param code Request code
     * @param result result that sent back to flutter
     */
    private void requestPermissionAsync(int code, Result result) {
        if (Shizuku.isPreV11()) {
            // Pre-v11 is unsupported
            result.success(false);
            return;
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            result.success(true);
            return;
        }

        if (Shizuku.shouldShowRequestPermissionRationale()) {
            // User denied permission and chose "Don't ask again"
            result.success(false);
            return;
        }

        // Request permission and listen for results
        // fix for https://github.com/santhosh-D-subramani/Shizuku-Plugin/issues/2
        Shizuku.addRequestPermissionResultListener(new Shizuku.OnRequestPermissionResultListener() {
            @Override
            public void onRequestPermissionResult(int requestCode, int grantResult) {
                if (requestCode == code) {
                    // Cleaning up listener once result is handled
                    Shizuku.removeRequestPermissionResultListener(this);
                    boolean isGranted = grantResult == PackageManager.PERMISSION_GRANTED;
                    result.success(isGranted);
                }
            }
        });

        Shizuku.requestPermission(code);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
