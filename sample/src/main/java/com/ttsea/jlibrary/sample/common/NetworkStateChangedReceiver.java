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

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null
                || (!(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)))) {
            return;
        }
        int status = NetWorkUtils.getNetWorkStatus(context);
        JLog.d(TAG, "network state changed, status:" + status + ":" + NetWorkUtils.getNetWorkStatusStr(status));

        //这里可以根据网络状态设置全局变量
        // to do

        onNetworkStatusChanged(status);
    }

    public abstract void onNetworkStatusChanged(int status);
}
