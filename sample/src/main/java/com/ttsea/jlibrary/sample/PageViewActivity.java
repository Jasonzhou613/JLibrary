package com.ttsea.jlibrary.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ttsea.jlibrary.base.BaseActivity;

/**
 * //To do <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/7/12 15:40 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/7/12 15:40
 */
public class PageViewActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "PageViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_view_main);

        initView();
        initData();
    }

    private void initView() {


    }

    private void initData() {
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {

            case R.id.btnSelect:
                break;

            default:
                break;
        }
    }
}
