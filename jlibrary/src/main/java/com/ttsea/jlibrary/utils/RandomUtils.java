package com.ttsea.jlibrary.utils;

import java.util.Random;

/**
 * 随机数据生成工具 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class RandomUtils {

    /**
     * 生成指定长度的随机字符内容
     *
     * @param length 长度
     * @return 字符内容
     */
    public static String randomString(int length) {
        StringBuilder buffer = new StringBuilder();
        for (int t = 1; t < length; t++) {
            long time = System.currentTimeMillis() + t;
            if (time % 3 == 0) {
                buffer.append((char) time % 9);
            } else if (time % 3 == 1) {
                buffer.append((char) (65 + time % 26));
            } else {
                buffer.append((char) (97 + time % 26));
            }
        }
        return buffer.toString();
    }

    /**
     * 生成指定[0,limit)范围的随机数
     *
     * @param limit 最大值
     * @return 随机数
     */
    public static int limitInt(int limit) {
        return Math.abs(new Random().nextInt(limit));
    }

}
