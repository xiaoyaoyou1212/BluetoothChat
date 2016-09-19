package com.vise.basebluetooth.utils;

import java.nio.ByteBuffer;

/**
 * @Description: 转换工具类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/8/16 21:41.
 */
public class ConvertUtil {

    /**
     * 检查某个字节数组是不是以一个字节数组开头
     *
     * @param array  the array
     * @param prefix the prefix
     * @return true, if successful
     */
    public static boolean doesArrayBeginWith(final byte[] array, final byte[] prefix) {
        if (array.length < prefix.length) {
            return false;
        }

        for (int i = 0; i < prefix.length; i++) {
            if (array[i] != prefix[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将两个字节转换成整型
     *
     * @param input the input
     * @return the int from the array
     */
    public static int getIntFrom2ByteArray(final byte[] input) {
        final byte[] result = new byte[4];

        result[0] = 0;
        result[1] = 0;
        result[2] = input[0];
        result[3] = input[1];

        return getIntFromByteArray(result);
    }

    /**
     * 将一个字节转换成整型
     * <p>
     * For example, FF will be converted to 255 and not -1.
     *
     * @param bite the byte
     * @return the int from byte
     */
    public static int getIntFromByte(final byte bite) {
        return bite & 0xFF;
    }

    /**
     * 将字节数组转换成整型
     *
     * @param bytes the bytes
     * @return the int from byte array
     */
    public static int getIntFromByteArray(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * 将字节数组转换成长整型
     *
     * @param bytes the bytes
     * @return the long from byte array
     */
    public static long getLongFromByteArray(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }


    /**
     * 将字节数组前后倒置
     *
     * @param array the array
     */
    public static void invertArray(final byte[] array) {
        final int size = array.length;
        byte temp;

        for (int i = 0; i < size / 2; i++) {
            temp = array[i];
            array[i] = array[size - 1 - i];
            array[size - 1 - i] = temp;
        }
    }

    /**
     * int数值转换为byte数组，高位在前
     * @param value
     * @param n
     * @return
     */
    public static byte[] intToBytesHigh(int value, int n){
        byte[] src = new byte[n];
        for(int i = 0; i < n; i++){
            src[i] = (byte) ((value >> (8 * (n - i - 1))) & 0xFF);
        }
        return src;
    }

    /**
     * int数值转换为byte数组，低位在前
     * @param value
     * @param n
     * @return
     */
    public static byte[] intToBytesLow(int value, int n){
        byte[] src = new byte[n];
        for(int i = 0; i < n; i++){
            src[i] = (byte) ((value >> (8 * i)) & 0xFF);
        }
        return src;
    }

    /**
     * byte数组转换为int，高位在前
     * @param bytes
     * @param offset
     * @return
     */
    public static int bytesToIntHigh(byte[] bytes, int offset){
        int value = 0;
        if(bytes == null || bytes.length == 0){
            return value;
        }
        for(int i = 0; i < bytes.length; i++){
            value += (int) ((bytes[i] & 0xFF) << (8 * (bytes.length - i - 1)));
        }
        return value;
    }

    /**
     * byte数组转换为int，低位在前
     * @param bytes
     * @param offset
     * @return
     */
    public static int bytesToIntLow(byte[] bytes, int offset){
        int value = 0;
        if(bytes == null || bytes.length == 0){
            return value;
        }
        for(int i = 0; i < bytes.length; i++){
            value += (int) ((bytes[i] & 0xFF) << (8 * i));
        }
        return value;
    }

    /**
     * ASCII字符串转换成16进制
     * @param str
     * @return
     */
    public static String convertStringToHex(String str){
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }
        return hex.toString();
    }

    /**
     * 16进制转换成ASCII字符串
     * @param hex
     * @return
     */
    public static String convertHexToString(String hex){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < hex.length() - 1; i += 2){
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char)decimal);
        }
        return sb.toString();
    }
}
