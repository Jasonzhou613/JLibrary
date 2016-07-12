package com.ttsea.jlibrary.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.common.JToast;

/**
 * //To do <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/7/11 18:10 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/7/11 18:10
 */
public class ComponentActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "ComponentActivity";

    private Button btnShowAlterDialog;
    private Button btnShowDialog;
    private Button btnShowProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_main);

        initView();
    }

    private void initView() {
        btnShowAlterDialog = (Button) findViewById(R.id.btnShowAlterDialog);
        btnShowDialog = (Button) findViewById(R.id.btnShowDialog);
        btnShowProgress = (Button) findViewById(R.id.btnShowProgress);

        btnShowAlterDialog.setOnClickListener(this);
        btnShowDialog.setOnClickListener(this);
        btnShowProgress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {

            case R.id.btnShowAlterDialog:
                showAlterDialog();
                break;

            case R.id.btnShowDialog:
                showDialog();
                break;

            case R.id.btnShowProgress:
                showProgress();
                break;

            default:
                break;
        }
    }

    private void showAlterDialog() {
        showAlertDialog("title", "msg",
                getStringById(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                },
                getStringById(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                },
                true, true);
    }

    private void showDialog() {
        showDialog("", new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                JToast.makeTextCenter(mActivity, "dialog dismiss");
            }
        }, true, true);

    }

    private void showProgress() {
        showProgress("提示", "正在加载...", true);
    }
}
