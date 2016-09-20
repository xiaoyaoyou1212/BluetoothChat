package com.vise.basebluetooth.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vise.basebluetooth.callback.IPairCallback;

/**
 * @Description: 配对处理广播
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/19 22:37.
 */
public class PairBroadcastReceiver extends BroadcastReceiver {

    private IPairCallback pairCallback;

    public PairBroadcastReceiver(IPairCallback pairCallback) {
        this.pairCallback = pairCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
            //取得状态改变的设备，更新设备列表信息（配对状态）
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(device != null){
                resolveBondingState(device.getBondState());
            }
        }
    }

    private void resolveBondingState(final int bondState) {
        if (pairCallback == null) {
            return;
        }
        switch (bondState) {
            case BluetoothDevice.BOND_BONDED://已配对
                pairCallback.bonded();
                break;
            case BluetoothDevice.BOND_BONDING://配对中
                pairCallback.bonding();
                break;
            case BluetoothDevice.BOND_NONE://未配对
                pairCallback.unBonded();
                break;
            default:
                pairCallback.bondFail();
                break;
        }
    }
}
