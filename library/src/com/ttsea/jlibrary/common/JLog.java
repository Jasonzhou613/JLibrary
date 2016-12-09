package com.ttsea.jlibrary.common;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.utils.CacheDirUtils;
import com.ttsea.jlibrary.utils.DateUtils;
import com.ttsea.jlibrary.utils.SdStatusUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 用来打印想要输出的数据，将DEBUG设为false后会更具{@link #LOG_DEGREE}来输错日志 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2013-11-18 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016.02.17
 */
public class JLog {
    private static final String TAG = "JLog";

    private static boolean DEBUG = true;
    /**
     * 输出日志等级，当DEBUG为false的时候会根据设置的等级来输出日志<br/>
     * 从高到低为ERROR, WARN, INFO, DEBUG, VERBOSE
     */
    private static final int LOG_DEGREE = Log.ERROR;

    /**
     * Enables logger (if {@link #disableLogging()} was called before)
     */
    public static void enableLogging() {
        DEBUG = true;
    }

    public static void initIfNeed(Context context) {
        DEBUG = context.getResources().getBoolean(R.bool._j_debug_model);
        Log.d(TAG, "DEBUG:" + DEBUG);
    }

    /**
     * Disables logger, no logs will be passed to LogCat, all log methods will
     * do nothing
     */
    public static void disableLogging() {
        DEBUG = false;
    }

    /**
     * if {@link #DEBUG} is true, it is debug mode
     */
    public static boolean isDebugMode() {
        return DEBUG;
    }

    public static void v(String tag, String msg) {
        if (DEBUG || LOG_DEGREE <= Log.VERBOSE) {
            msg = combineLogMsg(msg);
            Log.i(tag, "" + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG || LOG_DEGREE <= Log.DEBUG) {
            msg = combineLogMsg(msg);
            Log.d(tag, "" + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG || LOG_DEGREE <= Log.INFO) {
            msg = combineLogMsg(msg);
            Log.i(tag, "" + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG || LOG_DEGREE <= Log.WARN) {
            msg = combineLogMsg(msg);
            Log.w(tag, "" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG || LOG_DEGREE <= Log.ERROR) {
            msg = combineLogMsg(msg);
            Log.e(tag, "" + msg);
        }
    }

    /** 组装动态传参的字符串 将动态参数的字符串拼接成一个字符串 */
    private static String combineLogMsg(String... msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Thread:").append(Thread.currentThread().getId()).append("]");
        sb.append(getCaller()).append(": ");
        if (null != msg) {
            for (String s : msg) {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    private static String getCaller() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        for (int i = 3; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(JLog.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                caller = callingClass + "." + trace[i].getMethodName()
                        + "(rows:" + trace[i].getLineNumber() + ")";
                break;
            }
        }
        return caller;
    }

    /**
     * 将content中的内容保存至外置SD卡的指定文件夹中，保存名如：2015-07-21_21_55_01_log<br/>
     * 需要读写SD卡的权限: android.permission.WRITE_EXTERNAL_STORAGE
     *
     * @param context  上下文
     * @param content  保存的内容
     * @param fileName 保存的文件名
     */
    public static void saveString(Context context, String content, String fileName) {
        if (!DEBUG) {
            return;
        }
        JLog.d(TAG, "Start save content, content=" + content);
        String date = DateUtils.getCurrentTime("yyyy-MM-dd_HH_mm_ss");
        String debugFiledir = SdStatusUtils.getExternalStorageAbsoluteDir() + "/"
                + context.getResources().getString(R.string._j_root_cache_dir_debug);
        String filePath = debugFiledir + "/" + date + "_" + fileName;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            out.write(content.getBytes());
            out.close();
            JLog.d(TAG, "End save content. The file name is: " + filePath);

        } catch (Exception e) {
            JLog.e(TAG, "Save content error, Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 将文件拷贝到制定目录
     *
     * @param scrFilePath 源文件(包括路径)
     * @param desDirPath  要拷贝到的目录
     */
    public static void copyFile(String scrFilePath, String desDirPath) {
        if (!DEBUG) {
            return;
        }
        File srcFile = new File(scrFilePath);
        File desDir = new File(desDirPath);
        InputStream fis = null;
        OutputStream fos = null;
        if (!srcFile.exists()) {
            JLog.e(TAG, "srcFile not exist, srcFile=" + srcFile);
            return;
        }
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        String fileName = srcFile.getName();
        File desFile = new File(desDirPath + File.separator + fileName);
        if (desFile.exists()) {
            desFile.delete();
        }
        try {
            desFile.createNewFile();
        } catch (IOException e) {
            JLog.e(TAG, "copyFile, IOException e:" + e.toString());
        }

        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(desFile);
            byte[] b = new byte[1024];
            int i;
            while ((i = fis.read(b)) != -1) {
                fos.write(b, 0, i);
            }
            JLog.d(TAG, "copy file successful, desFile=" + desFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copyDB2SD(Context context, String dbName) {
        if (!DEBUG) {
            return;
        }
        File dbFile = context.getDatabasePath(dbName);
        copyFile(dbFile.getAbsolutePath(), CacheDirUtils.getCacheDir(context)
                + File.separator + "db");
    }

    public static void printCursor(Cursor c) {
        if (!DEBUG || c == null) {
            return;
        }

        JLog.d(TAG, "cursorCount:" + c.getCount());
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int columnCount = c.getColumnCount();
            String columnInfo = "";
            for (int i = 0; i < columnCount; i++) {
                columnInfo = columnInfo + "columnName:" + c.getColumnName(i) + "-columnValue:" + c.getString(i) + ", ";
            }
            JLog.d(TAG, columnInfo);
        }
    }
}
