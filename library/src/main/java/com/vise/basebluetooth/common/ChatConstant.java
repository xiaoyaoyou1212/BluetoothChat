package com.vise.basebluetooth.common;

import java.util.UUID;

/**
 * @Description: 常量
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/18 20:36.
 */
public class ChatConstant {

    //Start flag
    public static final byte VISE_COMMAND_START_FLAG = (byte) 0xFF;
    //Protocol version
    public static final byte VISE_COMMAND_PROTOCOL_VERSION = (byte) 0x01;

    /*Send Command Type*/
    public static final byte VISE_COMMAND_TYPE_NONE = (byte) 0x00;
    public static final byte VISE_COMMAND_TYPE_TEXT = (byte) 0x01;
    public static final byte VISE_COMMAND_TYPE_FILE = (byte) 0x02;
    public static final byte VISE_COMMAND_TYPE_IMAGE = (byte) 0x03;
    public static final byte VISE_COMMAND_TYPE_AUDIO = (byte) 0x04;
    public static final byte VISE_COMMAND_TYPE_VIDEO = (byte) 0x05;

    /*KEY*/
    public static final String NAME_SECURE = "BluetoothChatSecure";
    public static final String NAME_INSECURE = "BluetoothChatInsecure";

    /*UUID*/
    public static final UUID UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    /*Message Type*/
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
}
