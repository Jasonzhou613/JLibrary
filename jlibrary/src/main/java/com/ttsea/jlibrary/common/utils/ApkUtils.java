package com.ttsea.jlibrary.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import com.ttsea.jlibrary.debug.JLog;

import java.io.File;

/**
 * APK相关功能帮助器类 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class ApkUtils {
    private final static String TAG = "Utils.ApkUtils";

    /**
     * 判断APK包是否已经安装
     *
     * @param context     上下文，一般为Activity
     * @param packageName 包名
     * @return 包存在则返回true，否则返回false
     */
    public static boolean isPackageInstalled(Context context, String packageName) {
        if (null == packageName || "".equals(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return (null != info);

        } catch (NameNotFoundException e) {
            JLog.e(TAG, "isPackageExists, NameNotFoundException: " + e.toString());
            return false;

        } catch (Exception e) {
            JLog.e(TAG, "isPackageExists, Exception: " + e.toString());
            return false;
        }
    }

    /**
     * 安装指定APK文件
     *
     * @param activity Activity
     * @param apkFile  APK文件对象
     */
    public static void install(Activity activity, File apkFile) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile),
                "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }

    /**
     * 启动一个指定包名的应用的默认Activity
     *
     * @param activity    Activity
     * @param packageName 包名
     */
    public static void launch(Activity activity, String packageName) {
        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(
                packageName);
        if (null != intent) {
            activity.startActivity(intent);
        }
    }
}
