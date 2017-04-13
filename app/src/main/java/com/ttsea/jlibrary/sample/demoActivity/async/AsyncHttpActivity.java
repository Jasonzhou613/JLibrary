package com.ttsea.jlibrary.sample.demoActivity.async;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ttsea.jlibrary.common.JToast;
import com.ttsea.jlibrary.sample.R;
import com.ttsea.jlibrary.sample.base.BaseActivity;

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
    private Button btnSendEmail;

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.async_http_main);

        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);

        btnSendEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {


            case R.id.btnSendEmail:
                sendEmail();
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
}
