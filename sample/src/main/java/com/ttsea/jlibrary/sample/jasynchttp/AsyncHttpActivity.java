package com.ttsea.jlibrary.sample.jasynchttp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.custom.CStringRequest;
import com.android.volley.custom.CVolleySingleton;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.jasynchttp.mail.SendEmail;
import com.ttsea.jlibrary.sample.R;
import com.ttsea.jlibrary.sample.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

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
    private Button btnGetData;
    private Button btnSendEmail;
    private Button btnDownload;

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.async_http_main);

        btnGetData = (Button) findViewById(R.id.btnGetData);
        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);
        btnDownload = (Button) findViewById(R.id.btnDownload);

        btnGetData.setOnClickListener(this);
        btnSendEmail.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btnGetData:
                getData(null, 2);
                break;

            case R.id.btnSendEmail:
                //sendEmail();
                break;

            case R.id.btnDownload:
                intent = new Intent(mActivity, DownloadActivity.class);
                startActivity(intent);
                break;

            default:
                break;

        }
    }

    private void getData(final Map<String, String> params, final int requestCode) {

        CStringRequest request = new CStringRequest(Request.Method.POST, Urls.getUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String arg0) {
                        handleNetWorkData(arg0, requestCode);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {

                JLog.e(TAG, " " + arg0.getMessage());
                handleErrorResponse(arg0.getMessage(), requestCode);
            }
        }) {

            protected Map<String, String> getParams() {
                Map<String, String> finallyparams = new HashMap<String, String>();
                if (params != null) {
                    finallyparams = params;
                }
                finallyparams.put("platform", "android");

                return finallyparams;
            }
        };

        request.setTag(TAG);

        CVolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    @Override
    protected void onDestroy() {
        CVolleySingleton.getInstance(this).getRequestQueue().cancelAll(TAG);
        super.onDestroy();
    }

    private void sendEmail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SendEmail.send("test", "content, text");
            }
        }).start();
    }


    public void handleErrorResponse(String errorMsg, int requestCode) {
        JLog.d(TAG, errorMsg);
    }

    public synchronized boolean handleNetWorkData(String jsonData, int requestCode) {
        JLog.d(TAG, jsonData);
        return true;
    }
}
