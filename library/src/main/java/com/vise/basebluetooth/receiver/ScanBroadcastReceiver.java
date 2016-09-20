package com.vise.basebluetooth.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vise.basebluetooth.callback.IScanCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 扫描处理广播
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/19 22:38.
 */
public class ScanBroadcastReceiver extends BroadcastReceiver {

    private IScanCallback<BluetoothDevice> scanCallback;
    private final Map<String, BluetoothDevice> mDeviceMap = new HashMap<>();

    public ScanBroadcastReceiver(IScanCallback<BluetoothDevice> scanCallback) {
        this.scanCallback = scanCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (scanCallback == null) {
            return;
        }
        if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)){
            //扫描到蓝牙设备
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bluetoothDevice == null) {
                return;
            }
            if (!mDeviceMap.containsKey(bluetoothDevice.getAddress())) {
                mDeviceMap.put(bluetoothDevice.getAddress(), bluetoothDevice);
            }
            scanCallback.discoverDevice(bluetoothDevice);
        }else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            //扫描设备结束
            final List<BluetoothDevice> deviceList = new ArrayList<>(mDeviceMap.values());
            if(deviceList != null && deviceList.size() > 0){
                scanCallback.scanFinish(deviceList);
            } else{
                scanCallback.scanTimeout();
            }
        }
    }
}
