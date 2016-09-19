package com.vise.basebluetooth.common;

/**
 * @Description: 状态
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/18 20:37.
 */
public enum State {

    STATE_NONE(0),
    STATE_LISTEN(1),
    STATE_CONNECTING(2),
    STATE_CONNECTED(3);

    private int code;

    State(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
