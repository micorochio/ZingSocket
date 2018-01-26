package com.zc.server.posp.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zing
 * @create 2018/1/19 11:38
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageSlice {
    /**
     * 序号
     *
     * @return
     */
    int index();

    /**
     * 类型
     *
     * @return
     */
    DataType type();

    /**
     * 长度，当Field长度已知时，用本字段标出长度。
     * 本字段和lenRel字段至少有一个有值。
     * 当本字段为0时，解析时会找lenRef标记的依赖字段值作为长度
     *
     * @return
     */
    int len() default 0;

    /**
     * 动态长度依赖
     * 长度，当Field长度未知，但是依赖某个字段的时候，用本字段标记依赖字段的名称。
     * 本字段和lenRel字段至少有一个有值。
     * <p>
     * 只有在len为0，或小于0的情况下，才会解析本字段。当依赖字段名称在对应的类中找不到时，会有异常。
     *
     * @return
     */
    String lenRely() default "";

    /**
     * 数据类型
     */
    enum DataType {
        /**
         * 原生byte不变动
         */
        BYTE,
        /**
         * bcd编码的String
         */
        BCD,
        /**
         * HEX编码的String
         */
        HEX,
        /**
         * ASCII类型字符串
         */
        ASCII,
        /**
         * 无符号的整形long
         */
        UNSIGN_INT,
        /**
         * HEX格式的一个字节的int
         */
        HEX_INT,
        /**
         * BCD格式的一个字节的int
         */
        BCD_INT,
        /**
         * GBK编码的String
         */
        GBK
    }
}
