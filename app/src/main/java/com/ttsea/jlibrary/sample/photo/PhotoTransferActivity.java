package com.ttsea.jlibrary.sample.photo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import com.ttsea.jlibrary.photo.select.CropConfig;
import com.ttsea.jlibrary.photo.select.ImageSelector;
import com.ttsea.jlibrary.photo.select.SelectConfig;
import com.ttsea.jlibrary.sample.base.BaseActivity;

/**
 * 加这个中转的activity为的是解决三星拍照问题 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/6 13:48 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class PhotoTransferActivity extends BaseActivity {
    private Intent data;
    private int resultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            SelectConfig selectConfig = null;
            CropConfig cropConfig = null;
            if (mActivity.getIntent().getExtras().getSerializable("selectConfig") != null) {
                selectConfig = (SelectConfig) mActivity.getIntent().getExtras().getSerializable("selectConfig");
            }
            if (mActivity.getIntent().getExtras().getSerializable("cropConfig") != null) {
                cropConfig = (CropConfig) mActivity.getIntent().getExtras().getSerializable("cropConfig");
            }
            ImageSelector.open(this, selectConfig, cropConfig);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState == null) {
            outState = new Bundle();
        }
        outState.putParcelable("data", data);
        outState.putInt("resultCode", resultCode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }

        if (savedInstanceState.getParcelable("data") != null) {
            data = savedInstanceState.getParcelable("data");
        }

        resultCode = savedInstanceState.getInt("resultCode");

        setResult(resultCode, data);
        this.finish();
    }

    @Override
    public void finish() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PhotoTransferActivity.super.finish();
            }
        }, 600);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.data = data;
        this.resultCode = resultCode;
        setResult(resultCode, data);
        this.finish();
    }

    public static void start(Activity activity, SelectConfig selectConfig, CropConfig cropConfig) {
        Intent intent = new Intent(activity, PhotoTransferActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectConfig", selectConfig);
        bundle.putSerializable("cropConfig", cropConfig);
        intent.putExtras(bundle);

        activity.startActivityForResult(intent, selectConfig.getRequestCode());
    }
}
