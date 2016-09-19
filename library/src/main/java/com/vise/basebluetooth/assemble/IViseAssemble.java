package com.vise.basebluetooth.assemble;

/**
 * @Description:
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2016-09-19 15:41
 */
public interface IViseAssemble extends IBaseAssemble {
    /*设置数据长度*/
    void setDataLength(byte[] dataLength);

    /*设置协议版本*/
    void setProtocolVersion(byte protocolVersion);

    /*设置发送类型*/
    void setCommandType(byte commandType);
}
