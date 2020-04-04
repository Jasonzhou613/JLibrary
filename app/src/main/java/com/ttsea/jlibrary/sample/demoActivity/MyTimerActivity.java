package com.ttsea.jlibrary.sample.demoActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ttsea.jlibrary.common.utils.MyTimer;
import com.ttsea.jlibrary.debug.JLog;
import com.ttsea.jlibrary.sample.R;
import com.ttsea.jlibrary.sample.base.BaseActivity;

/**
 * 计时器 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/10/9 10:46 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/10/9 10:46
 */
public class MyTimerActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "MyTimerActivity";

    private EditText etFrequency;
    private EditText etTotalTime;
    private Button btnStart;
    private Button btnStop;
    private TextView tvInfo;

    private MyTimer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_timer_main);

        initView();
    }

    private void initView() {
        etFrequency = (EditText) findViewById(R.id.etFrequency);
        etTotalTime = (EditText) findViewById(R.id.etTotalTime);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        tvInfo = (TextView) findViewById(R.id.tvInfo);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        myTimer = new MyTimer();
        myTimer.setOnTimerListener(new MyTimer.OnTimerListener() {
            @Override
            public void onTimer(long residualTimeMillis) {
                String info = "计时中...刷新频率:" + myTimer.getFrequencyMillis() + ", 剩余时间：" + residualTimeMillis;
                tvInfo.setText(info);
                JLog.d(TAG, info);
            }

            @Override
            public void onTimerEnd() {
                tvInfo.setText("计时结束");
                JLog.d(TAG, "计时结束");
            }
        });
    }

    private void start() {
        long endTimeMillis = 0;
        long frequencyMillis = 0;
        try {
            frequencyMillis = Long.parseLong(etFrequency.getText().toString());
        } catch (Exception e) {
            showToast("刷新频率需填写整数");
            return;
        }

        try {
            endTimeMillis = Long.parseLong(etTotalTime.getText().toString());
        } catch (Exception e) {
            showToast("计时时间需填写整数");
            return;
        }

        myTimer.setEndTimeMillis(endTimeMillis);
        myTimer.setFrequencyMillis(frequencyMillis);
        myTimer.start();
    }

    private void stop() {
        myTimer.stop(false);
    }

    @Override
    protected void onDestroy() {
        myTimer.stop(false);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {

            case R.id.btnStart:
                start();
                break;

            case R.id.btnStop:
                stop();
                break;

            default:
                break;
        }
    }
}
