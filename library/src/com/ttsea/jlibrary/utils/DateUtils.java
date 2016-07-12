package com.ttsea.jlibrary.utils;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间类 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2014.04.01 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0.1
 */
public class DateUtils {
    private final static String TAG = "Utils.DateUtils";

    /**
     * 格式化要显示的时间<br/>
     * 1. 1分钟内显示：刚刚<br/>
     * 2. 1~5分钟内显示：n分钟前<br/>
     * 3. 1~2小时内显示：n小时前<br/>
     * 4. 超过2小时小于1天显示：操作时间，如18:00 <br/>
     * 5. 超过一天 小于两天则显示：昨天+操作时间，如昨天18:00<br/>
     * 6. 超过两天显示：2014-05-15 12:00 <br/>
     * 7. 如果时间为负数则显示：未知
     *
     * @param timeInterval 以毫秒为单位
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getFormatOperationTime(Activity activity, long timeInterval, long operationTime) {
        String strTime = "";
        SimpleDateFormat formatter;

        long between = timeInterval / 1000;// 除以1000是为了转换成秒
        long day = between / (24 * 60 * 60);
        long hour = between / (60 * 60) - day * 24;
        long minute = between / 60 - day * 24 * 60 - hour * 60;
        long second = (between - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);

        if (between < 0) {// 为负数
            strTime = activity.getString(R.string.dateutility_unknow);

        } else if (between < 60) {// 0~1分钟内
            strTime = activity.getString(R.string.dateutility_a_moment_ago);

        } else if (between < 300) {// 1~5分钟之内
            strTime = minute + activity.getString(R.string.dateutility_a_min_ago);

        } else if (between < (60 * 60 * 2)) {// 1~2小时内
            strTime = hour + activity.getString(R.string.dateutility_a_hour_ago);

        } else if (between < (60 * 60 * 24)) {// 超过2小时小于1天
            strTime = activity.getString(R.string.dateutility_today)
                    + hour + ":" + minute;

        } else if (between < (60 * 60 * 24 * 2)) {// 超过一天 小于两天
            strTime = activity.getString(R.string.dateutility_yesterday)
                    + hour + ":" + minute;

        } else {
            strTime = DateUtils.parseString(operationTime, "yyyy-MM-dd HH:mm");
        }
        JLog.d(TAG, day + "天" + hour + "小时" + minute + "分" + second + "秒");

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
}
