package com.ttsea.jlibrary.common.utils;

/**
 * // to do <br>
 * <p>
 * <b>date:</b> 2017/9/14 20:21 <br>
 * <b>author:</b> zhijian.zhou <br>
 * <b>version:</b> 1.0 <br>
 */
public class SqlUtils {

    /** 转换所有特殊字符 */
    public static String sqliteEscape(String keyWord) {
        keyWord = keyWord.replace("/", "//");
        keyWord = keyWord.replace("'", "''");
        keyWord = keyWord.replace("[", "/[");
        keyWord = keyWord.replace("]", "/]");
        keyWord = keyWord.replace("%", "/%");
        keyWord = keyWord.replace("&", "/&");
        keyWord = keyWord.replace("_", "/_");
        keyWord = keyWord.replace("(", "/(");
        keyWord = keyWord.replace(")", "/)");
        return keyWord;
    }
}
