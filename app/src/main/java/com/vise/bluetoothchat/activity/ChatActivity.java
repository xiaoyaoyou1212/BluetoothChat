package com.vise.bluetoothchat.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.vise.basebluetooth.BluetoothChatHelper;
import com.vise.basebluetooth.CommandHelper;
import com.vise.basebluetooth.callback.IChatCallback;
import com.vise.basebluetooth.common.ChatConstant;
import com.vise.basebluetooth.common.State;
import com.vise.basebluetooth.mode.BaseMessage;
import com.vise.basebluetooth.mode.FileMessage;
import com.vise.basebluetooth.utils.HexUtil;
import com.vise.bluetoothchat.R;
import com.vise.bluetoothchat.adapter.ChatAdapter;
import com.vise.bluetoothchat.common.AppConstant;
import com.vise.bluetoothchat.mode.ChatInfo;
import com.vise.bluetoothchat.mode.FriendInfo;
import com.vise.common_base.utils.ToastUtil;
import com.vise.common_utils.log.LogUtils;
import com.vise.common_utils.utils.character.DateTime;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.qqtheme.framework.picker.FilePicker;
import cn.qqtheme.framework.util.StorageUtils;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/24 16:26.
 */
public class ChatActivity extends BaseChatActivity implements EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        EmojiconGridFragment.OnEmojiconClickedListener {

    private TextView mTitleTv;
    private ListView mChatMsgLv;
    private ImageButton mMsgFaceIb;
    private ImageButton mMsgAddIb;
    private EditText mMsgEditEt;
    private ImageButton mMsgSendIb;
    private FrameLayout mEmojiconFl;
    private ProgressDialog mProgressDialog;
    private ChatAdapter mChatAdapter;
    private FriendInfo mFriendInfo;
    private List<ChatInfo> mChatInfoList = new ArrayList<>();
    private BluetoothChatHelper mBluetoothChatHelper;
    private boolean mIsSendFile = false;
    private File mSendFile;
    private String mFilePath;

    private IChatCallback<byte[]> chatCallback = new IChatCallback<byte[]>() {
        @Override
        public void connectStateChange(State state) {
            LogUtils.i("connectStateChange:"+state.getCode());
            if(state == State.STATE_CONNECTED){
                if (mProgressDialog != null) {
                    mProgressDialog.hide();
                }
                if(mFriendInfo != null){
                    mTitleTv.setText(mFriendInfo.getFriendNickName()+"("+getString(R.string.device_online)+")");
                }
                ToastUtil.showToast(mContext, getString(R.string.connect_friend_success));
            }
        }

        @Override
        public void writeData(byte[] data, int type) {
            if(data == null){
                LogUtils.e("writeData is Null or Empty!");
                return;
            }
            LogUtils.i("writeData:"+HexUtil.encodeHexStr(data));
        }

        @Override
        public void readData(byte[] data, int type) {
            if(data == null){
                LogUtils.e("readData is Null or Empty!");
                return;
            }
            LogUtils.i("readData:"+HexUtil.encodeHexStr(data));
            try {
                BaseMessage message = CommandHelper.unpackData(data);
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setMessage(message);
                chatInfo.setReceiveTime(DateTime.getStringByFormat(new Date(), DateTime.DEFYMDHMS));
                chatInfo.setSend(false);
                chatInfo.setFriendInfo(mFriendInfo);
                mChatInfoList.add(chatInfo);
                mChatAdapter.setListAll(mChatInfoList);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setDeviceName(String name) {
            LogUtils.i("setDeviceName:"+name);
        }

        @Override
        public void showMessage(String message, int code) {
            if (!isFinishing()) {
                return;
            }
            LogUtils.i("showMessage:"+message);
            if (mProgressDialog != null) {
                mProgressDialog.hide();
            }
            ToastUtil.showToast(mContext, getString(R.string.connect_friend_fail));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chat);
    }

    @Override
    protected void initWidget() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitleTv = (TextView) findViewById(R.id.title);
        mChatMsgLv = (ListView) findViewById(R.id.chat_msg_show_list);
        mMsgFaceIb = (ImageButton) findViewById(R.id.chat_msg_face);
        mMsgAddIb = (ImageButton) findViewById(R.id.chat_msg_add);
        mMsgEditEt = (EditText) findViewById(R.id.chat_msg_edit);
        mMsgSendIb = (ImageButton) findViewById(R.id.chat_msg_send);
        mEmojiconFl = (FrameLayout) findViewById(R.id.chat_emojicons);
        mProgressDialog = new ProgressDialog(mContext);
    }

    @Override
    protected void initData() {
        mFriendInfo = this.getIntent().getParcelableExtra(AppConstant.FRIEND_INFO);
        if (mFriendInfo == null) {
            return;
        }
        if(mFriendInfo.isOnline()){
            mTitleTv.setText(mFriendInfo.getFriendNickName()+"("+getString(R.string.device_online)+")");
        } else{
            mTitleTv.setText(mFriendInfo.getFriendNickName()+"("+getString(R.string.device_offline)+")");
        }
        mChatAdapter = new ChatAdapter(mContext);
        mChatMsgLv.setAdapter(mChatAdapter);

        mBluetoothChatHelper = new BluetoothChatHelper(chatCallback);
        mProgressDialog.setMessage(getString(R.string.connect_friend_loading));
        if(!isFinishing() && !mProgressDialog.isShowing()){
            mProgressDialog.show();
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothChatHelper.connect(mFriendInfo.getBluetoothDevice(), false);
            }
        }, 3000);
    }

    @Override
    protected void initEvent() {
        mMsgFaceIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEmojiconFl.getVisibility() == View.GONE){
                    hideSoftInput();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEmojiconFl.setVisibility(View.VISIBLE);
                            setEmojiconFragment(false);
                        }
                    }, 100);
                } else{
                    mEmojiconFl.setVisibility(View.GONE);
                }
            }
        });
        mMsgAddIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilePicker picker = new FilePicker(ChatActivity.this, FilePicker.FILE);
                picker.setShowHideDir(false);
                picker.setRootPath(StorageUtils.getInternalRootPath(ChatActivity.this));
                picker.setOnFilePickListener(new FilePicker.OnFilePickListener() {
                    @Override
                    public void onFilePicked(String currentPath) {
                        mIsSendFile = true;
                        mFilePath = currentPath;
                        mSendFile = new File(mFilePath);
                        mMsgEditEt.setText(mSendFile.getName());
                    }
                });
                picker.show();
            }
        });
        mMsgSendIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMsgEditEt.getText() != null && mMsgEditEt.getText().toString().trim().length() > 0){
                    sendMessage();
                } else{
                    ToastUtil.showToast(mContext, getString(R.string.send_msg_isEmpty));
                }
            }
        });
        mMsgEditEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmojiconFl.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothChatHelper != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mBluetoothChatHelper.getState() == State.STATE_NONE) {
                // Start the Bluetooth chat services
                mBluetoothChatHelper.start(false);
            }
        }
    }

    private void sendMessage() {
        ChatInfo chatInfo = new ChatInfo();
        FriendInfo friendInfo = new FriendInfo();
        friendInfo.setBluetoothDevice(mBluetoothChatHelper.getAdapter().getRemoteDevice(mBluetoothChatHelper.getAdapter().getAddress()));
        friendInfo.setOnline(true);
        friendInfo.setFriendNickName(mBluetoothChatHelper.getAdapter().getName());
        friendInfo.setIdentificationName(mBluetoothChatHelper.getAdapter().getName());
        friendInfo.setDeviceAddress(mBluetoothChatHelper.getAdapter().getAddress());
        chatInfo.setFriendInfo(friendInfo);
        chatInfo.setSend(true);
        chatInfo.setSendTime(DateTime.getStringByFormat(new Date(), DateTime.DEFYMDHMS));
        BaseMessage message = null;
        if(mIsSendFile){
            message = new FileMessage();
            message.setMsgType(ChatConstant.VISE_COMMAND_TYPE_FILE);
            message.setMsgContent(mMsgEditEt.getText().toString());
            message.setMsgLength(mMsgEditEt.getText().length());
            if(mSendFile != null){
                ((FileMessage)message).setFileLength((int) mSendFile.length());
                ((FileMessage)message).setFileName(mSendFile.getName());
            }
            if(mFilePath != null){
                ((FileMessage)message).setFileNameLength(mFilePath.length());
            }
        } else{
            message = new BaseMessage();
            message.setMsgType(ChatConstant.VISE_COMMAND_TYPE_TEXT);
            message.setMsgContent(mMsgEditEt.getText().toString());
            message.setMsgLength(mMsgEditEt.getText().length());
        }
        chatInfo.setMessage(message);
        mChatInfoList.add(chatInfo);
        mChatAdapter.setListAll(mChatInfoList);
        mMsgEditEt.setText("");
        try {
            if(mIsSendFile && mSendFile != null){
                mBluetoothChatHelper.write(CommandHelper.packFile(mSendFile));
                mIsSendFile = false;
                //调用系统程序发送文件
                String uri = "file://" + mFilePath;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("*/*");
                sharingIntent.setComponent(new ComponentName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri));
                startActivityForResult(sharingIntent, 1);
            } else{
                mBluetoothChatHelper.write(CommandHelper.packMsg(message.getMsgContent()));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if(mBluetoothChatHelper != null){
            mBluetoothChatHelper.stop();
            mBluetoothChatHelper = null;
        }
        super.onDestroy();
    }

    private void hideSoftInput(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.chat_emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mMsgEditEt);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mMsgEditEt, emojicon);
    }
}
