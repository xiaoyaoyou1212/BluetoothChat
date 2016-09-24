package com.vise.bluetoothchat.mode;

import com.vise.basebluetooth.mode.BaseMessage;

import java.io.Serializable;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/24 20:58.
 */
public class ChatInfo implements Serializable {

    private int chatId;
    private FriendInfo friendInfo;
    private BaseMessage message;
    private boolean isSend;
    private String sendTime;
    private String receiveTime;

    public int getChatId() {
        return chatId;
    }

    public ChatInfo setChatId(int chatId) {
        this.chatId = chatId;
        return this;
    }

    public FriendInfo getFriendInfo() {
        return friendInfo;
    }

    public ChatInfo setFriendInfo(FriendInfo friendInfo) {
        this.friendInfo = friendInfo;
        return this;
    }

    public BaseMessage getMessage() {
        return message;
    }

    public ChatInfo setMessage(BaseMessage message) {
        this.message = message;
        return this;
    }

    public boolean isSend() {
        return isSend;
    }

    public ChatInfo setSend(boolean send) {
        isSend = send;
        return this;
    }

    public String getSendTime() {
        return sendTime;
    }

    public ChatInfo setSendTime(String sendTime) {
        this.sendTime = sendTime;
        return this;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public ChatInfo setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
        return this;
    }

    @Override
    public String toString() {
        return "ChatInfo{" +
                "chatId=" + chatId +
                ", friendInfo=" + friendInfo +
                ", message=" + message +
                ", isSend=" + isSend +
                ", sendTime='" + sendTime + '\'' +
                ", receiveTime='" + receiveTime + '\'' +
                '}';
    }
}
