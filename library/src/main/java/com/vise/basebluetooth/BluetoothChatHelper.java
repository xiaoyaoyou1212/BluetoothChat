package com.vise.basebluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.vise.basebluetooth.callback.IChatCallback;
import com.vise.basebluetooth.common.ChatConstant;
import com.vise.basebluetooth.common.State;
import com.vise.basebluetooth.mode.BaseMessage;
import com.vise.basebluetooth.thread.AcceptThread;
import com.vise.basebluetooth.thread.ConnectThread;
import com.vise.basebluetooth.thread.ConnectedThread;
import com.vise.basebluetooth.utils.BleLog;

/**
 * @Description: 蓝牙消息处理帮助类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-18 17:35
 */
public class BluetoothChatHelper {

    private final BluetoothAdapter mAdapter;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private State mState;
    private IChatCallback<byte[]> mChatCallback;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null || msg.obj == null) {
                return;
            }
            switch (msg.what) {
                case ChatConstant.MESSAGE_STATE_CHANGE:
                    if (mChatCallback != null) {
                        mChatCallback.connectStateChange((State) msg.obj);
                    }
                    break;
                case ChatConstant.MESSAGE_WRITE:
                    if (mChatCallback != null) {
                        mChatCallback.writeData((byte[]) msg.obj, 0);
                    }
                    break;
                case ChatConstant.MESSAGE_READ:
                    if (mChatCallback != null) {
                        mChatCallback.readData((byte[]) msg.obj, 0);
                    }
                    break;
                case ChatConstant.MESSAGE_DEVICE_NAME:
                    if (mChatCallback != null) {
                        mChatCallback.setDeviceName((String) msg.obj);
                    }
                    break;
                case ChatConstant.MESSAGE_TOAST:
                    if (mChatCallback != null) {
                        mChatCallback.showMessage((String) msg.obj, 0);
                    }
                    break;
            }
        }
    };

    public BluetoothChatHelper(IChatCallback<byte[]> chatCallback) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = State.STATE_NONE;
        this.mChatCallback = chatCallback;
    }

    public synchronized State getState() {
        return mState;
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    public Handler getHandler() {
        return mHandler;
    }


    private synchronized BluetoothChatHelper setState(State state) {
        mState = state;
        mHandler.obtainMessage(ChatConstant.MESSAGE_STATE_CHANGE, -1, -1, state).sendToTarget();
        return this;
    }

    public ConnectThread getConnectThread() {
        return mConnectThread;
    }

    public BluetoothChatHelper setConnectThread(ConnectThread mConnectThread) {
        this.mConnectThread = mConnectThread;
        return this;
    }

    public ConnectedThread getConnectedThread() {
        return mConnectedThread;
    }

    public BluetoothChatHelper setConnectedThread(ConnectedThread mConnectedThread) {
        this.mConnectedThread = mConnectedThread;
        return this;
    }

    public synchronized void start(boolean secure) {
        BleLog.d("server start");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(State.STATE_LISTEN);

        if (mAcceptThread == null) {
            if(secure){
                BleLog.d("mSecureAcceptThread start");
            } else{
                BleLog.d("mInsecureAcceptThread start");
            }
            mAcceptThread = new AcceptThread(this, secure);
            mAcceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device, boolean secure) {
        BleLog.d("connect to: " + device);
        if (mState == State.STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(this, device, secure);
        mConnectThread.start();
        setState(State.STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device, final String socketType) {
        BleLog.d("connected, Socket Type:" + socketType);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        mConnectedThread = new ConnectedThread(this, socket, socketType);
        mConnectedThread.start();

        mHandler.obtainMessage(ChatConstant.MESSAGE_DEVICE_NAME, -1, -1, device.getName()).sendToTarget();
        setState(State.STATE_CONNECTED);
    }

    public synchronized void stop() {
        BleLog.d("server stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        setState(State.STATE_NONE);
    }

    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != State.STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        r.write(out);
    }

    public void connectionFailed() {
        mHandler.obtainMessage(ChatConstant.MESSAGE_TOAST, -1, -1, "Unable to connect device").sendToTarget();
        this.start(false);
    }

    public void connectionLost() {
        mHandler.obtainMessage(ChatConstant.MESSAGE_TOAST, -1, -1, "Device connection was lost").sendToTarget();
        this.start(false);
    }

}
