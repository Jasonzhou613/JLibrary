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
 * 用来打印想要输出的数据，默认为false，将DEBUG设为false后会根据{@link #LOG_TAG}来输错日志 <br>
 * 从高到低为ASSERT, ERROR, WARN, INFO, DEBUG, VERBOSE<br>
 * 使用adb shell setprop log.tag.{@link #LOG_TAG}来控制输出log等级
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class JLog {
    private static final String TAG = "JLog";

    private static boolean DEBUG = false;
    /**
     * 输出日志等级，当DEBUG为false的时候会根据设置的等级来输出日志<br/>
     * 从高到低为ASSERT, ERROR, WARN, INFO, DEBUG, VERBOSE<br/>
     */
    private static String LOG_TAG = "jlibrary.log.LEVEL";

    /**
     * 开启或者关闭log
     *
     * @param enable true为开启log，false为关闭log
     */
    public static void enableLog(boolean enable) {
        DEBUG = enable;
    }

    public static void v(String tag, String msg) {
        if (DEBUG || Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            msg = combineLogMsg(msg);
            Log.i(tag, "" + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG || Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            msg = combineLogMsg(msg);
            Log.d(tag, "" + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG || Log.isLoggable(LOG_TAG, Log.INFO)) {
            msg = combineLogMsg(msg);
            Log.i(tag, "" + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG || Log.isLoggable(LOG_TAG, Log.WARN)) {
            msg = combineLogMsg(msg);
            Log.w(tag, "" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG || Log.isLoggable(LOG_TAG, Log.ERROR)) {
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
        if (!DEBUG && !Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            return;
        }
        JLog.d(TAG, "Start save content, content=" + content);
        String date = DateUtils.getCurrentTime("yyyy-MM-dd_HH_mm_ss");
        String debugFiledir = CacheDirUtils.getSdDataDir(context);
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
        if (!DEBUG && !Log.isLoggable(LOG_TAG, Log.DEBUG)) {
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
            JLog.e(TAG, "Exception e:" + e.getMessage());
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

    public static void printCursor(Cursor c) {
        if ((!DEBUG && !Log.isLoggable(LOG_TAG, Log.DEBUG)) || c == null) {
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
