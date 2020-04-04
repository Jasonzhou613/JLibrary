package com.ttsea.jlibrary.sample.demoActivity.async;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ttsea.jlibrary.common.utils.JToast;
import com.ttsea.jlibrary.debug.JLog;
import com.ttsea.jlibrary.sample.R;
import com.ttsea.jlibrary.sample.base.BaseActivity;
import com.ttsea.jlibrary.sample.base.SamplePresenterImpl;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * //To do <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/9/20 10:53 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/9/20 10:53
 */
public class AsyncHttpActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "MainActivity";

    private Button btnSendEmail;
    private Button btnSyncHttp;
    private Button btnAsyncHttp;

    private SamplePresenterImpl mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.async_http_main);

        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);
        btnSyncHttp = (Button) findViewById(R.id.btnSyncHttp);
        btnAsyncHttp = (Button) findViewById(R.id.btnAsyncHttp);

        btnSendEmail.setOnClickListener(this);
        btnSyncHttp.setOnClickListener(this);
        btnAsyncHttp.setOnClickListener(this);

        mPresenter = new SamplePresenterImpl(mActivity);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {

            case R.id.btnSendEmail://发送邮件
                sendEmail();
                break;

            case R.id.btnSyncHttp://同步请求
                syncHttp();
                break;

            case R.id.btnAsyncHttp://异步请求
                asyncHttp();
                break;

            default:
                break;

        }
    }

    private void sendEmail() {
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String s) throws Exception {
                        return SendEmail.send("Hi,Jason", "<h1><a href=\"http://www.huiweishang.com\" target=\"_blank\">test</a></h1>");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showDialog("正在发送...", false);
                    }

                    @Override
                    public void onNext(Boolean value) {
                        if (value) {
                            JToast.makeText(mActivity, "发送成功");
                        } else {
                            JToast.makeText(mActivity, "发送失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissAllDialog();
                        JToast.makeText(mActivity, "发送失败");
                    }

                    @Override
                    public void onComplete() {
                        dismissAllDialog();
                    }
                });
    }

    private void syncHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://www.baidu.com";
                    Map<String, String> params = new HashMap<>();
                    String jsonData = mPresenter.getResponseSync(url, params);
                    JLog.d("syncHttp, jsonData:" + jsonData);

                } catch (Exception e) {
                    JLog.e("Exception e:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void asyncHttp() {
        String url = "http://www.baidu.com";
        Map<String, String> params = new HashMap<>();
        mPresenter.addRequest(url, params, 0x001);
    }
}
