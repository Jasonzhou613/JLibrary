package com.ttsea.jlibrary.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Jason on 2016/5/15.
 */
public class DigitUtils {
    private static final String TAG = "Utils.DigitUtils";

    // *********************************Integer start*********************************//
    // *********************************Integer end*********************************//


    // *********************************Long start*********************************//
    // *********************************Long end*********************************//


    // *********************************Float start*********************************//

    /**
     * 获取一个数的浮点类型
     *
     * @param originFloat 原数据
     * @param digitCount  要保留的位数
     * @return float
     */
    public static float getFloat(float originFloat, int digitCount) {
        return getFloat(originFloat, digitCount, RoundingMode.DOWN);
    }

    /**
     * @param originFloat 原数据
     * @param digitCount  要保留的位数
     * @param mode        RoundingMode，可以设置为四舍五入
     * @return float
     */
    public static float getFloat(float originFloat, int digitCount, RoundingMode mode) {
        BigDecimal bg = new BigDecimal(originFloat).setScale(digitCount, mode);
        return bg.floatValue();
    }
    // *********************************Float end*********************************//
}
