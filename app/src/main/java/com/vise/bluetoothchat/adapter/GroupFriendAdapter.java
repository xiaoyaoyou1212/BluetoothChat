package com.vise.bluetoothchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vise.bluetoothchat.R;
import com.vise.bluetoothchat.mode.FriendInfo;
import com.vise.bluetoothchat.mode.GroupInfo;

import java.util.List;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-21 16:50
 */
public class GroupFriendAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<GroupInfo> groupInfoList;

    public GroupFriendAdapter(Context context, List<GroupInfo> groupInfoList) {
        this.context = context;
        this.groupInfoList = groupInfoList;
    }

    @Override
    public int getGroupCount() {
        if (groupInfoList == null || groupInfoList.size() == 0) {
            return 0;
        }
        return groupInfoList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupInfoList == null || groupInfoList.size() == 0) {
            return 0;
        }
        return groupInfoList.get(groupPosition).getFriendList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (groupInfoList == null || groupInfoList.size() == 0) {
            return null;
        }
        return groupInfoList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupInfoList == null || groupInfoList.size() == 0) {
            return null;
        }
        return groupInfoList.get(groupPosition).getFriendList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (groupInfoList == null || groupInfoList.get(groupPosition) == null) {
            return null;
        }
        GroupViewHolder groupViewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_info, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.nameTv = (TextView) convertView.findViewById(R.id.item_group_name);
            groupViewHolder.iconIv = (ImageView) convertView.findViewById(R.id.item_group_icon);
            convertView.setTag(groupViewHolder);
        } else{
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        GroupInfo groupInfo = groupInfoList.get(groupPosition);
        List<FriendInfo> friendInfoList = groupInfo.getFriendList();
        if(friendInfoList != null && friendInfoList.size() > 0){
            groupViewHolder.nameTv.setText(groupInfo.getGroupName() +"("+groupInfo.getOnlineNumber()+"/" +groupInfo.getFriendList().size()+")");
        } else{
            groupViewHolder.nameTv.setText(groupInfo.getGroupName()+"(0/0)");
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (groupInfoList == null || groupInfoList.get(groupPosition) == null
                || groupInfoList.get(groupPosition).getFriendList() == null
                || groupInfoList.get(groupPosition).getFriendList().get(childPosition) == null) {
            return null;
        }
        FriendViewHolder friendViewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend_info, null);
            friendViewHolder = new FriendViewHolder();
            friendViewHolder.iconIv = (ImageView) convertView.findViewById(R.id.item_friend_icon);
            friendViewHolder.nameTv = (TextView) convertView.findViewById(R.id.item_friend_name);
            friendViewHolder.addressTv = (TextView) convertView.findViewById(R.id.item_friend_address);
            friendViewHolder.statusTv = (TextView) convertView.findViewById(R.id.item_friend_status);
            convertView.setTag(friendViewHolder);
        } else{
            friendViewHolder = (FriendViewHolder) convertView.getTag();
        }
        FriendInfo friendInfo = groupInfoList.get(groupPosition).getFriendList().get(childPosition);
        friendViewHolder.nameTv.setText(friendInfo.getFriendNickName()+"("+friendInfo.getIdentificationName()+")");
        friendViewHolder.addressTv.setText(friendInfo.getDeviceAddress());
        if(friendInfo.isOnline()){
            friendViewHolder.statusTv.setText(context.getString(R.string.device_online));
        } else{
            friendViewHolder.statusTv.setText(context.getString(R.string.device_offline));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setGroupInfoList(List<GroupInfo> groupInfoList){
        this.groupInfoList = groupInfoList;
        this.notifyDataSetChanged();
    }

    class GroupViewHolder{
        TextView nameTv;
        ImageView iconIv;
    }

    class FriendViewHolder{
        ImageView iconIv;
        TextView nameTv;
        TextView addressTv;
        TextView statusTv;
    }
}
