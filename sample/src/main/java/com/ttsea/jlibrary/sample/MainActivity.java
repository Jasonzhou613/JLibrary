package com.ttsea.jlibrary.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.common.ExitApplication;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.sample.jasynchttp.AsyncHttpActivity;
import com.ttsea.jlibrary.utils.AppInformationUtils;
import com.ttsea.jlibrary.utils.CacheDirUtils;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "MainActivity";

    private Button btnTest;
    private Button btnAppInfo;
    private Button btnExit;
    private Button btnComponent;
    private Button btnPhoto;
    private Button btnJAsync;
    private Button btnTimer;
    private Button btnRoundImage;
    private Button btnCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        btnTest = (Button) findViewById(R.id.btnTest);
        btnAppInfo = (Button) findViewById(R.id.btnAppInfo);
        btnExit = (Button) findViewById(R.id.btnExit);
        btnComponent = (Button) findViewById(R.id.btnComponent);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        btnJAsync = (Button) findViewById(R.id.btnJAsync);
        btnTimer = (Button) findViewById(R.id.btnTimer);
        btnRoundImage = (Button) findViewById(R.id.btnRoundImage);
        btnCheckBox = (Button) findViewById(R.id.btnCheckBox);

        btnTest.setOnClickListener(this);
        btnAppInfo.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnComponent.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);
        btnJAsync.setOnClickListener(this);
        btnTimer.setOnClickListener(this);
        btnRoundImage.setOnClickListener(this);
        btnCheckBox.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {

            case R.id.btnTest:
                test();
                break;

            case R.id.btnAppInfo:
                showAppInfo();
                break;

            case R.id.btnExit:
                ExitApplication.getInstance().exitApplication();
                break;

            case R.id.btnComponent:
                intent = new Intent(mActivity, ComponentActivity.class);
                startActivity(intent);
                break;

            case R.id.btnPhoto:
                intent = new Intent(mActivity, PhotoActivity.class);
                startActivity(intent);
                break;

            case R.id.btnJAsync:
                intent = new Intent(mActivity, AsyncHttpActivity.class);
                startActivity(intent);
                break;

            case R.id.btnTimer:
                intent = new Intent(mActivity, MyTimerActivity.class);
                startActivity(intent);
                break;

            case R.id.btnRoundImage:
                intent = new Intent(mActivity, RoundImageActivity.class);
                startActivity(intent);
                break;

            case R.id.btnCheckBox:
                intent = new Intent(mActivity, SmoothCheckBoxActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void test() {
//         JLog.d(TAG, "isPackageInstalled:" + ApkUtils.isPackageInstalled(mActivity, "com.huiweishang.ws"));
//         ApkUtils.install(mActivity, new File(""));
//         ApkUtils.launch(mActivity, "com.huiweishang.ws");
//
//        JLog.d(TAG, "getCacheDir:" + CacheDirUtils.getCacheDir(mActivity));
//        JLog.d(TAG, "getDataCacheDir:" + CacheDirUtils.getDataCacheDir(mActivity));
//        JLog.d(TAG, "getImageCacheDir:" + CacheDirUtils.getImageCacheDir(mActivity));
//        JLog.d(TAG, "getImageCacheDir:" + CacheDirUtils.getSdDataDir(mActivity));
//        JLog.d(TAG, "getTempDir:" + CacheDirUtils.getTempDir(mActivity));
//
//        JLog.d(TAG, "getFloat:" + DigitUtils.getFloat(5.153456f, 5));
//
//        JLog.d(TAG, "randomString:" + RandomUtils.randomString(5));
//        JLog.d(TAG, "limitInt:" + RandomUtils.limitInt(5));
//
//        JToast.makeTextTop(mActivity, "top", 50);
//        JToast.makeTextBottom(mActivity, "botom", 50);
//        JToast.makeTextLeft(mActivity, "left");
//        JToast.makeTextRight(mActivity, "right");
//        JToast.makeTextCenter(mActivity, "center");
    }

    private void showAppInfo() {
        AppInformationUtils.getPackageName(mActivity);
        AppInformationUtils.getVersionName(mActivity);
        AppInformationUtils.getVersionCode(mActivity);

        JLog.d(TAG, "cacheDir:" + CacheDirUtils.getCacheDir(this) +
                ", dataCacheDir:" + CacheDirUtils.getDataCacheDir(this) +
                ", imageCacheDir:" + CacheDirUtils.getImageCacheDir(this) +
                ", tmpDir:" + CacheDirUtils.getTempDir(this)
        );
    }
}
