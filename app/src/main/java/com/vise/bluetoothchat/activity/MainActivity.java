package com.vise.bluetoothchat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.vise.basebluetooth.BluetoothChatHelper;
import com.vise.basebluetooth.callback.IChatCallback;
import com.vise.basebluetooth.common.State;
import com.vise.basebluetooth.mode.BaseMessage;
import com.vise.basebluetooth.utils.BluetoothUtil;
import com.vise.bluetoothchat.R;
import com.vise.bluetoothchat.adapter.GroupFriendAdapter;
import com.vise.bluetoothchat.mode.FriendInfo;
import com.vise.bluetoothchat.mode.GroupInfo;
import com.vise.common_base.activity.BaseActivity;
import com.vise.common_base.manager.AppManager;
import com.vise.common_base.utils.ToastUtil;
import com.vise.common_utils.log.LogUtils;
import com.vise.common_utils.utils.character.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MainActivity extends BaseActivity
        implements AppCompatCallback,NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog mProgressDialog;
    private ExpandableListView mGroupFriendLv;
    private GroupFriendAdapter mGroupFriendAdapter;
    private List<GroupInfo> mGroupFriendListData = new ArrayList<>();
    private BluetoothChatHelper mBluetoothChatHelper;

    private IChatCallback<BaseMessage> chatCallback = new IChatCallback<BaseMessage>() {
        @Override
        public void connectStateChange(State state) {
            LogUtils.i("connectStateChange:"+state.getCode());
            if(state == State.STATE_CONNECTED){
                mProgressDialog.hide();
                ToastUtil.showToast(mContext, getString(R.string.connect_friend_success));
            }
        }

        @Override
        public void writeData(BaseMessage data, int type) {
            LogUtils.i("writeData:"+data.toString());
        }

        @Override
        public void readData(BaseMessage data, int type) {
            LogUtils.i("readData:"+data.toString());
        }

        @Override
        public void setDeviceName(String name) {
            LogUtils.i("setDeviceName:"+name);
        }

        @Override
        public void showMessage(String message, int code) {
            LogUtils.i("showMessage:"+message);
            mProgressDialog.hide();
            ToastUtil.showToast(mContext, getString(R.string.connect_friend_fail));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddFriendActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mProgressDialog = new ProgressDialog(mContext);
        mGroupFriendLv = (ExpandableListView) findViewById(R.id.friend_group_list);
        initData();
    }

    private void initData() {
        mGroupFriendAdapter = new GroupFriendAdapter(mContext, mGroupFriendListData);
        mGroupFriendLv.setAdapter(mGroupFriendAdapter);
        mGroupFriendLv.expandGroup(0);
        mGroupFriendLv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                mProgressDialog.setMessage(getString(R.string.connect_friend_loading));
                mProgressDialog.show();
                mBluetoothChatHelper.connect(mGroupFriendListData.get(groupPosition).getFriendList().get(childPosition).getBluetoothDevice(), false);
                return true;
            }
        });

        mBluetoothChatHelper = new BluetoothChatHelper(chatCallback);
        if(BluetoothUtil.isSupportBle(mContext)){
            BluetoothUtil.enableBluetooth((Activity) mContext, 1);
        } else{
            ToastUtil.showToast(mContext, getString(R.string.phone_not_support_bluetooth));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppManager.getAppManager().appExit(mContext);
                }
            }, 3000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                findDevice();
            } else{
                AppManager.getAppManager().appExit(mContext);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestart() {
        if(BluetoothUtil.isSupportBle(mContext)){
            BluetoothUtil.enableBluetooth((Activity) mContext, 1);
        } else{
            ToastUtil.showToast(mContext, getString(R.string.phone_not_support_bluetooth));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppManager.getAppManager().appExit(mContext);
                }
            }, 3000);
        }
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_about) {
            displayAboutDialog();
            return true;
        } else if(id == R.id.menu_share){
            ToastUtil.showToast(mContext, getString(R.string.menu_share));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        AppCompatDelegate.create(this, this).setSupportActionBar(toolbar);
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    private void displayAboutDialog() {
        final int paddingSizeDp = 5;
        final float scale = getResources().getDisplayMetrics().density;
        final int dpAsPixels = (int) (paddingSizeDp * scale + 0.5f);

        final TextView textView = new TextView(this);
        final SpannableString text = new SpannableString(getString(R.string.about_dialog_text));

        textView.setText(text);
        textView.setAutoLinkMask(RESULT_OK);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

        Linkify.addLinks(text, Linkify.ALL);
        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_about)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null)
                .setView(textView)
                .show();
    }

    private void findDevice(){
        // 获得已经保存的配对设备
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (pairedDevices.size() > 0) {
            mGroupFriendListData.clear();
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setGroupName(BluetoothAdapter.getDefaultAdapter().getName());
            List<FriendInfo> friendInfoList = new ArrayList<>();
            for (BluetoothDevice device : pairedDevices) {
                FriendInfo friendInfo = new FriendInfo();
                friendInfo.setIdentificationName(device.getName());
                friendInfo.setDeviceAddress(device.getAddress());
                friendInfo.setFriendNickName(device.getName());
                friendInfo.setOnline(false);
                friendInfo.setJoinTime(DateTime.getStringByFormat(new Date(), DateTime.DEFYMDHMS));
                friendInfo.setBluetoothDevice(device);
                friendInfoList.add(friendInfo);
            }
            groupInfo.setFriendList(friendInfoList);
            groupInfo.setOnlineNumber(0);
            mGroupFriendListData.add(groupInfo);
            mGroupFriendAdapter.setGroupInfoList(mGroupFriendListData);
        }
        mBluetoothChatHelper.start(false);
    }

}
