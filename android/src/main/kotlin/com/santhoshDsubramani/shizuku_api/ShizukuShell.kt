package com.santhoshDsubramani.shizuku_api

import java.io.BufferedReader
import java.io.InputStreamReader

import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuRemoteProcess

class ShizukuShell(private var mCommand: String?) {

    private var mProcess: ShizukuRemoteProcess? = null
    private var mDir = "/"

    fun isBusy(): Boolean {
        return mProcess != null
    }

    fun execCommands(): String {
        val outputBuilder = StringBuilder()
        try {
            // System.out.println("mCommand = " + mCommand);
            if (mCommand.isNullOrEmpty()) {
                throw IllegalArgumentException("Command cannot be null or empty")
            }

            mProcess = Shizuku.newProcess(arrayOf("sh", "-c", mCommand), null, mDir)
            val mInput = BufferedReader(InputStreamReader(mProcess!!.inputStream))
            val mError = BufferedReader(InputStreamReader(mProcess!!.errorStream))

            var line: String?
            // Read standard output
            while (mInput.readLine().also { line = it } != null) {
                outputBuilder.append(line).append("\n")
            }
            // Read error output
            while (mError.readLine().also { line = it } != null) {
                outputBuilder.append(line).append("\n")
            }

            mProcess!!.waitFor()
        } catch (e: IllegalArgumentException) {
            outputBuilder.append("Error: ").append(e.message)
        } catch (e: Exception) {
            outputBuilder.append("Unexpected error: ").append(e.message)
            e.printStackTrace()
        } finally {
            mProcess?.destroy()
        }
        return outputBuilder.toString().trim()
    }

    fun destroy() {
        mProcess?.destroy()
    }
}
