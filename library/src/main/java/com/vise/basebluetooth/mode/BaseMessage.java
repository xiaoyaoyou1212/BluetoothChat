package com.vise.basebluetooth.mode;

import java.io.Serializable;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-19 14:44
 */
public class BaseMessage implements Serializable {

    private byte msgType;
    private String msgContent;
    private long msgLength;

    public BaseMessage() {
    }

    public byte getMsgType() {
        return msgType;
    }

    public BaseMessage setMsgType(byte msgType) {
        this.msgType = msgType;
        return this;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public BaseMessage setMsgContent(String msgContent) {
        this.msgContent = msgContent;
        return this;
    }

    public long getMsgLength() {
        return msgLength;
    }

    public BaseMessage setMsgLength(long msgLength) {
        this.msgLength = msgLength;
        return this;
    }
}
