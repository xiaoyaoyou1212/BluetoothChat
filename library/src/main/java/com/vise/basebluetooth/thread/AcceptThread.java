package com.vise.basebluetooth.thread;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.vise.basebluetooth.BluetoothChatHelper;
import com.vise.basebluetooth.utils.BleLog;

import java.io.IOException;
import java.util.UUID;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-13 17:57
 */
public class AcceptThread extends Thread {

    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static final UUID UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private BluetoothChatHelper mHelper;
    private final BluetoothServerSocket mmServerSocket;
    private String mSocketType;

    public AcceptThread(BluetoothChatHelper bluetoothChatHelper, boolean secure) {
        mHelper = bluetoothChatHelper;
        BluetoothServerSocket tmp = null;
        mSocketType = secure ? "Secure" : "Insecure";

        try {
            if (secure) {
                tmp = mHelper.getAdapter().listenUsingRfcommWithServiceRecord(NAME_SECURE, UUID_SECURE);
            } else {
                tmp = mHelper.getAdapter().listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, UUID_INSECURE);
            }
        } catch (IOException e) {
            BleLog.e("Socket Type: " + mSocketType + "listen() failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BleLog.d("Socket Type: " + mSocketType + "BEGIN mAcceptThread" + this);
        setName("AcceptThread" + mSocketType);

        BluetoothSocket socket = null;

        while (mHelper.getState() != BluetoothChatHelper.STATE_CONNECTED) {
            try {
                BleLog.e("wait new socket");
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                BleLog.e("Socket Type: " + mSocketType + "accept() failed", e);
                break;
            }
            if (socket != null) {
                synchronized (this) {
                    switch (mHelper.getState()) {
                        case BluetoothChatHelper.STATE_LISTEN:
                        case BluetoothChatHelper.STATE_CONNECTING:
                            BleLog.e("mark CONNECTING");
                            mHelper.connected(socket, socket.getRemoteDevice(), mSocketType);
                            break;
                        case BluetoothChatHelper.STATE_NONE:
                        case BluetoothChatHelper.STATE_CONNECTED:
                            try {
                                socket.close();
                            } catch (IOException e) {
                                BleLog.e("Could not close unwanted socket", e);
                            }
                            break;
                    }
                }
            }
        }
        BleLog.i("END mAcceptThread, socket Type: " + mSocketType);
    }

    public void cancel() {
        BleLog.d("Socket Type" + mSocketType + "cancel " + this);
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            BleLog.e("Socket Type" + mSocketType + "close() of server failed", e);
        }
    }
}
