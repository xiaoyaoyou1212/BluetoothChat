package com.vise.basebluetooth.thread;

import android.bluetooth.BluetoothSocket;

import com.vise.basebluetooth.BluetoothChatHelper;
import com.vise.basebluetooth.utils.BleLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-13 18:38
 */
public class MessageThread extends Thread {

    private BluetoothChatHelper mHelper;
    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;

    public MessageThread(BluetoothChatHelper bluetoothChatHelper, BluetoothSocket socket, String socketType) {
        BleLog.d("create ConnectedThread: " + socketType);
        mHelper = bluetoothChatHelper;
        mSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            BleLog.e("temp sockets not created", e);
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;
    }

    public void run() {
        BleLog.i("BEGIN mConnectedThread");
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = mInStream.read(buffer);
                mHelper.getHandler().obtainMessage(BluetoothChatHelper.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                BleLog.e("disconnected", e);
                mHelper.connectionLost();
                this.start();
                break;
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            mOutStream.write(buffer);
            mHelper.getHandler().obtainMessage(BluetoothChatHelper.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
        } catch (IOException e) {
            BleLog.e("Exception during write", e);
        }
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            BleLog.e("close() of connect socket failed", e);
        }
    }
}
