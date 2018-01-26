package com.zc.server.posp.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * 常用验证器
 *
 * @author zing
 * @create 2018/1/18 18:05
 */
public final class Validator {
    /**
     * 纯数字正则
     */
    private static final String NUMBER_REGEX = "^\\d$";
    private static Logger logger = LoggerFactory.getLogger(Validator.class);

    /**
     * 正则验证
     *
     * @param regex 正则表达式
     * @param mach  输入数据
     * @return 是否匹配
     */
    public static boolean regexMach(String regex, String mach) {
        return Pattern.matches(regex, mach);
    }

    /**
     * 判断是否为数字
     *
     * @param input 输入
     * @return 是否为纯数字
     */

    public static boolean isNumber(String input) {
        if (StringUtils.isEmpty(input)) {
            logger.error("input an empty data!");
        }
        return regexMach(NUMBER_REGEX, input);
    }

    /**
     * 判断是否为偶数
     *
     * @param num 源
     * @return 验证结果
     */
    public static boolean isEvenNumber(int num) {
        return 0 == num % 2;
    }

    /**
     * 判断是否为奇数
     *
     * @param num 输入
     * @return 判断结果
     */
    public static boolean isOddNumber(int num) {
        return 0 != num % 2;
    }
}
