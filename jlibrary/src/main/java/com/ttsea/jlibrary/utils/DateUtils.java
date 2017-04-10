package com.ttsea.jlibrary.utils;

import android.content.Context;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间类 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class DateUtils {
    private final static String TAG = "Utils.DateUtils";

    /**
     * 格式化要显示的时间<br/>
     * 1. 2分钟内显示：刚刚<br/>
     * 2. 一天内显示：操作时间，如18:00 <br/>
     * 3. 超过一天 小于两天则显示：昨天+操作时间，如 昨天18:00<br/>
     * 4. 超过两天 小于三天则显示：前天+操作时间，如 前天18:00<br/>
     * 4. 超过三天显示：2014-05-15 12:00 <br/>
     * 5. 如果时间为负数则显示：未知
     *
     * @param lastOperationTime 它的格式：String类型
     * @return String
     */
    public static String getFormatOperationTime(Context context, String lastOperationTime) {
        String strTime;
        try {
            long time = Long.parseLong(lastOperationTime);
            strTime = getFormatOperationTime(context, time);
        } catch (Exception e) {
            JLog.e(TAG, "getFormatOperationTime Exception: " + e.getMessage());
            strTime = context.getString(R.string.dateutility_unknow);
        }

        return strTime;
    }

    /**
     * 格式化要显示的时间<br/>
     * 1. 2分钟内显示：刚刚<br/>
     * 2. 一天内显示：操作时间，如18:00 <br/>
     * 3. 超过一天 小于两天则显示：昨天+操作时间，如 昨天18:00<br/>
     * 4. 超过两天 小于三天则显示：前天+操作时间，如 前天18:00<br/>
     * 4. 超过三天显示：2014-05-15 12:00 <br/>
     * 5. 如果时间为负数则显示：未知
     *
     * @param lastOperationTime 它的格式：long类型
     * @return String
     */
    public static String getFormatOperationTime(Context context, long lastOperationTime) {
        String strTime = "";
        SimpleDateFormat formatter;
        long currentTimeMills = System.currentTimeMillis();

        try {
            // 获取lastOperationTime该时间点当天的零点时间戳
            long beginZeroPointTimeMillis = getZeroTimeMillis(lastOperationTime);
            Date beginDate = new Date(lastOperationTime);// 获取当前时间
            // lastOperationTime时间戳与当前时间戳的时间差
            long lastCurrentTimeMillis = (currentTimeMills - lastOperationTime) / 1000;

            // lastOperationTime该时间当天零点时间戳与当前时间戳的时间差
            long between = (currentTimeMills - beginZeroPointTimeMillis) / 1000;// 除以1000是为了转换成秒
            long day = between / (24 * 60 * 60);
            long hour = between / (60 * 60) - day * 24;
            long minute = between / 60 - day * 24 * 60 - hour * 60;
            long second = (between - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);

            if (between < 0) {
                strTime = context.getString(R.string.dateutility_unknow);
                return strTime;
            }

            if (day == 0) {
                if (lastCurrentTimeMillis < 120) {// 0~2分钟内
                    strTime = context.getString(R.string.dateutility_a_moment_ago);
                } else {
                    formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    strTime = context.getString(R.string.dateutility_today) + formatter.format(beginDate);
                }

            } else if (day == 1) {
                formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
                strTime = context.getString(R.string.dateutility_yesterday)
                        + formatter.format(beginDate);

            } else if (day == 2) {
                formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
                strTime = context
                        .getString(R.string.dateutility_today_before_yesterday)
                        + formatter.format(beginDate);

            } else {
                formatter = new SimpleDateFormat("yyy-MM-dd HH:mm", Locale.getDefault());
                strTime = formatter.format(beginDate);
            }

            JLog.d(TAG, day + "天" + hour + "小时" + minute + "分" + second + "秒");

        } catch (Exception e) {
            JLog.e(TAG, "getFormatOperationTime Exception: " + e.getMessage());
            strTime = context.getString(R.string.dateutility_unknow);
        }

        return strTime;
    }

    /**
     * 得到当前时间
     *
     * @param pattern 时间格式
     * @return
     */
    public static String getCurrentTime(String pattern) {
        String date = null;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        date = formatter.format(curDate);

        return date;
    }

    /**
     * 格式化时间
     *
     * @param datetime String 形如"1392566400000"
     * @param pattern  需要的时间格式，形如：yyyy-MM-dd 或者yyyy-MM-dd HH:mm:ss
     * @return 正常解析时返回时间字符串，否则返回null
     */
    public static String parseString(String datetime, String pattern) {
        String d = null;
        if (Utils.isEmpty(datetime)) {
            JLog.d(TAG, "parseString, datetime is empty.");
            return null;
        }
        try {
            long date = Long.parseLong(datetime);
            d = parseString(date, pattern);

        } catch (Exception e) {
            d = null;
            JLog.d(TAG, "parseString, Exception e:" + e.toString());
        }

        return d;
    }

    /**
     * 格式化时间
     *
     * @param datetime long 如：1392566400000
     * @param pattern  需要的时间格式，形如：yyyy-MM-dd 或者yyyy-MM-dd HH:mm:ss
     * @return 正常解析时返回时间字符串，否则返回null
     */
    public static String parseString(long datetime, String pattern) {
        String d = null;
        try {
            SimpleDateFormat time = new SimpleDateFormat(pattern, Locale.getDefault());
            Date date = new Date(datetime);
            d = time.format(date);
            JLog.d(TAG, "parseString, date:" + d);

        } catch (Exception e) {
            d = null;
            JLog.d(TAG, "parseString, Exception e:" + e.toString());
        }

        return d;
    }

    /**
     * 获取timeMillis当天零点的时间戳
     *
     * @param timeMillis 当前时间戳
     * @return long
     */
    public static long getZeroTimeMillis(long timeMillis) {
        Date date = new Date(timeMillis);
        long l = 24 * 60 * 60 * 1000; // 每天的毫秒数
        // date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（
        // 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        // 减8个小时的毫秒值是为了解决时区的问题。
        return (date.getTime() - (date.getTime() % l) - 8 * 60 * 60 * 1000);
    }
}
