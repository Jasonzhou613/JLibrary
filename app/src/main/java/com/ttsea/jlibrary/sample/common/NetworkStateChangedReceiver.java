package com.ttsea.jlibrary.sample.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.utils.NetWorkUtils;

/**
 * 网络连接变化监听 <br>
 * 需要权限：android.permission.ACCESS_NETWORK_STATE
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/2/8 13:39 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/2/8 13:39.
 */
public abstract class NetworkStateChangedReceiver extends BroadcastReceiver {
    private final String TAG = "NetworkStateChangedReceiver";

    private boolean changeGlobal = true;

    public NetworkStateChangedReceiver() {
        this(true);
    }

    public NetworkStateChangedReceiver(boolean changeGlobal) {
        this.changeGlobal = changeGlobal;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null
                || (!(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)))) {
            return;
        }
        int status = NetWorkUtils.getNetWorkStatus(context);
        JLog.d(TAG, "network state changed, status:" + status + ":" + NetWorkUtils.getNetWorkStatusStr(status));

        if (changeGlobal) {
            //这里可以根据网络状态设置全局变量
            // to do
        }

        onNetworkStatusChanged(status);
    }

    /**
     * status的值是 -1,
     * {@link ConnectivityManager#TYPE_MOBILE},
     * {@link ConnectivityManager#TYPE_WIFI},
     * {@link ConnectivityManager#TYPE_MOBILE_MMS},
     * {@link ConnectivityManager#TYPE_MOBILE_SUPL},
     * {@link ConnectivityManager#TYPE_MOBILE_DUN},
     * {@link ConnectivityManager#TYPE_MOBILE_HIPRI},
     * {@link ConnectivityManager#TYPE_WIMAX},
     * {@link ConnectivityManager#TYPE_BLUETOOTH},
     * {@link ConnectivityManager#TYPE_DUMMY},
     * {@link ConnectivityManager#TYPE_ETHERNET},
     * {@link ConnectivityManager#TYPE_VPN},
     *
     * @param status 网络状态,-1代表网络不可用
     */
    public abstract void onNetworkStatusChanged(int status);
}
