package com.santhoshDsubramani.shizuku_api

import android.content.pm.PackageManager

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

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "shizuku_api")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
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
        mShizukuShell?.destroy()
        val shizukuShell = ShizukuShell(command)
        mShizukuShell = shizukuShell
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
        synchronized(pendingPermissionResults) {
            ensurePermissionListener()
            pendingPermissionResults.getOrPut(code) { mutableListOf() }.add(result)
        }

        Shizuku.requestPermission(code)
    }

    companion object {
        // Results waiting for a permission answer, keyed by request code.
        private val pendingPermissionResults = HashMap<Int, MutableList<Result>>()
        private var permissionListenerAdded = false

        // A single listener that is added once and never removed. Shizuku's
        // removeRequestPermissionResultListener() calls removeIf() on a
        // CopyOnWriteArrayList, which throws UnsupportedOperationException on
        // Android 7.x and crashed the host app after the permission dialog.
        private val permissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            val results = synchronized(pendingPermissionResults) {
                pendingPermissionResults.remove(requestCode)
            } ?: return@OnRequestPermissionResultListener
            val isGranted = grantResult == PackageManager.PERMISSION_GRANTED
            results.forEach { it.success(isGranted) }
        }

        private fun ensurePermissionListener() {
            if (!permissionListenerAdded) {
                Shizuku.addRequestPermissionResultListener(permissionListener)
                permissionListenerAdded = true
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
