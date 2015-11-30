package com.belichenko.filetransfer;

import android.os.Message;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Админ on 13.08.2015.
 */
public class OurInputStream extends Thread {
    private Handler handler;
    private ObjectInputStream inputStream;

    /**
     * Constructs a new {@code Thread} with no {@code Runnable} object and the
     * name provided. The new {@code Thread} will belong to the same {@code
     * ThreadGroup} as the {@code Thread} calling this constructor.
     *
     * @param threadName the name for the {@code Thread} being created
     * @see ThreadGroup
     * @see Runnable
     */
    public OurInputStream(String threadName, Handler handler, ObjectInputStream inputStream) {
        super(threadName);
        this.handler = handler;
        this.inputStream = inputStream;
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
        inputLoop();
    }

    private void inputLoop() {
        File fromServer;
        // Read disks
        try {
            while ((fromServer = (File) inputStream.readObject()) != null) {
                Log.d("Exchange_log", "Resive disk -" + fromServer.toString());
                Message msg = handler.obtainMessage(1, fromServer);
                handler.sendMessage(msg);
            }
            // Read files
            while ((fromServer = (File) inputStream.readObject()) != null) {
                Log.d("Exchange_log", "Resive file -" + fromServer.toString());
                Message msg = handler.obtainMessage(2, fromServer);
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            Message msg = handler.obtainMessage(3, "Error " + e.getMessage());
            handler.sendMessage(msg);
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            Log.d("Exchange_log", "Class not found - " + e.toString());
            e.printStackTrace();
        }

    }
}
