package com.santhoshDsubramani.shizuku_api

import java.io.InputStream

import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuRemoteProcess

class ShizukuShell(private var mCommand: String?) {

    @Volatile
    private var mProcess: ShizukuRemoteProcess? = null
    private var mDir = "/"

    fun isBusy(): Boolean {
        return mProcess != null
    }

    fun execCommands(): String {
        val outputBuilder = StringBuilder()
        try {
            if (mCommand.isNullOrEmpty()) {
                throw IllegalArgumentException("Command cannot be null or empty")
            }

            val process = Shizuku.newProcess(arrayOf("sh", "-c", mCommand), null, mDir)
            mProcess = process

            // Drain stderr on its own thread. Reading it only after stdout ends
            // hangs forever once the command fills the stderr pipe buffer.
            var error = ""
            val errorReader = Thread { error = readFully(process.errorStream) }
            errorReader.start()
            val output = readFully(process.inputStream)
            errorReader.join()

            outputBuilder.append(output).append(error)
            process.waitFor()
        } catch (e: IllegalArgumentException) {
            outputBuilder.append("Error: ").append(e.message)
        } catch (e: Exception) {
            outputBuilder.append("Unexpected error: ").append(e.message)
            e.printStackTrace()
        } finally {
            destroyProcess()
        }
        return outputBuilder.toString().trim()
    }

    private fun readFully(stream: InputStream): String {
        val builder = StringBuilder()
        try {
            stream.bufferedReader().forEachLine { builder.append(it).append("\n") }
        } catch (e: Exception) {
            // Stream closed by destroy(); keep what was read.
        }
        return builder.toString()
    }

    private fun destroyProcess() {
        try {
            mProcess?.destroy()
        } catch (t: Throwable) {
            // rikka.shizuku.ShizukuRemoteProcess.destroy() throws NullPointerException
            // if the remote process or binder died before destroy() was called.
        } finally {
            mProcess = null
        }
    }

    fun destroy() {
        destroyProcess()
    }
}
