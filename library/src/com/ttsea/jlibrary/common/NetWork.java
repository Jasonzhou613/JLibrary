package com.ttsea.jlibrary.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 检测网络是否可用、获取网络链类型<br/>
 * need "android.permission.ACCESS_NETWORK_STATE" permission
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2013.09.25 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2015.05.21
 */
public class NetWork {
    private final static String TAG = "Common.NetWork";

    /**
     * 获取网络链接类型，返回值：<br/>
     * 正常时返回ConnectivityManager里所定义的类型，如：TYPE_WIFI、TYPE_MOBILE等 <br/>
     * -1 表示网络不可用或发生异常 <br/>
     * 0 表示TYPE_MOBILE <br/>
     * 1 表示TYPE_WIFI<br/>
     * 2 表示TYPE_MOBILE_MMS<br/>
     * 3 表示TYPE_MOBILE_SUPL<br/>
     * 4 表示TYPE_MOBILE_DUN<br/>
     * 5 表示TYPE_MOBILE_HIPRI<br/>
     * 6 表示TYPE_WIMAX<br/>
     *
     * @param context
     * @return 网络链接类型，-1表示网络不可用
     */
    public static int getNetWorkStatus(Context context) {
        int status = -1;
        // 获取手机所有连接管理对象（包括对wifi,cmnet,cmwap等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取网络连接管理的对象
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                status = networkInfo.getType();
            }

        } catch (Exception e) {
            status = -1;
            JLog.e(TAG, "getNetWorkStatus, Exception: " + e.getMessage());
        }
        JLog.d(TAG, "NetWork status:" + String.valueOf(status));

        return status;
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return 网络可以用，则返回true；否则返回false
     */
    public static boolean isAvailable(Context context) {
        if (getNetWorkStatus(context) == -1) {
            JLog.d(TAG, "Network is not available.");
            return false;
        }
        JLog.d(TAG, "Network is available.");
        return true;
    }

    /**
     * wifi是否可用
     *
     * @param context
     * @return wifi可用，则返回true；否则返回false
     */
    public static boolean isWiFiAvailable(Context context) {
        if (getNetWorkStatus(context) == ConnectivityManager.TYPE_WIFI) {
            JLog.d(TAG, "wifi is available.");
            return true;
        }
        JLog.d(TAG, "wifi is not available.");
        return false;
    }
}
