package com.vise.basebluetooth.assemble;

/**
 * @Description: 基础组装接口
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2016-09-19 15:39
 */
public interface IBaseAssemble {
    /*设置起始标志*/
    void setStartFlag(byte startFlag);

    /*设置数据*/
    void setData(byte[] data);

    /*设置校验码*/
    void setCheckCode(byte checkCode);

    /*组装命令*/
    byte[] assembleCommand();
}
