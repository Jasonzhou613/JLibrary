package com.ttsea.jlibrary.sample.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.base.JBaseActivity;
import com.ttsea.jlibrary.common.ExitApplication;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.component.dialog.MyAlertDialog;
import com.ttsea.jlibrary.component.dialog.MyDialog;
import com.ttsea.jlibrary.component.dialog.MyProgressDialog;
import com.ttsea.jlibrary.debug.ViewServer;
import com.ttsea.jlibrary.interfaces.OnActivityLifeChangedListener;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Override
    protected void onPause() {
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
}
