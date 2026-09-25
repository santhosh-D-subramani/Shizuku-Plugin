package com.santhoshDsubramani.shizuku_api

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import rikka.shizuku.Shizuku
import java.util.Collections
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ShizukuApiPlugin
 *
 * All Shizuku listeners are delivered on the main thread, and Flutter calls
 * [onMethodCall] on the main thread, so the shared state below is only ever
 * touched from the main thread. Commands run on a background executor and post
 * their results back to it.
 */
class ShizukuApiPlugin : FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {
    private lateinit var channel: MethodChannel
    private lateinit var binderStatusChannel: EventChannel
    private lateinit var context: Context
    private var binderStatusSink: EventChannel.EventSink? = null

    private var commandExecutor: ExecutorService? = null
    private val runningShells = Collections.synchronizedSet(HashSet<ShizukuShell>())

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "shizuku_api")
        channel.setMethodCallHandler(this)
        binderStatusChannel = EventChannel(flutterPluginBinding.binaryMessenger, "shizuku_api/binder_status")
        binderStatusChannel.setStreamHandler(this)
        commandExecutor = Executors.newCachedThreadPool()
        attachedPlugins.add(this)
        ensureShizukuListeners()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        binderStatusChannel.setStreamHandler(null)
        binderStatusSink = null
        attachedPlugins.remove(this)
        // The engine is gone, so its waiting futures can't be answered anymore.
        pendingPermissionResults.removeAll { it.owner === this }

        commandExecutor?.shutdownNow()
        commandExecutor = null
        synchronized(runningShells) {
            runningShells.forEach { it.destroy() }
            runningShells.clear()
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        try {
            when (call.method) {
                "requestPermission" -> {
                    val requestCode = call.argument<Int>("requestCode") ?: DEFAULT_REQUEST_CODE
                    requestPermission(requestCode, ReplyOnce(result))
                }
                "runCommand" -> runCommand(call.argument<String>("command"), ReplyOnce(result))
                "pingBinder" -> result.success(isBinderRunning())
                "checkPermission" -> result.success(isPermissionGranted())
                "shouldShowRequestPermissionRationale" -> result.success(
                    isBinderRunning() && !Shizuku.isPreV11() && Shizuku.shouldShowRequestPermissionRationale()
                )
                "isPreV11" -> result.success(isBinderRunning() && Shizuku.isPreV11())
                "getVersion" -> result.success(ifBinderRunning { Shizuku.getVersion() })
                "getServerPatchVersion" -> result.success(ifBinderRunning { Shizuku.getServerPatchVersion() })
                "getLatestServiceVersion" -> result.success(Shizuku.getLatestServiceVersion())
                "getUid" -> result.success(ifBinderRunning { Shizuku.getUid() })
                "getSELinuxContext" -> result.success(ifBinderRunning { Shizuku.getSELinuxContext() })
                "isShizukuInstalled" -> result.success(isShizukuInstalled())
                "openShizukuApp" -> result.success(openShizukuApp())
                else -> result.notImplemented()
            }
        } catch (e: Exception) {
            // Shizuku throws when its binder dies between our ping and the call.
            // Surface that as a PlatformException instead of an uncaught error.
            Log.w(TAG, "${call.method} failed", e)
            result.error("SHIZUKU_ERROR", e.message, null)
        }
    }

    // region Binder status stream

    override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
        binderStatusSink = events
        events.success(isBinderRunning())
    }

    override fun onCancel(arguments: Any?) {
        binderStatusSink = null
    }

    private fun onBinderStatusChanged(isRunning: Boolean) {
        binderStatusSink?.success(isRunning)
    }

    // endregion

    /**
     * Run a command using Shizuku, off the main thread.
     *
     * @param command Shell Command to run
     */
    private fun runCommand(command: String?, result: ReplyOnce) {
        val executor = commandExecutor
        if (executor == null) {
            result.error("DETACHED", "Plugin is detached from the engine", null)
            return
        }
        val shizukuShell = ShizukuShell(command)
        runningShells.add(shizukuShell)
        try {
            executor.execute {
                val output = try {
                    shizukuShell.execCommands()
                } finally {
                    runningShells.remove(shizukuShell)
                }
                mainHandler.post { result.success(output) }
            }
        } catch (e: RejectedExecutionException) {
            runningShells.remove(shizukuShell)
            result.error("DETACHED", "Plugin is detached from the engine", null)
        }
    }

    /**
     * Check if Shizuku is running.
     *
     * @return true if Shizuku is running, false if not
     */
    private fun isBinderRunning(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }

    private inline fun <T> ifBinderRunning(block: () -> T): T? {
        return if (isBinderRunning()) block() else null
    }

    private fun isPermissionGranted(): Boolean {
        // checkSelfPermission() throws when the binder hasn't been received yet.
        return isBinderRunning() && !Shizuku.isPreV11() &&
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }

    @Suppress("DEPRECATION")
    private fun isShizukuInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openShizukuApp(): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(SHIZUKU_PACKAGE) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.w(TAG, "Couldn't open the Shizuku app", e)
            false
        }
    }

    /**
     * Request Shizuku permission.
     *
     * Only one permission dialog is shown at a time: calls made while a request
     * is waiting for the user share that request's answer instead of stacking
     * another dialog.
     *
     * @param code Request code
     * @param result result that sent back to flutter
     */
    private fun requestPermission(code: Int, result: ReplyOnce) {
        if (!isBinderRunning() || Shizuku.isPreV11()) {
            // Shizuku isn't running, or it's pre-v11 which is unsupported
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

        pendingPermissionResults.add(PendingResult(this, result))

        val inFlight = inFlightRequestCode
        if (inFlight != null && SystemClock.elapsedRealtime() - inFlightSince < STALE_REQUEST_MS) {
            // A dialog is already showing; this call gets that dialog's answer.
            return
        }

        inFlightRequestCode = code
        inFlightSince = SystemClock.elapsedRealtime()
        try {
            Shizuku.requestPermission(code)
        } catch (e: Exception) {
            // Binder died between the checks above and the request. Answer now,
            // otherwise these results would wait for a dialog that never shows.
            Log.w(TAG, "requestPermission failed", e)
            completePermissionRequest(false)
        }
    }

    private class PendingResult(val owner: ShizukuApiPlugin, val result: ReplyOnce)

    /**
     * Wraps a [Result] so it's answered at most once. Answering a [Result] twice
     * throws IllegalStateException, which would crash the host app.
     */
    private class ReplyOnce(private val result: Result) {
        private val replied = AtomicBoolean(false)

        fun success(value: Any?) {
            if (replied.compareAndSet(false, true)) result.success(value)
        }

        fun error(code: String, message: String?, details: Any?) {
            if (replied.compareAndSet(false, true)) result.error(code, message, details)
        }
    }

    companion object {
        private const val TAG = "ShizukuApiPlugin"
        private const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
        private const val DEFAULT_REQUEST_CODE = 0

        // After this long without an answer, a new request shows the dialog
        // again instead of waiting on the old one forever.
        private const val STALE_REQUEST_MS = 2 * 60 * 1000L

        private val mainHandler = Handler(Looper.getMainLooper())
        private val attachedPlugins = mutableListOf<ShizukuApiPlugin>()

        // Results waiting for the user to answer the permission dialog.
        private val pendingPermissionResults = mutableListOf<PendingResult>()
        private var inFlightRequestCode: Int? = null
        private var inFlightSince = 0L
        private var listenersAdded = false

        private fun completePermissionRequest(isGranted: Boolean) {
            inFlightRequestCode = null
            val results = pendingPermissionResults.toList()
            pendingPermissionResults.clear()
            results.forEach { it.result.success(isGranted) }
        }

        // Listeners are added once and never removed. Shizuku's remove*Listener()
        // methods call removeIf() on a CopyOnWriteArrayList, which throws
        // UnsupportedOperationException on Android 7.x and crashed the host app
        // after the permission dialog.
        private val permissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            // Ignore answers to requests the host app made itself with its own codes.
            if (requestCode != inFlightRequestCode) return@OnRequestPermissionResultListener
            completePermissionRequest(grantResult == PackageManager.PERMISSION_GRANTED)
        }

        private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
            attachedPlugins.toList().forEach { it.onBinderStatusChanged(true) }
        }

        private val binderDeadListener = Shizuku.OnBinderDeadListener {
            // The dialog can't answer once Shizuku is gone.
            if (inFlightRequestCode != null) completePermissionRequest(false)
            attachedPlugins.toList().forEach { it.onBinderStatusChanged(false) }
        }

        private fun ensureShizukuListeners() {
            if (listenersAdded) return
            Shizuku.addRequestPermissionResultListener(permissionListener, mainHandler)
            Shizuku.addBinderReceivedListener(binderReceivedListener, mainHandler)
            Shizuku.addBinderDeadListener(binderDeadListener, mainHandler)
            listenersAdded = true
        }
    }
}
