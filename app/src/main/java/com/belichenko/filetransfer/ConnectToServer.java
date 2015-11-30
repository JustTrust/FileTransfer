package com.belichenko.filetransfer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by Админ on 03.08.2015.
 */
public class ConnectToServer extends Thread {

    Handler handler;

    public ConnectToServer(Handler handler) {
        this.handler = handler;
    }

    /**
     * Override this method to perform a computation on a background thread.
     */
    @Override
    public void run() {
        ArrayList<InetAddress> ipList = getListOfIpAddress();
        if (ipList.isEmpty()) {
            Message msg = handler.obtainMessage(3, "Don't have any connection");
            handler.sendMessage(msg);
        } else {
            tryToConnect(ipList);
        }
    }


    private ArrayList<InetAddress> getListOfIpAddress() {

        ArrayList<InetAddress> returnList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> netIns = NetworkInterface.getNetworkInterfaces();
            if (netIns != null) {
                while (netIns.hasMoreElements()) {
                    NetworkInterface n = netIns.nextElement();
                    Enumeration<NetworkInterface> subNetIns = n.getSubInterfaces();
                    if (subNetIns.hasMoreElements()) {
                        while (subNetIns.hasMoreElements()) {
                            NetworkInterface sub = netIns.nextElement();
                            Enumeration<InetAddress> inetAdrsEnum = sub.getInetAddresses();
                            while (inetAdrsEnum.hasMoreElements()) {
                                InetAddress inetAdrs = inetAdrsEnum.nextElement();
                                if (!inetAdrs.isLoopbackAddress() && inetAdrs instanceof Inet4Address) {
                                    returnList.add(inetAdrs);
                                }
                            }
                        }
                    } else {
                        Enumeration<InetAddress> inetAdrsEnum = n.getInetAddresses();
                        while (inetAdrsEnum.hasMoreElements()) {
                            InetAddress inetAdrs = inetAdrsEnum.nextElement();
                            if (!inetAdrs.isLoopbackAddress() && inetAdrs instanceof Inet4Address) {
                                returnList.add(inetAdrs);
                            }
                        }
                    }

                }
            }
        } catch (SocketException e) {
            Message msg = handler.obtainMessage(3, "Don't get IP list");
            handler.sendMessage(msg);
            e.printStackTrace();
        }
        return returnList;
    }

    private void tryToConnect(ArrayList<InetAddress> ipList) {

        Boolean pass = Boolean.valueOf("True");
        int k = 0;
        InetAddress i;
        while (pass && k < ipList.size()) {
            i = ipList.get(k);
            String[] ipAddress = i.getHostAddress().split("\\.");
            int n = 18;
            while (pass && n < 255) {
                n++;
                try {
                    Log.d("Exchange_log", "Try connect to - " + ipAddress[0] + "." + ipAddress[1]
                            + "." + ipAddress[2] + "." + String.valueOf(n));
                    Socket socket1149 = new Socket(ipAddress[0] + "." + ipAddress[1]
                            + "." + ipAddress[2] + "." + String.valueOf(n), (int) 1149);
                    if (socket1149.isConnected()) {
                        Log.d("Exchange_log", "Connect to server - " + socket1149.toString());
                        ObjectInputStream in = new ObjectInputStream(socket1149.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(socket1149.getOutputStream());

                        new OurInputStream("InStream", handler, in).start();
                        new OurOutputStream("OutStream", handler, out).start();

                        Message msg = handler.obtainMessage(3, "Connect to " + socket1149.getInetAddress().toString() + ":" + socket1149.getPort());
                        handler.sendMessage(msg);
                        pass = false;
                    } else {
                        Log.d("Exchange_log", "Not connected - " + socket1149.toString());
                        socket1149.close();
                    }
                } catch (IOException e) {
                    Message msg = handler.obtainMessage(3, "Error " + e.getMessage());
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
            k++;
        }
    }
}
