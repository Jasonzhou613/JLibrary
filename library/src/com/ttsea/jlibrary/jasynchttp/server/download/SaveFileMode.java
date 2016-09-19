package com.ttsea.jlibrary.jasynchttp.server.download;

/**
 * 如果文件存在，则需要选择文件保存方式 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/5/19 11:02 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/19 11:02
 */
public enum SaveFileMode {
    /** 无作为，即不进行下载 */
    NONACTION,
    /** 覆盖，即将原有的文件覆盖 */
    OVERRIDE,
    /** 重命名，即在文件的后面加上"（2）"等等 */
    RENAME
}
