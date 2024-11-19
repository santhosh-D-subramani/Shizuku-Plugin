package com.santhoshDsubramani.shizuku_api;




import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuRemoteProcess;
import rikka.shizuku.ShizukuBinderWrapper;

public class ShizukuShell {

    private static List<String> mOutput;
    private static ShizukuRemoteProcess mProcess = null;
    private static String mCommand;
    private static String mDir = "/";

    public ShizukuShell(
           List<String> output,
            String command) {
      mOutput = output;
        mCommand = command;
    }

    public boolean isBusy() {
        return mOutput != null && !mOutput.isEmpty() && !mOutput.get(mOutput.size() - 1).equals("aShell: Finish");
    }

    public void exec() {
        try {
            mProcess = Shizuku.newProcess(new String[] {"sh", "-c", mCommand}, null, mDir);
            BufferedReader mInput = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            BufferedReader mError = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));

            String line;
            while ((line = mInput.readLine()) != null) {
                System.out.println("line while ((line = mInput.readLine()) != null) = " + line);
                mOutput.add(line);
            }
            while ((line = mError.readLine()) != null) {
                System.out.println("line while ((line = mError.readLine()) != null)= " + line);
                mOutput.add("<font color=#FF0000>" + line + "</font>");
            }
            mProcess.waitFor();
        } catch (Exception ignored) {


        }  finally {
        if (mProcess != null) {
            mProcess.destroy();
        }
    }
    }

    public void destroy() {
        if (mProcess != null) mProcess.destroy();
    }

}