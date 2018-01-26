package com.zc.server.posp.common;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;


/**
 * Byte 转换工具类
 *
 * @author zing
 * @create 2018/1/17 20:20
 */
public final class ByteTrans {

    private static ByteTrans trans = new ByteTrans();
    private Logger logger = LoggerFactory.getLogger(ByteTrans.class);

    private ByteTrans() {
    }

    public static ByteTrans newInstance() {
        return trans;
    }

    /**
     * byte 转 ASCII
     *
     * @param bytes 源
     * @return
     */
    public String toAscii(byte[] bytes) {
        if (ArrayUtils.isEmpty(bytes)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append((char) bytes[i]);
        }
        return sb.toString();
    }

    /**
     * 转BCD
     *
     * @param bytes 源
     * @return
     */
    public String toBcd(byte[] bytes) {
        if (ArrayUtils.isEmpty(bytes)) {
            return "";
        } else {
            return String.valueOf(Hex.encodeHex(bytes));
        }
    }

    /**
     * 转大写HEX
     *
     * @param bytes
     * @return
     */
    public String toHex(byte[] bytes) {
        if (ArrayUtils.isEmpty(bytes)) {
            return "";
        }
        return new String(Hex.encodeHex(bytes, false));
    }

    /**
     * 4个字节的byte转无符号整型
     *
     * @param bytes 源
     * @return 结果
     */
    public long toLong(byte[] bytes) {
        if (ArrayUtils.isEmpty(bytes) || bytes.length < 4) {
            logger.error("data is empty or too long!");
            return 0;
        }
        int firstByte = 0, secondByte = 0, thirdByte = 0, fourthByte = 0;
        int index = 0;
        firstByte = (0x000000FF & ((int) bytes[index]));
        secondByte = (0x000000FF & ((int) bytes[index + 1]));
        thirdByte = (0x000000FF & ((int) bytes[index + 2]));
        fourthByte = (0x000000FF & ((int) bytes[index + 3]));
        return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
    }

    /**
     * 将数据转换成GBK字符串
     *
     * @param gbk gbk byte数组
     * @return
     */
    public String toGBK(byte[] gbk) {
        try {
            return new String(gbk, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 转换成 ASCII byte
     *
     * @param ascii
     * @return
     */
    public byte[] transAscii(String ascii) {
        if (StringUtils.isBlank(ascii)) {
            return null;
        }
        char[] charArr = ascii.toCharArray();
        int len = charArr.length;
        byte[] result = new byte[len];
        int index = 0;
        for (char c : charArr) {
            if (c > 0XFF) {
                logger.error("transform unsupported Ascii:" + c);
                result[index] = 0x00;
            } else {
                result[index] = (byte) c;
            }
            index++;
        }
        return result;
    }

    /**
     * 将 HEX字符串转换成 byte数组
     *
     * @param hex
     * @return
     */
    public byte[] transHex(String hex) {
        if (StringUtils.isEmpty(hex)) {
            logger.error("transHex input an empty data!");
            return new byte[]{0};
        } else if (Validator.isOddNumber(hex.length())) {
            hex = "0" + hex;
        }
        try {
            return Hex.decodeHex(hex.toCharArray());
        } catch (DecoderException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 将BCD字符串转换成BCD byte数组
     *
     * @param bcd BCD字符串
     * @return 结果
     */
    public byte[] transBcd(String bcd) {
        if (Validator.isNumber(bcd)) {
            return transHex(bcd);
        } else {
            return null;
        }
    }

    /**
     * 将一个无符号整型转换成byte数组
     *
     * @param unsignedInteger 无符号整型
     * @return 结果
     */
    public byte[] transLong(long unsignedInteger) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (unsignedInteger >> 24);
        bytes[1] = (byte) ((unsignedInteger >> 16));
        bytes[2] = (byte) ((unsignedInteger >> 8));
        bytes[3] = (byte) ((unsignedInteger >> 0));
        return bytes;
    }

    /**
     * 转换成GBK byte
     *
     * @param data 数据源
     * @return GBK byte数组
     */
    private byte[] transGBK(String data) {
        try {
            return data.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
