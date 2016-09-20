package com.vise.bluetoothchat.adapter;

import android.content.Context;

import com.vise.bluetoothchat.R;
import com.vise.bluetoothchat.mode.FriendInfo;
import com.vise.common_base.adapter.helper.HelperAdapter;
import com.vise.common_base.adapter.helper.HelperViewHolder;

/**
 * @Description: 好友列表适配
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-20 16:16
 */
public class FriendAdapter extends HelperAdapter<FriendInfo> {
    public FriendAdapter(Context context) {
        super(context, R.layout.item_friend_info);
    }

    @Override
    public void HelpConvert(HelperViewHolder viewHolder, int position, FriendInfo friendInfo) {

    }
}
