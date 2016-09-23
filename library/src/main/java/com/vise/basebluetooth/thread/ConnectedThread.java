package com.vise.basebluetooth.thread;

import android.bluetooth.BluetoothSocket;

import com.vise.basebluetooth.BluetoothChatHelper;
import com.vise.basebluetooth.common.ChatConstant;
import com.vise.basebluetooth.utils.BleLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Description: 连接后维护线程
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-13 18:38
 */
public class ConnectedThread extends Thread {

    private BluetoothChatHelper mHelper;
    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;

    public ConnectedThread(BluetoothChatHelper bluetoothChatHelper, BluetoothSocket socket, String socketType) {
        BleLog.i("create ConnectedThread: " + socketType);
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
        int count = 0;
        int haveCheck = 0;
        int bytes;
        try{
            // 如果在网络传输中数据没有完全传递，则方法返回0
            while (count == 0) {
                count = mInStream.available();
                haveCheck++;
                if (haveCheck >= 50)
                    return;
            }
            byte[] buffer = new byte[count];
            bytes = mInStream.read(buffer);
            mHelper.getHandler().obtainMessage(ChatConstant.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
        }catch (IOException e){
            BleLog.e("disconnected", e);
            mHelper.connectionLost();
            this.start();
            return;
        }
    }

    public void write(byte[] buffer) {
        if(mSocket.isConnected()){
            try {
                mOutStream.write(buffer);
                mHelper.getHandler().obtainMessage(ChatConstant.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                BleLog.e("Exception during write", e);
            }
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
