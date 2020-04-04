package com.ttsea.jlibrary.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.debug.JLog;

/**
 * 在程序第一次启动的时候创建快捷方式<br>
 * {@link #createShortcut(String)}方法得到应用的Version name<br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class CreateShortCut {
    private final String TAG = "Common.CreateShortCut";
    private Activity mActivity;

    public CreateShortCut(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 为程序创建桌面快捷方式<br/>
     * <b>需要添加权限：</b>uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"
     *
     * @param launcherActivityName app默认启动的activity，需要包含包名，如"com.ttsea.jdemo.MainActivity"<br/>
     */
    public void createShortcut(String launcherActivityName) {
        if (!isInstallShortCut(mActivity)) {
            saveShortCutInfo(mActivity);
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

            // 快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, mActivity.getString(R.string.app_name));
            shortcut.putExtra("duplicate", false); // 不允许重复创建

            Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
            shortcutIntent.setClassName(mActivity, launcherActivityName);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            // 快捷方式的图标
            ShortcutIconResource iconRes = Intent.ShortcutIconResource
                    .fromContext(mActivity, R.drawable.ic_launcher);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

            mActivity.sendBroadcast(shortcut);
            JLog.d(TAG, "Created a short cut.");
        }
        JLog.d(TAG, "createShortcut, short cut has created.");
    }

    private void saveShortCutInfo(Context context) {
        SharedPreferences.Editor shortCutInfo = context.getSharedPreferences("shortCutInfo", 0).edit();
        shortCutInfo.putBoolean("isInstalled", true);
        shortCutInfo.apply();
    }

    private boolean isInstallShortCut(Context context) {
        boolean isInstalled = false;
        try {
            SharedPreferences shortCutInfo = context.getSharedPreferences("shortCutInfo", 0);
            if (shortCutInfo != null) {
                isInstalled = shortCutInfo.getBoolean("isInstalled", false);
            }

        } catch (Exception e) {
            isInstalled = false;
            JLog.e(TAG, "isInstallShortCut, Exception: " + e.toString());
        }

        return isInstalled;
    }
}
