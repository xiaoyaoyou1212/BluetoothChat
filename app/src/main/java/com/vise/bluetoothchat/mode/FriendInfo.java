package com.vise.bluetoothchat.mode;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @Description: 好友信息
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-20 16:16
 */
public class FriendInfo implements Parcelable {

    private int friendId;//ID
    private String friendNickName;//昵称
    private String friendIconUrl;//头像地址
    private String deviceAddress;//设备地址
    private String identificationName;//标识名称，即设备名称
    private String joinTime;//加入时间
    private boolean isOnline;//是否在线
    private BluetoothDevice bluetoothDevice;

    public FriendInfo() {
    }

    protected FriendInfo(Parcel in) {
        friendId = in.readInt();
        friendNickName = in.readString();
        friendIconUrl = in.readString();
        deviceAddress = in.readString();
        identificationName = in.readString();
        joinTime = in.readString();
        isOnline = in.readByte() != 0;
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public static final Creator<FriendInfo> CREATOR = new Creator<FriendInfo>() {
        @Override
        public FriendInfo createFromParcel(Parcel in) {
            return new FriendInfo(in);
        }

        @Override
        public FriendInfo[] newArray(int size) {
            return new FriendInfo[size];
        }
    };

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

    public boolean isOnline() {
        return isOnline;
    }

    public FriendInfo setOnline(boolean online) {
        isOnline = online;
        return this;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public FriendInfo setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
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
                ", isOnline=" + isOnline +
                ", bluetoothDevice=" + bluetoothDevice +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(friendId);
        parcel.writeString(friendNickName);
        parcel.writeString(friendIconUrl);
        parcel.writeString(deviceAddress);
        parcel.writeString(identificationName);
        parcel.writeString(joinTime);
        parcel.writeByte((byte) (isOnline ? 1 : 0));
        parcel.writeParcelable(bluetoothDevice, i);
    }
}
