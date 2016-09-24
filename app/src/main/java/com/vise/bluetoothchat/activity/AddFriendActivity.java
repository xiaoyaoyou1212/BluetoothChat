package com.vise.bluetoothchat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vise.basebluetooth.callback.IPairCallback;
import com.vise.basebluetooth.callback.IScanCallback;
import com.vise.basebluetooth.receiver.PairBroadcastReceiver;
import com.vise.basebluetooth.receiver.ScanBroadcastReceiver;
import com.vise.basebluetooth.utils.BluetoothUtil;
import com.vise.bluetoothchat.R;
import com.vise.bluetoothchat.adapter.AddFriendAdapter;
import com.vise.common_base.utils.ToastUtil;
import com.vise.common_utils.log.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-20 15:18
 */
public class AddFriendActivity extends BaseChatActivity {

    private ProgressBar mScanPb;
    private ListView mAddFriendLv;
    private ProgressDialog mProgressDialog;
    private AddFriendAdapter mAdapter;
    private ScanBroadcastReceiver mScanBroadcastReceiver;
    private PairBroadcastReceiver mPairBroadcastReceiver;
    private Map<String, BluetoothDevice> mDeviceMap = new HashMap<>();
    private List<BluetoothDevice> mBluetoothDevices = new ArrayList<>();

    private final IScanCallback<BluetoothDevice> scanCallback = new IScanCallback<BluetoothDevice>() {
        @Override
        public void discoverDevice(BluetoothDevice bluetoothDevice) {
            if (!mDeviceMap.containsKey(bluetoothDevice.getAddress())) {
                mDeviceMap.put(bluetoothDevice.getAddress(), bluetoothDevice);
                mBluetoothDevices.add(bluetoothDevice);
                mAdapter.setListAll(mBluetoothDevices);
            }
        }

        @Override
        public void scanTimeout() {
            mScanPb.setVisibility(View.GONE);
            LogUtils.i("Scan TimeOut!");
            ToastUtil.showToast(mContext, getString(R.string.no_found_friend));
        }

        @Override
        public void scanFinish(List<BluetoothDevice> bluetoothDevices) {
            mScanPb.setVisibility(View.GONE);
            LogUtils.i("Scan Finish!");
        }
    };

    private final IPairCallback pairCallback = new IPairCallback() {
        @Override
        public void unBonded() {
            LogUtils.i("unBonded");
        }

        @Override
        public void bonding() {
            LogUtils.i("bonding");
        }

        @Override
        public void bonded() {
            mProgressDialog.dismiss();
            LogUtils.i("bonded");
            ToastUtil.showToast(mContext, getString(R.string.add_friend_success));
            finish();
        }

        @Override
        public void bondFail() {
            mProgressDialog.hide();
            LogUtils.i("bondFail");
            ToastUtil.showToast(mContext, getString(R.string.add_friend_fail));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
    }

    @Override
    protected void initWidget() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView)findViewById(R.id.title)).setText(getString(R.string.add_friend));

        mProgressDialog = new ProgressDialog(mContext);
        mScanPb = (ProgressBar) findViewById(R.id.add_friend_scan_progress);
        mAddFriendLv = (ListView) findViewById(R.id.add_friend_scan_list);
    }

    @Override
    protected void initData() {
        mAdapter = new AddFriendAdapter(mContext);
        mAddFriendLv.setAdapter(mAdapter);
        beginDiscover();
    }

    @Override
    protected void initEvent() {
        mAddFriendLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mBluetoothDevices.get(position).getBondState() != BluetoothDevice.BOND_BONDED){
                    startPair();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mBluetoothDevices.get(position).createBond();
                    } else{
                        //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);
                        Method createBondMethod = null;
                        try {
                            createBondMethod =BluetoothDevice.class.getMethod("createBond");
                            createBondMethod.invoke(mBluetoothDevices.get(position));
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    mProgressDialog.setMessage(getString(R.string.add_friend_loading));
                    mProgressDialog.show();
                } else{
                    ToastUtil.showToast(mContext, getString(R.string.already_is_friend));
                }
            }
        });
    }

    private void beginDiscover(){
        if(mScanBroadcastReceiver == null){
            mScanBroadcastReceiver = new ScanBroadcastReceiver(scanCallback);
        }
        //注册蓝牙扫描监听器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mScanBroadcastReceiver, intentFilter);
        BluetoothUtil.enableBluetooth((Activity) mContext, 1);
    }

    private void startPair(){
        if(mPairBroadcastReceiver == null){
            mPairBroadcastReceiver = new PairBroadcastReceiver(pairCallback);
        }
        //注册蓝牙配对监听器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mPairBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                BluetoothAdapter.getDefaultAdapter().startDiscovery();
                LogUtils.i("Start Scan");
                mScanPb.setVisibility(View.VISIBLE);
            } else{
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        if(mScanBroadcastReceiver != null){
            unregisterReceiver(mScanBroadcastReceiver);
            mScanBroadcastReceiver = null;
        }
        if(mPairBroadcastReceiver != null){
            unregisterReceiver(mPairBroadcastReceiver);
            mPairBroadcastReceiver = null;
        }
        super.onDestroy();
    }
}
