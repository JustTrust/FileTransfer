package com.belichenko.filetransfer;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by Админ on 13.08.2015.
 */
public class OurOutputStream extends Thread {
    private Handler handler;
    private ObjectOutputStream outputStream;
    private static File settingPath;

    /**
     * Constructs a new {@code Thread} with a {@code Runnable} object and a
     * newly generated name. The new {@code Thread} will belong to the same
     * {@code ThreadGroup} as the {@code Thread} calling this constructor.
     *
     * @param threadName a {@code String} whose method <code>run</code> will be
     *                 executed by the new {@code Thread}
     * @see ThreadGroup
     * @see Runnable
     */
    public OurOutputStream(String threadName, Handler handler, ObjectOutputStream outputStream) {
        super(threadName);
        this.handler = handler;
        this.outputStream = outputStream;
        settingPath = null;
    }

    /**
     * Calls the <code>run()</code> method of the Runnable object the receiver
     * holds. If no Runnable is set, does nothing.
     *
     * @see Thread#start
     */
    @Override
    public void run() {
        super.run();
        while (true){
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            outputLoop();
        }

    }

    public static void setSettingPath(File setPath) {
        settingPath = setPath;
    }


    private void outputLoop(){
        if (settingPath != null){
            try {
                outputStream.writeObject(settingPath);
                outputStream.writeObject(null);
            } catch (IOException e) {
                Message msg = handler.obtainMessage(3, "Error " + e.getMessage());
                handler.sendMessage(msg);
                e.printStackTrace();
            }
            settingPath = null;
        }
    }
}
