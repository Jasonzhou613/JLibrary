package com.ttsea.jlibrary.sample.base;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.ttsea.jlibrary.base.JBaseActivity;
import com.ttsea.jlibrary.sample.common.NetworkStateChangedReceiver;

/**
 * Activity基类，继承JBaseActivity <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/4/11 20:13 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/4/11 20:13
 */
public class BaseActivity extends JBaseActivity {
    private NetworkStateChangedReceiver networkStateChangedReceiver = new NetworkStateChangedReceiver() {
        @Override
        public void onNetworkStatusChanged(int status) {
            BaseActivity.this.onNetworkStatusChanged(status);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //注册网络变化监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateChangedReceiver, filter);
    }

    @Override
    protected void onPause() {
        //反注册网络变化监听
        unregisterReceiver(networkStateChangedReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * status的值是 -1,
     * {@link android.net.ConnectivityManager#TYPE_MOBILE},
     * {@link android.net.ConnectivityManager#TYPE_WIFI},
     * {@link android.net.ConnectivityManager#TYPE_MOBILE_MMS},
     * {@link android.net.ConnectivityManager#TYPE_MOBILE_SUPL},
     * {@link android.net.ConnectivityManager#TYPE_MOBILE_DUN},
     * {@link android.net.ConnectivityManager#TYPE_MOBILE_HIPRI},
     * {@link android.net.ConnectivityManager#TYPE_WIMAX},
     * {@link android.net.ConnectivityManager#TYPE_BLUETOOTH},
     * {@link android.net.ConnectivityManager#TYPE_DUMMY},
     * {@link android.net.ConnectivityManager#TYPE_ETHERNET},
     * {@link android.net.ConnectivityManager#TYPE_VPN},
     *
     * @param status 网络状态,-1代表网络不可用
     */
    public void onNetworkStatusChanged(int status) {

    }
}
