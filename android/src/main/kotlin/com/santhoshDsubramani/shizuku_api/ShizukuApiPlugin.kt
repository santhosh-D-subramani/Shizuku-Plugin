package com.santhoshDsubramani.shizuku_api

import android.content.pm.PackageManager
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import rikka.shizuku.Shizuku

/**
 * ShizukuApiPlugin
 */
class ShizukuApiPlugin: FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private var mShizukuShell: ShizukuShell? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "shizuku_api")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "requestPermission" -> {
                val requestCode = call.argument<Int>("requestCode") ?: 0
                requestPermissionAsync(requestCode, result)
            }
            "runCommand" -> {
                result.success(runCommand(call.argument<String>("command")))
            }
            "pingBinder" -> {
                result.success(isBinderRunning())
            }
            "checkPermission" -> {
                val isGranted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
                result.success(isGranted)
            }
            else -> result.notImplemented()
        }
    }

    /**
     * Run a command using Shizuku.
     *
     * @param command Shell Command to run
     * @return Outputs a String
     */
    private fun runCommand(command: String?): String {
        //System.out.println("command = " + command);
        val shizukuShell = ShizukuShell(command)
        if (mShizukuShell != null && mShizukuShell!!.isBusy()) {
            shizukuShell.destroy()
        }
        return shizukuShell.execCommands()
    }

    /**
     * Check if Shizuku is running.
     *
     * @return true if Shizuku is running, false if not
     */
    private fun isBinderRunning(): Boolean {
        return Shizuku.pingBinder()
    }


    /**
     * Request Shizuku permission.
     * @param code Request code
     * @param result result that sent back to flutter
     */
    private fun requestPermissionAsync(code: Int, result: Result) {
        if (Shizuku.isPreV11()) {
            // Pre-v11 is unsupported
            result.success(false)
            return
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            result.success(true)
            return
        }

        if (Shizuku.shouldShowRequestPermissionRationale()) {
            // User denied permission and chose "Don't ask again"
            result.success(false)
            return
        }

        // Request permission and listen for results
        // fix for https://github.com/santhosh-D-subramani/Shizuku-Plugin/issues/2
        Shizuku.addRequestPermissionResultListener(object : Shizuku.OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                if (requestCode == code) {
                    // Cleaning up listener once result is handled
                    Shizuku.removeRequestPermissionResultListener(this)
                    val isGranted = grantResult == PackageManager.PERMISSION_GRANTED
                    result.success(isGranted)
                }
            }
        })

        Shizuku.requestPermission(code)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
