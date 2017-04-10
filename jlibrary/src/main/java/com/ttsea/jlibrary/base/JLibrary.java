package com.ttsea.jlibrary.base;

import android.content.Context;

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

    public static void init(Context appContext) {
        JLog.initIfNeed(appContext);
        CacheDirUtils.initIfNeed(appContext);
    }

}
