package com.zc.server.posp.common;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.CRC32;

/**
 * 加密解密工具类
 *
 * @author zing
 * @create 2018/1/18 18:40
 */
public final class DigestUtil {
    private static final String SHA_256 = "sha-256";
    private static final String SHA_512 = "sha-512";
    private static final String AES = "AES";
    private static final String AES_CIPHER = "BC";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
    private static DigestUtil du = new DigestUtil();
    private final ByteTrans trans = ByteTrans.newInstance();
    private final ByteUtil byteUtil = ByteUtil.newInstance();
    /**
     * 加解密
     */
    private final Logger logger = LoggerFactory.getLogger(DigestUtil.class);

    private DigestUtil() {
    }

    public static DigestUtil newInstance() {
        return du;
    }

    /**
     * 生成MD5 摘要
     *
     * @param source 源数据
     * @return MD5摘要
     */
    public Optional<String> md5(String source) {
        String result = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(source.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            result = new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return Optional.of(result);
    }

    /**
     * AES 加密
     *
     * @param source   源
     * @param password 密码
     * @return 加密结果
     */
    public byte[] encryptAES(byte[] source, byte[] password) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            SecretKeySpec key = new SecretKeySpec(password, AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, AES_CIPHER);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, key, generateIV());
            byte[] result = cipher.doFinal(source);
            // 加密
            return result;
        } catch (Exception e) {
            logger.error("AES 加密失败！", e);
        }
        return null;
    }

    /**
     * AES 解密
     *
     * @param secret   密文
     * @param password 密码
     * @return 解密结果
     */
    public byte[] decryptAES(byte[] secret, byte[] password) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            SecretKeySpec key = new SecretKeySpec(password, AES);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, key, generateIV());
            byte[] result = cipher.doFinal(secret);
            // 解密
            return result;
        } catch (Exception e) {
            logger.error("解密失败！", e);
        }
        return null;
    }

    /**
     * 获取填充的IV
     *
     * @return 填充结果
     * @throws Exception
     */
    private AlgorithmParameters generateIV() throws Exception {
        byte[] iv = new byte[16];
        Arrays.fill(iv, (byte) 0x00);
        AlgorithmParameters params = AlgorithmParameters.getInstance(AES);
        params.init(new IvParameterSpec(iv));
        return params;
    }


    /**
     * CRC校验值
     *
     * @param header 校验原文
     * @return 校验码
     */
    public long getCRC(byte[] header) {
        CRC32 crc32 = new CRC32();
        crc32.update(header);
        return crc32.getValue();
    }

    /**
     * pkcs7 填充
     *
     * @param pwd    密码
     * @param length 长度
     * @return 填充结果
     */
    public byte[] pkcs7Padding(String pwd, int length) {
        byte[] ascii_bin = trans.transAscii(pwd);
        int mode = length - ascii_bin.length % length;
        for (int i = 0; i < mode; i++) {
            ascii_bin = byteUtil.concatAll(ascii_bin, trans.transHex(String.format("%02x", mode)));
        }
        return ascii_bin;
    }


    /**
     * 传入文本内容，返回 sha-256 串
     *
     * @param strText 内容
     * @return 结果
     */
    public Optional<String> sha256(final String strText) {
        return sha(strText, SHA_256);
    }

    /**
     * 传入文本内容，返回 sha-256 串
     *
     * @param strText 内容
     * @return 结果
     */
    public byte[] sha256Byte(final String strText) {
        return shaByte(strText, SHA_256);
    }

    /**
     * 传入文本内容，返回 sha-512 串
     *
     * @param strText 内容
     * @return 结果
     */
    public Optional<String> sha512(final String strText) {
        return sha(strText, SHA_512);
    }

    /**
     * 传入文本内容，返回 sha-512 串
     *
     * @param strText 内容
     * @return 结果
     */
    public byte[] sha512Byte(final String strText) {
        return shaByte(strText, SHA_512);
    }


    /**
     * 字符串 sha 加密
     *
     * @param strText 数据源
     * @param strType 加密类型
     * @return 加密结果
     */
    private Optional<String> sha(final String strText, final String strType) {
        // 返回值
        String strResult = null;
        byte[] byteBuffer = shaByte(strText, strType);
        if (null != byteBuffer && 0 < byteBuffer.length) {

            // 將 byte 轉換爲 string
            StringBuffer strHexString = new StringBuffer();
            // 遍歷 byte buffer
            for (int i = 0; i < byteBuffer.length; i++) {
                String hex = Integer.toHexString(0xff & byteBuffer[i]);
                // 奇数需要前面补0
                if (Validator.isOddNumber(hex.length())) {
                    strHexString.append('0');
                }
                strHexString.append(hex);
            }
            // 得到返回結果
            strResult = strHexString.toString();
        }

        return Optional.of(strResult);
    }

    /**
     * sha 加密返回byte数组
     *
     * @param strText 数据源
     * @param strType 加密类型
     * @return 结果
     */
    private byte[] shaByte(final String strText, final String strType) {
        // 是否是有效字符串
        if (strText != null && strText.length() > 0) {
            try {
                // sha 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                messageDigest.update(strText.getBytes());
                // 得到 byte 類型结果
                return messageDigest.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


}
