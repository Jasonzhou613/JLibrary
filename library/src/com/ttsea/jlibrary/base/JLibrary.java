package com.ttsea.jlibrary.base;

import android.content.Context;

import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.utils.CacheDirUtils;

/**
 * // to do <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/12/6 14:29 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/12/6 14:29.
 */
public class JLibrary {

    public static void init(Context appContext) {
        CacheDirUtils.initIfNeed(appContext);
        JLog.initIfNeed(appContext);
    }

}
