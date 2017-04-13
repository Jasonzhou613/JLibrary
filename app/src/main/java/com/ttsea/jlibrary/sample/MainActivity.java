package com.ttsea.jlibrary.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.common.JToast;
import com.ttsea.jlibrary.sample.base.BaseActivity;
import com.ttsea.jlibrary.sample.base.BaseApplication;
import com.ttsea.jlibrary.sample.demoActivity.async.AsyncHttpActivity;
import com.ttsea.jlibrary.sample.demoActivity.ComponentActivity;
import com.ttsea.jlibrary.sample.demoActivity.MyTimerActivity;
import com.ttsea.jlibrary.sample.demoActivity.NetWorkConnectionActivity;
import com.ttsea.jlibrary.sample.demoActivity.PageViewActivity;
import com.ttsea.jlibrary.sample.demoActivity.RoundImageActivity;
import com.ttsea.jlibrary.sample.demoActivity.SmoothCheckBoxActivity;
import com.ttsea.jlibrary.sample.demoActivity.ToggleButtonActivity;
import com.ttsea.jlibrary.sample.demoActivity.photo.PhotoActivity;
import com.ttsea.jlibrary.utils.ApkUtils;
import com.ttsea.jlibrary.utils.AppInformationUtils;
import com.ttsea.jlibrary.utils.CacheDirUtils;
import com.ttsea.jlibrary.utils.DigitUtils;
import com.ttsea.jlibrary.utils.RandomUtils;
import com.ttsea.jlibrary.utils.SdStatusUtils;

public class MainActivity extends BaseActivity {
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
    private Button btnToggleButton;
    private Button btnPageView;
    private Button btnNetWorkConnection;

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
        btnToggleButton = (Button) findViewById(R.id.btnToggleButton);
        btnPageView = (Button) findViewById(R.id.btnPageView);
        btnNetWorkConnection = (Button) findViewById(R.id.btnNetWorkConnection);

        btnTest.setOnClickListener(mOnSingleClickListener);
        btnAppInfo.setOnClickListener(mOnSingleClickListener);
        btnExit.setOnClickListener(mOnSingleClickListener);
        btnComponent.setOnClickListener(mOnSingleClickListener);
        btnPhoto.setOnClickListener(mOnSingleClickListener);
        btnJAsync.setOnClickListener(mOnSingleClickListener);
        btnTimer.setOnClickListener(mOnSingleClickListener);
        btnRoundImage.setOnClickListener(mOnSingleClickListener);
        btnCheckBox.setOnClickListener(mOnSingleClickListener);
        btnToggleButton.setOnClickListener(mOnSingleClickListener);
        btnPageView.setOnClickListener(mOnSingleClickListener);
        btnNetWorkConnection.setOnClickListener(mOnSingleClickListener);
    }

    //@Override
    public void onSingleClick(View v) {
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
                BaseApplication.exitApplication(mActivity);
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

            case R.id.btnToggleButton:
                intent = new Intent(mActivity, ToggleButtonActivity.class);
                startActivity(intent);
                break;

            case R.id.btnPageView:
                intent = new Intent(mActivity, PageViewActivity.class);
                startActivity(intent);
                break;

            case R.id.btnNetWorkConnection:
                intent = new Intent(mActivity, NetWorkConnectionActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void test() {
        JLog.d(TAG, "isPackageInstalled:" + ApkUtils.isPackageInstalled(mActivity, "com.huiweishang.ws"));
//        ApkUtils.install(mActivity, new File(""));
//        ApkUtils.launch(mActivity, "com.tiantiantui.ttt");

        JLog.d(TAG, "getCacheDir:" + CacheDirUtils.getCacheDir(mActivity));
        JLog.d(TAG, "getImageCacheDir:" + CacheDirUtils.getImageCacheDir(mActivity));
        JLog.d(TAG, "getDataCacheDir:" + CacheDirUtils.getDataCacheDir(mActivity));
        JLog.d(TAG, "getSdRootDir:" + CacheDirUtils.getSdRootDir(mActivity));
        JLog.d(TAG, "getSdImageDir:" + CacheDirUtils.getSdImageDir(mActivity));
        JLog.d(TAG, "getSdDataDir:" + CacheDirUtils.getSdDataDir(mActivity));
        JLog.d(TAG, "getSdTempDir:" + CacheDirUtils.getSdTempDir(mActivity));

        JLog.d(TAG, "getFloat:" + DigitUtils.getFloat(5.153456f, 1));

        JLog.d(TAG, "randomString:" + RandomUtils.randomString(32));
        JLog.d(TAG, "limitInt:" + RandomUtils.limitInt(500));

        JLog.d(TAG, "isSDAvailable:" + SdStatusUtils.isSDAvailable());
        JLog.d(TAG, "getAvailableBlockMB:" + SdStatusUtils.getAvailableBlockMB());
        JLog.d(TAG, "isABlockEnough 2000:" + SdStatusUtils.isABlockEnough(2000));

        JToast.makeTextTop(mActivity, "top", 50);
        JToast.makeTextBottom(mActivity, "botom", 50);
        JToast.makeTextLeft(mActivity, "left");
        JToast.makeTextRight(mActivity, "right");
        JToast.makeTextCenter(mActivity, "center");
    }

    private void showAppInfo() {
        JLog.d(TAG, "PackageName:" + AppInformationUtils.getPackageName(mActivity) +
                ", VersionName:" + AppInformationUtils.getVersionName(mActivity) +
                ", VersionCode:" + AppInformationUtils.getVersionCode(mActivity)
        );
    }
}
