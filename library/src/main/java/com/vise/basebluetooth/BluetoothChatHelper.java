package com.vise.basebluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.vise.basebluetooth.thread.AcceptThread;
import com.vise.basebluetooth.thread.ConnectThread;
import com.vise.basebluetooth.thread.MessageThread;
import com.vise.basebluetooth.utils.BleLog;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-18 17:35
 */
public class BluetoothChatHelper {

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private MessageThread mMessageThread;
    private int mState;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public BluetoothChatHelper(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    public synchronized int getState() {
        return mState;
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public AcceptThread getSecureAcceptThread() {
        return mSecureAcceptThread;
    }

    private synchronized BluetoothChatHelper setState(int state) {
        mState = state;
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        return this;
    }

    public BluetoothChatHelper setSecureAcceptThread(AcceptThread mSecureAcceptThread) {
        this.mSecureAcceptThread = mSecureAcceptThread;
        return this;
    }

    public AcceptThread getInsecureAcceptThread() {
        return mInsecureAcceptThread;
    }

    public BluetoothChatHelper setInsecureAcceptThread(AcceptThread mInsecureAcceptThread) {
        this.mInsecureAcceptThread = mInsecureAcceptThread;
        return this;
    }

    public ConnectThread getConnectThread() {
        return mConnectThread;
    }

    public BluetoothChatHelper setConnectThread(ConnectThread mConnectThread) {
        this.mConnectThread = mConnectThread;
        return this;
    }

    public MessageThread getMessageThread() {
        return mMessageThread;
    }

    public BluetoothChatHelper setMessageThread(MessageThread mMessageThread) {
        this.mMessageThread = mMessageThread;
        return this;
    }

    public synchronized void start() {
        BleLog.d("server start");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mMessageThread != null) {
            mMessageThread.cancel();
            mMessageThread = null;
        }

        setState(STATE_LISTEN);

        if (mSecureAcceptThread == null) {
            BleLog.d("mSecureAcceptThread start");
            mSecureAcceptThread = new AcceptThread(this, true);
            mSecureAcceptThread.start();
        }
        if (mInsecureAcceptThread == null) {
            BleLog.d("mInsecureAcceptThread start");
            mInsecureAcceptThread = new AcceptThread(this, false);
            mInsecureAcceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device, boolean secure) {
        BleLog.d("connect to: " + device);
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mMessageThread != null) {
            mMessageThread.cancel();
            mMessageThread = null;
        }

        mConnectThread = new ConnectThread(this, device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device, final String socketType) {
        BleLog.d("connected, Socket Type:" + socketType);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mMessageThread != null) {
            mMessageThread.cancel();
            mMessageThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        mMessageThread = new MessageThread(this, socket, socketType);
        mMessageThread.start();

        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        BleLog.d("server stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mMessageThread != null) {
            mMessageThread.cancel();
            mMessageThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    public void write(byte[] out) {
        MessageThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mMessageThread;
        }
        r.write(out);
    }

    public void connectionFailed() {
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        this.start();
    }

    public void connectionLost() {
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        this.start();
    }

}
