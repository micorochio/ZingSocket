package com.zc.server.socket.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author zing
 * @create 2018/1/18 19:06
 */

public final class ByteUtil {

    private static ByteUtil byteUtil = new ByteUtil();
    private Logger logger = LoggerFactory.getLogger(ByteUtil.class);

    private ByteUtil() {
    }

    public static ByteUtil newInstance() {
        return byteUtil;
    }

    /**
     * 连接所有byte 数组
     *
     * @param rest
     * @return
     */
    public byte[] concatAll(byte[]... rest) {

        return concatAll(Arrays.asList(rest));
    }

    /**
     * 连接所有byte 数组
     *
     * @param rest
     * @return
     */
    public byte[] concatAll(List<byte[]> rest) {
        int totalLength = 0;

        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = new byte[totalLength];
        int offset = 0;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * 将数组拉伸到指定长度，数据在末端，首位填充prefix
     *
     * @param source 源数据
     * @param length 长度
     * @param prefix 长度
     * @return 结果
     */
    public byte[] stretchToSpecifyLen(byte[] source, int length, byte prefix) {
        if (length < source.length) {
            logger.warn("Source is longer than {} to Stretch", length);
            return source;
        }
        byte[] result = new byte[length];

        length -= 1;
        for (int i = source.length - 1; i >= 0; i--, length--) {
            result[length] = source[i];
        }

        for (; length >= 0; length--) {
            result[length] = prefix;
        }
        return result;
    }

}
