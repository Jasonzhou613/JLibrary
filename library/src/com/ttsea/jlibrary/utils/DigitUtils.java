package com.ttsea.jlibrary.utils;

import com.ttsea.jlibrary.common.JLog;

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
     * @return floate
     */
    public static float getFloat(float originFloat, int digitCount) {
        String format = "#.";
        float result = 0;

        for (int i = 0; i < digitCount; i++) {
            format = format + "0";
        }

        try {
            java.text.DecimalFormat df = new java.text.DecimalFormat(format);
            result = Float.parseFloat(df.format(originFloat));

        } catch (Exception e) {
            JLog.e(TAG, "getFloat, Exception e:" + e.toString());
        }
        return result;
    }
    // *********************************Float end*********************************//
}
