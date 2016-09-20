package com.vise.bluetoothchat.adapter;

import android.content.Context;

import com.vise.bluetoothchat.R;
import com.vise.bluetoothchat.mode.GroupInfo;
import com.vise.common_base.adapter.helper.HelperAdapter;
import com.vise.common_base.adapter.helper.HelperViewHolder;

/**
 * @Description: 分组列表适配
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-20 17:26
 */
public class GroupAdapter extends HelperAdapter<GroupInfo> {
    public GroupAdapter(Context context) {
        super(context, R.layout.item_group_info);
    }

    @Override
    public void HelpConvert(HelperViewHolder viewHolder, int position, GroupInfo groupInfo) {

    }
}
