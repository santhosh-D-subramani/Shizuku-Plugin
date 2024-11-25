package com.santhoshDsubramani.shizuku_api;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuRemoteProcess;

public class ShizukuShell {

    private static ShizukuRemoteProcess mProcess = null;
    private static String mCommand;
    private static String mDir = "/";

    public ShizukuShell(String command) {
        mCommand = command;
    }

    public boolean isBusy() {
        return mProcess != null;

    }

    public String execCommands() {
        StringBuilder outputBuilder = new StringBuilder();
        try {
            System.out.println("mCommand = " + mCommand);
            if (mCommand == null || mCommand.isEmpty()) {
                throw new IllegalArgumentException("Command cannot be null or empty");
            }

            mProcess = Shizuku.newProcess(new String[]{"sh", "-c", mCommand}, null, mDir);
            BufferedReader mInput = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            BufferedReader mError = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));

            String line;
            // Read standard output
            while ((line = mInput.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }
            // Read error output
            while ((line = mError.readLine()) != null) {
                outputBuilder.append("<font color=#FF0000>").append(line).append("</font>\n");
            }

            mProcess.waitFor();
        } catch (IllegalArgumentException e) {
            outputBuilder.append("Error: ").append(e.getMessage());
        } catch (Exception e) {
            outputBuilder.append("Unexpected error: ").append(e.getMessage());
            e.printStackTrace();
        } finally {
            if (mProcess != null) {
                mProcess.destroy();
            }
        }
        return outputBuilder.toString().trim();
    }

    public void destroy() {
        if (mProcess != null) {
            mProcess.destroy();
        }
    }
}
