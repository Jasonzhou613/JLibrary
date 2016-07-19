package com.ttsea.jlibrary.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.photo.select.ImageConfig;
import com.ttsea.jlibrary.photo.select.ImageSelector;

/**
 * //To do <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/7/12 15:40 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/7/12 15:40
 */
public class PhotoActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "PhotoActivity";

    private Button btnSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_main);

        initView();
    }

    private void initView() {
        btnSelect = (Button) findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {

            case R.id.btnSelect:
                selectPhoto();
                break;

            default:
                break;
        }
    }

    private void selectPhoto() {
        ImageConfig config = new ImageConfig.Builder(this)
                .build();
        ImageSelector.open(mActivity, config);
    }
}
