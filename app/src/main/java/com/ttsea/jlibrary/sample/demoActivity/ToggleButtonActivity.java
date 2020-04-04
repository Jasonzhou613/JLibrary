package com.ttsea.jlibrary.sample.demoActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ttsea.jlibrary.debug.JLog;
import com.ttsea.jlibrary.component.widget.JellyToggle.JellyToggleButton;
import com.ttsea.jlibrary.component.widget.JellyToggle.State;
import com.ttsea.jlibrary.component.widget.ToggleButton;
import com.ttsea.jlibrary.sample.R;
import com.ttsea.jlibrary.sample.base.BaseActivity;

/**
 * //To do <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/11/2 15:45 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/11/2 15:45
 */
public class ToggleButtonActivity extends BaseActivity implements View.OnClickListener, ToggleButton.OnToggleChanged {
    private final String TAG = "ToggleButtonActivity";

    private ToggleButton tbtn01;
    private ToggleButton tbtn02;
    private ToggleButton tbtn03;
    private JellyToggleButton jtbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toggle_buttom_main);

        initView();
    }

    private void initView() {
        tbtn01 = (ToggleButton) findViewById(R.id.tbtn01);
        tbtn02 = (ToggleButton) findViewById(R.id.tbtn02);
        tbtn03 = (ToggleButton) findViewById(R.id.tbtn03);
        jtbtn = (JellyToggleButton) findViewById(R.id.jtbtn);

        tbtn01.setOnToggleChanged(this);
        tbtn02.setOnToggleChanged(this);
        tbtn03.setOnToggleChanged(this);

//        jtbtn.setMoveToSameStateCallListener(true);
//        jtbtn.setColorChangeType(ColorChangeType.RGB);
//        jtbtn.setJelly(Jelly.RANDOM);
//        jtbtn.setEaseType(EaseType.EaseInCirc);
        jtbtn.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                JLog.d(TAG, "state:" + state.name());
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {

            default:
                break;
        }
    }

    @Override
    public void onToggle(ToggleButton button, boolean on) {
        switch (button.getId()) {
            case R.id.tbtn01:
                JLog.d(TAG, "tbtn01, on:" + on);
                break;

            case R.id.tbtn02:
                JLog.d(TAG, "tbtn02, on:" + on);
                break;

            case R.id.tbtn03:
                JLog.d(TAG, "tbtn03, on:" + on);
                break;

            default:
                break;
        }
    }
}
