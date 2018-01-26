package com.zc.server.posp.base.annotation.transfer;

import com.zc.server.posp.base.annotation.MessageSlice;
import com.zc.server.posp.base.exceptions.POSPRunTimeException;
import com.zc.server.posp.common.ByteTrans;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zing
 * @create 2018/1/19 15:56
 */
public class DataConverter<T> {

    Logger logger = LoggerFactory.getLogger(DataConverter.class);
    /**
     * 转码器
     */
    ByteTrans trans = ByteTrans.newInstance();

    /**
     * @param data
     * @param obj
     * @return
     */

    public T convert(byte[] data, T obj) {
        Field[] fs = obj.getClass().getDeclaredFields();
        List<Field> fl = Arrays.stream(fs)
                .parallel()
                .filter(i -> null != i.getAnnotation(MessageSlice.class))
                .sorted(Comparator.comparingInt(i -> i.getAnnotation(MessageSlice.class).index()))
                .collect(Collectors.toList());
        int index = 0;
        for (Field i : fl) {
            i.setAccessible(true);
            MessageSlice slice = i.getAnnotation(MessageSlice.class);
            int len = getLen(slice, obj);
            byte[] byteData = ArrayUtils.subarray(data, index, index + len);
            obj = transData(obj, i.getName(), slice, byteData);
            index = index + len;
        }
        return obj;
    }

    /**
     * 数据类型转换
     *
     * @param temp      需要接受数据的对象
     * @param fieldName 接受数据对象承接数据的属性名称
     * @param slice     承接数据属性的MessageSlice注解对象
     * @param byteData  转换前的byte源数据
     * @return 转换结果
     */
    private T transData(T temp, String fieldName, MessageSlice slice, byte[] byteData) {

        try {
            switch (slice.type()) {
                case BYTE:
                    PropertyUtils.setProperty(temp, fieldName, byteData);
                    break;
                case BCD:
                    PropertyUtils.setProperty(temp, fieldName, trans.toBcd(byteData));
                    break;
                case HEX:
                    PropertyUtils.setProperty(temp, fieldName, trans.toHex(byteData));
                    break;
                case ASCII:
                    PropertyUtils.setProperty(temp, fieldName, trans.toAscii(byteData));
                    break;
                case GBK:
                    PropertyUtils.setProperty(temp, fieldName, trans.toGBK(byteData));
                    break;
                case BCD_INT:
                case HEX_INT:
                    PropertyUtils.setProperty(temp, fieldName, Integer.parseInt(trans.toHex(byteData), 16));
                    break;
                case UNSIGN_INT:
                    PropertyUtils.setProperty(temp, fieldName, trans.toLong(byteData));
                    break;
                default:
                    break;
            }
        } catch (IllegalAccessException e) {
            logger.error("初始化异常，类访问失败", e);
            throw new POSPRunTimeException("字段解析失败！请检查对象的访问权限！", e);
        } catch (NoSuchMethodException e) {
            logger.error("初始化异常，set方法未找到", e);
            throw new POSPRunTimeException("字段解析失败！请检查setter方法！", e);
        } catch (InvocationTargetException e) {
            logger.error("访问对象异常", e);
            throw new POSPRunTimeException("字段解析失败！", e);
        }
        return temp;


    }

    /**
     * 获取当前字段占用字字节的长度
     *
     * @param s 注解
     * @param t 包装对象
     * @return 注解标记的字段长度
     */
    private int getLen(MessageSlice s, T t) {
        try {
            if (s.len() > 0) {
                return s.len();
            } else {
                Field ref = t.getClass().getDeclaredField(s.lenRely());
                ref.setAccessible(true);
                return Integer.valueOf(String.valueOf(ref.get(t)));
            }
        } catch (NoSuchFieldException e) {
            logger.error("字段长度未知！至少需要有有一个len属性和lenRel属性值不为空，len需要有值或lenRef需要制定int型的字段名", e);
            throw new POSPRunTimeException("字段解析失败！请检查注解@MessageSlice的lenRef字段是否正确！", e);
        } catch (IllegalAccessException e) {
            logger.error("字段长度未知！请保证属性的访问权限", e);
            throw new POSPRunTimeException("字段解析失败！请检查注解@MessageSlice的lenRef字段是否正确！", e);
        }
    }
}
