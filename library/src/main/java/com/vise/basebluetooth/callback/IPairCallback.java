package com.vise.basebluetooth.callback;

/**
 * @Description: 配对回调
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2016-09-20 13:38
 */
public interface IPairCallback {
    void unBonded();
    void bonding();
    void bonded();
    void bondFail();
}
