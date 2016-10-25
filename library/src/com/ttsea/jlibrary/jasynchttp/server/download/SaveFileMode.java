package com.ttsea.jlibrary.jasynchttp.server.download;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 如果文件存在，则需要选择文件保存方式 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/5/19 11:02 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/19 11:02
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({
        SaveFileMode.NONACTION, SaveFileMode.OVERRIDE,
        SaveFileMode.RENAME
})
public @interface SaveFileMode {
    /** 无作为，即不进行下载 */
    int NONACTION = 0;
    /** 覆盖，即将原有的文件覆盖 */
    int OVERRIDE = 1;
    /** 重命名，即在文件的后面加上"（2）"等等 */
    int RENAME = 2;
}
