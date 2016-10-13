package com.ttsea.jlibrary.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.component.widget.SmoothCheckBox;

/**
 * //To do <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/10/12 16:49 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/10/12 16:49
 */
public class SmoothCheckBoxActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "SmoothCheckBoxActivity";

    private SmoothCheckBox scBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smooth_check_box_main);

        initView();
    }

    private void initView() {
        scBox = (SmoothCheckBox) findViewById(R.id.scBox);

        scBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox smoothCheckBox, boolean isChecked) {
                JLog.d(TAG, "scBox isChecked:" + isChecked);
            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
    }
}