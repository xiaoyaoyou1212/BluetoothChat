package com.vise.bluetoothchat.adapter;

import android.content.Context;
import android.widget.TextView;

import com.vise.bluetoothchat.R;
import com.vise.bluetoothchat.mode.ChatInfo;
import com.vise.common_base.adapter.helper.HelperAdapter;
import com.vise.common_base.adapter.helper.HelperViewHolder;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/24 21:03.
 */
public class ChatAdapter extends HelperAdapter<ChatInfo> {
    public ChatAdapter(Context context) {
        super(context, R.layout.item_chat_info_left, R.layout.item_chat_info_right);
    }

    @Override
    public void HelpConvert(HelperViewHolder viewHolder, int position, ChatInfo chatInfo) {
        if (chatInfo == null) {
            return;
        }
        TextView timeTv;
        TextView msgTv;
        if(chatInfo.isSend()){
            timeTv = viewHolder.getView(R.id.item_chat_right_time);
            msgTv = viewHolder.getView(R.id.item_chat_right_msg);
            if(chatInfo.getMessage() != null) {
                timeTv.setText(chatInfo.getSendTime());
                msgTv.setText(chatInfo.getMessage().getMsgContent());
            }
        } else{
            timeTv = viewHolder.getView(R.id.item_chat_left_time);
            msgTv = viewHolder.getView(R.id.item_chat_left_msg);
            if(chatInfo.getMessage() != null) {
                timeTv.setText(chatInfo.getReceiveTime());
                msgTv.setText(chatInfo.getMessage().getMsgContent());
            }
        }
    }

    @Override
    public int checkLayout(int position, ChatInfo item) {
        if(item != null && item.isSend()){
            return 1;
        }
        return 0;
    }
}
