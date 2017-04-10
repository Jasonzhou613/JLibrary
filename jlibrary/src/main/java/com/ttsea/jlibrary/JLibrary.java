package com.ttsea.jlibrary;

import android.content.Context;
import android.util.Log;

import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.utils.CacheDirUtils;

/**
 * // to do <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class JLibrary {
    private static final String TAG = "JLibrary";
    private static boolean DEBUG = false;

    public static void init(Context appContext) {

        CacheDirUtils.initIfNeed(appContext);
        JLog.enableLog(JLibrary.isDebugMode());

        Log.d(TAG, "JLibrary DEBUG:" + JLibrary.isDebugMode());
    }

    /**
     * if {@link #DEBUG} is true, it is debug mode
     */
    public static boolean isDebugMode() {
        return DEBUG;
    }

    /**
     * 设置JLibrary是否为调试模式
     *
     * @param debug true为调试模式，false为不是调试模式
     */
    public static void debugMode(boolean debug) {
        DEBUG = debug;
        JLog.enableLog(debug);

        Log.d(TAG, "JLibrary DEBUG:" + JLibrary.isDebugMode());
    }
}
