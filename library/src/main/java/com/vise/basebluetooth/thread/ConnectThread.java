package com.vise.basebluetooth.thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.vise.basebluetooth.BluetoothChatHelper;
import com.vise.basebluetooth.utils.BleLog;

import java.io.IOException;
import java.util.UUID;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-13 17:58
 */
public class ConnectThread extends Thread {

    private static final UUID UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private BluetoothChatHelper mHelper;
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private String mSocketType;

    public ConnectThread(BluetoothChatHelper bluetoothChatHelper, BluetoothDevice device, boolean secure) {
        mHelper = bluetoothChatHelper;
        mDevice = device;
        BluetoothSocket tmp = null;
        mSocketType = secure ? "Secure" : "Insecure";

        try {
            if (secure) {
                tmp = device.createRfcommSocketToServiceRecord(UUID_SECURE);
            } else {
                tmp = device.createInsecureRfcommSocketToServiceRecord(UUID_INSECURE);
            }
        } catch (IOException e) {
            BleLog.e("Socket Type: " + mSocketType + "create() failed", e);
        }
        mSocket = tmp;
    }

    public void run() {
        BleLog.i("BEGIN mConnectThread SocketType:" + mSocketType);
        setName("ConnectThread" + mSocketType);

        mHelper.getAdapter().cancelDiscovery();

        try {
            mSocket.connect();
        } catch (IOException e) {
            // Close the socket
            try {
                mSocket.close();
            } catch (IOException e2) {
                BleLog.e("unable to close() " + mSocketType + " socket during connection failure", e2);
            }
            mHelper.connectionFailed();
            return;
        }

        synchronized (this) {
            mHelper.setConnectThread(null);
        }

        mHelper.connected(mSocket, mDevice, mSocketType);
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            BleLog.e("close() of connect " + mSocketType
                    + " socket failed", e);
        }
    }
}
