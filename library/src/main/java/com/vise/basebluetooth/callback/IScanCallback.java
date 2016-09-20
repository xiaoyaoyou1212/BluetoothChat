package com.vise.basebluetooth.callback;

import java.util.List;

/**
 * @Description: 扫描回调
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-13 18:50
 */
public interface IScanCallback<T> {
    void discoverDevice(T t);
    void scanTimeout();
    void scanFinish(List<T> tList);
}
