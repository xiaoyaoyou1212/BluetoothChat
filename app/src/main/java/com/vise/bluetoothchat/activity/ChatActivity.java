package com.vise.bluetoothchat.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.vise.basebluetooth.BluetoothChatHelper;
import com.vise.basebluetooth.CommandHelper;
import com.vise.basebluetooth.callback.IChatCallback;
import com.vise.basebluetooth.common.ChatConstant;
import com.vise.basebluetooth.common.State;
import com.vise.basebluetooth.mode.BaseMessage;
import com.vise.basebluetooth.utils.HexUtil;
import com.vise.bluetoothchat.R;
import com.vise.bluetoothchat.adapter.ChatAdapter;
import com.vise.bluetoothchat.common.AppConstant;
import com.vise.bluetoothchat.mode.ChatInfo;
import com.vise.bluetoothchat.mode.FriendInfo;
import com.vise.common_base.utils.ToastUtil;
import com.vise.common_utils.log.LogUtils;
import com.vise.common_utils.utils.character.DateTime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/24 16:26.
 */
public class ChatActivity extends BaseChatActivity {

    private TextView mTitleTv;
    private ListView mChatMsgLv;
    private ImageButton mMsgAddIb;
    private EditText mMsgEditEt;
    private ImageButton mMsgSendIb;
    private ProgressDialog mProgressDialog;
    private ChatAdapter mChatAdapter;
    private FriendInfo mFriendInfo;
    private List<ChatInfo> mChatInfoList = new ArrayList<>();
    private BluetoothChatHelper mBluetoothChatHelper;

    private IChatCallback<byte[]> chatCallback = new IChatCallback<byte[]>() {
        @Override
        public void connectStateChange(State state) {
            LogUtils.i("connectStateChange:"+state.getCode());
            if(state == State.STATE_CONNECTED){
                mProgressDialog.hide();
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
            LogUtils.i("showMessage:"+message);
            mProgressDialog.hide();
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
        mMsgAddIb = (ImageButton) findViewById(R.id.chat_msg_add);
        mMsgEditEt = (EditText) findViewById(R.id.chat_msg_edit);
        mMsgSendIb = (ImageButton) findViewById(R.id.chat_msg_send);
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
        mProgressDialog.show();
        mBluetoothChatHelper.connect(mFriendInfo.getBluetoothDevice(), false);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothChatHelper.start(false);
            }
        }, 3000);
    }

    @Override
    protected void initEvent() {
        mMsgAddIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
    }

    private void sendMessage() {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setFriendInfo(mFriendInfo);
        chatInfo.setSend(true);
        chatInfo.setSendTime(DateTime.getStringByFormat(new Date(), DateTime.DEFYMDHMS));
        BaseMessage message = new BaseMessage();
        message.setMsgType(ChatConstant.VISE_COMMAND_TYPE_TEXT);
        message.setMsgContent(mMsgEditEt.getText().toString());
        message.setMsgLength(mMsgEditEt.getText().length());
        chatInfo.setMessage(message);
        mChatInfoList.add(chatInfo);
        mChatAdapter.setListAll(mChatInfoList);
        mMsgEditEt.setText("");
        try {
            mBluetoothChatHelper.write(CommandHelper.packMsg(message.getMsgContent()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
