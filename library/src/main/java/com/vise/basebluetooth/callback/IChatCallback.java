package com.vise.basebluetooth.callback;

import com.vise.basebluetooth.common.State;

/**
 * @Description: 消息回调
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/18 21:24.
 */
public interface IChatCallback<T> {
    void connectStateChange(State state);
    void writeData(T data, int type);
    void readData(T data, int type);
    void setDeviceName(String name);
    void showMessage(String message, int code);
}
