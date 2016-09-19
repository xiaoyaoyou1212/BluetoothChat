package com.vise.basebluetooth.assemble;

import com.vise.basebluetooth.common.ChatConstant;
import com.vise.basebluetooth.utils.BleLog;
import com.vise.basebluetooth.utils.CRCUtil;
import com.vise.basebluetooth.utils.ConvertUtil;
import com.vise.basebluetooth.utils.HexUtil;

import java.nio.ByteBuffer;

/**
 * @Description: 针对消息发送的命令组装
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-09-19 15:43
 */
public class ViseAssemble extends BaseAssemble implements IViseAssemble {
    private byte startFlag = ChatConstant.VISE_COMMAND_START_FLAG;
    private byte[] dataLength;
    private byte protocolVersion = ChatConstant.VISE_COMMAND_PROTOCOL_VERSION;
    private byte commandType;

    @Override
    public void setDataLength(byte[] dataLength) {
        this.dataLength = dataLength;
    }

    @Override
    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public void setCommandType(byte commandType) {
        this.commandType = commandType;
    }

    @Override
    public byte[] assembleCommand() {
        int length = 0;
        if(data != null){
            length = 6 + data.length;
            dataLength = ConvertUtil.intToBytesHigh(data.length, 2);
        } else{
            length = 6;
            dataLength = ConvertUtil.intToBytesHigh(0, 2);
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(startFlag);
        buffer.put(dataLength);
        buffer.put(protocolVersion);
        buffer.put(commandType);
        if(data != null){
            buffer.put(data);
        } else{
            buffer.put(new byte[0]);
        }
        byte[] checkData = new byte[length - 2];
        System.arraycopy(buffer.array(), 1, checkData, 0, length - 2);
        checkCode = CRCUtil.calcCrc8(checkData);
        buffer.put(checkCode);
        BleLog.i("send packet:"+ HexUtil.encodeHexStr(buffer.array()));
        return buffer.array();
    }

    public static class Builder{

        private IViseAssemble viseAssemble;

        public Builder() {
            this.viseAssemble = new ViseAssemble();
        }

        public Builder setProtocolVersion(byte protocolVersion) {
            this.viseAssemble.setProtocolVersion(protocolVersion);
            return this;
        }

        public Builder setCommandFlag(byte commandType) {
            this.viseAssemble.setCommandType(commandType);
            return this;
        }

        public Builder setData(byte[] data) {
            this.viseAssemble.setData(data);
            return this;
        }

        public byte[] assembleCommand() {
            return this.viseAssemble.assembleCommand();
        }

    }
}
