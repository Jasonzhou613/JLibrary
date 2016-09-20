package com.ttsea.jlibrary.sample.jasynchttp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.sample.R;

/**
 * //To do <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/9/20 11:07 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/9/20 11:07
 */
public class DownloadActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "DownloadActivity";
    private EditText etPath;
    private Button btnAddDownload;
    private Button btnDownloadDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_main);

        etPath = (EditText) findViewById(R.id.etPath);
        btnAddDownload = (Button) findViewById(R.id.btnAddDownload);
        btnDownloadDetail = (Button) findViewById(R.id.btnDownloadDetail);

        btnAddDownload.setOnClickListener(this);
        btnDownloadDetail.setOnClickListener(this);

        etPath.setText(TestDownloadUrl.TEMP_URL);
        if (etPath.getText() != null) {
            String txt = etPath.getText().toString();
            etPath.setSelection(txt.length());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnAddDownload:

                break;

            case R.id.btnDownloadDetail:
//                intent = new Intent(mActivity, DownloadDetailActivity.class);
//                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
