package com.vise.bluetoothchat.mode;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 分组信息
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-20 17:04
 */
public class GroupInfo implements Serializable {

    private int groupId;//ID
    private int onlineNumber;//在线人数
    private String groupName;//名称
    private List<FriendInfo> friendList;//该组好友列表

    public int getGroupId() {
        return groupId;
    }

    public GroupInfo setGroupId(int groupId) {
        this.groupId = groupId;
        return this;
    }

    public int getOnlineNumber() {
        return onlineNumber;
    }

    public GroupInfo setOnlineNumber(int onlineNumber) {
        this.onlineNumber = onlineNumber;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public GroupInfo setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public List<FriendInfo> getFriendList() {
        return friendList;
    }

    public GroupInfo setFriendList(List<FriendInfo> friendList) {
        this.friendList = friendList;
        return this;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "groupId=" + groupId +
                ", onlineNumber=" + onlineNumber +
                ", groupName='" + groupName + '\'' +
                ", friendList=" + friendList +
                '}';
    }
}
