package com.ttsea.jlibrary.sample;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.ttsea.jlibrary.common.JToast;
import com.ttsea.jlibrary.sample.base.BaseActivity;
import com.ttsea.jlibrary.utils.NetWorkUtils;

/**
 * // to do <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/2/8 11:10 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/2/8 11:10.
 */
public class NetWorkConnectionActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "MyTimerActivity";

    private Button btnStart;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_connection_main);

        initView();
    }

    private void initView() {
        btnStart = (Button) findViewById(R.id.btnStart);
        tvInfo = (TextView) findViewById(R.id.tvInfo);

        btnStart.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {

            case R.id.btnStart:
                ConnectionQuality cq = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
                JToast.makeText(mActivity, "" + cq.name());
                break;

            default:
                break;
        }
    }

    @Override
    public void onNetworkStatusChanged(int status) {
        if (status == -1) {
            toastMessage("网络不可用");
        } else if (status == ConnectivityManager.TYPE_WIFI) {
            toastMessage("已连接wifi");
        } else {
            toastMessage("网络可用，" + NetWorkUtils.getNetWorkStatusStr(status));
        }
    }
}

