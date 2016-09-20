package com.vise.bluetoothchat.mode;

import java.io.Serializable;

/**
 * @Description: 好友信息
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-20 16:16
 */
public class FriendInfo implements Serializable {

    private int friendId;//ID
    private String friendNickName;//昵称
    private String friendIconUrl;//头像地址
    private String deviceAddress;//设备地址
    private String identificationName;//标识名称，即设备名称
    private String joinTime;//加入时间
    private boolean isOnline;//是否在线

    public int getFriendId() {
        return friendId;
    }

    public FriendInfo setFriendId(int friendId) {
        this.friendId = friendId;
        return this;
    }

    public String getFriendNickName() {
        return friendNickName;
    }

    public FriendInfo setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
        return this;
    }

    public String getFriendIconUrl() {
        return friendIconUrl;
    }

    public FriendInfo setFriendIconUrl(String friendIconUrl) {
        this.friendIconUrl = friendIconUrl;
        return this;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public FriendInfo setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
        return this;
    }

    public String getIdentificationName() {
        return identificationName;
    }

    public FriendInfo setIdentificationName(String identificationName) {
        this.identificationName = identificationName;
        return this;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public FriendInfo setJoinTime(String joinTime) {
        this.joinTime = joinTime;
        return this;
    }

    @Override
    public String toString() {
        return "FriendInfo{" +
                "friendId=" + friendId +
                ", friendNickName='" + friendNickName + '\'' +
                ", friendIconUrl='" + friendIconUrl + '\'' +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", identificationName='" + identificationName + '\'' +
                ", joinTime='" + joinTime + '\'' +
                '}';
    }
}
