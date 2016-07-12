package com.ttsea.jlibrary.common;

import android.content.Context;

/**
 * 获取应用的版本号和版本代码 <br/>
 * {@link #getVersionName()}方法得到应用的Version name<br/>
 * {@link #getVersionCode()}方法得到应用的Version code
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2013.11.25 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0
 */
public class AppInformation {
    private Context mContext;
    private final String TAG = "Common.AppInformation";

    public AppInformation(Context context) {
        this.mContext = context;
    }

    /**
     * 得到应用程序的包名
     *
     * @return 包名
     */
    public String getPackageName() {
        return mContext.getPackageName();
    }

    /**
     * 得到应用的version name，如：1.0.0
     *
     * @return version name，发生异常时返回null;
     */
    public String getVersionName() {
        String versionname = null;
        String pkName = "";

        try {
            pkName = mContext.getPackageName();
            versionname = mContext.getPackageManager()
                    .getPackageInfo(pkName, 0).versionName;

        } catch (Exception e) {
            JLog.e(TAG, "getVersionName, Exception: " + e.getMessage());
        }
        JLog.d(TAG, "App package name:" + pkName + ", App version name:" + versionname);

        return versionname;
    }

    /**
     * 得到应用的version code，如：131125
     *
     * @return version code，发生异常时返回-1
     */
    public int getVersionCode() {
        int versioncode = -1;
        try {
            versioncode = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionCode;

        } catch (Exception e) {
            JLog.e(TAG, "getVersionCode, Exception: " + e.getMessage());
        }
        JLog.d(TAG, "App version code:" + String.valueOf(versioncode));

        return versioncode;
    }
}
