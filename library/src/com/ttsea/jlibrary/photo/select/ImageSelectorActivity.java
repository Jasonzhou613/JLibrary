package com.ttsea.jlibrary.photo.select;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.photo.crop.CropConstants;

import java.io.File;
import java.util.ArrayList;

public class ImageSelectorActivity extends FragmentActivity implements View.OnClickListener,
        ImageSelectorFragment.OnImageSelectListener {
    private final String TAG = "ImageSelectorActivity";

    private ArrayList<String> selectedList;
    private ImageConfig imageConfig;

    private View llyTitleBar;
    private Button titleBarBack;
    private Button titleBarOK;
    private TextView titleBarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageselector_activity);

        imageConfig = ImageSelector.getImageConfig();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, Fragment.instantiate(this, ImageSelectorFragment.class.getName(), null))
                .commit();

        initView();
    }

    private void initView() {
        titleBarBack = (Button) findViewById(R.id.titleBarBack);
        titleBarOK = (Button) findViewById(R.id.titleBarOK);
        titleBarName = (TextView) findViewById(R.id.titleBarName);
        llyTitleBar = findViewById(R.id.llyTitleBar);

        titleBarBack.setOnClickListener(this);
        titleBarOK.setOnClickListener(this);

        titleBarOK.setTextColor(imageConfig.getTitleSubmitTextColor());
        titleBarName.setTextColor(imageConfig.getTitleTextColor());
        llyTitleBar.setBackgroundColor(imageConfig.getTitleBgColor());

        selectedList = imageConfig.getPathList();
        if (selectedList == null) {
            selectedList = new ArrayList<String>();
        }

        refreshtitleBarOKTxt();
        if (selectedList.size() <= 0) {
            titleBarOK.setEnabled(false);
        } else {
            titleBarOK.setEnabled(true);
        }
    }

    /** 跳转到剪切图片页面 */
    private void crop(File imageFile) {
//        //cropImagePath = file.getAbsolutePath();
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(Uri.fromFile(new File(imagePath)), "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", aspectX);
//        intent.putExtra("aspectY", aspectY);
//        intent.putExtra("outputX", outputX);
//        intent.putExtra("outputY", outputY);
//        intent.putExtra("return-data", false);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//        startActivityForResult(intent, ImageSelector.IMAGE_CROP_CODE);
    }

    private void refreshtitleBarOKTxt() {
        titleBarOK.setText((getResources().getText(R.string.finish)) +
                "(" + selectedList.size() + "/" + imageConfig.getMaxSize() + ")");
        if (selectedList.size() == 0) {
            titleBarOK.setText(R.string.finish);
            titleBarOK.setEnabled(false);
        }
    }

    private void onBackKeyClicked() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onSingleImageSelected(String path) {
        JLog.d(TAG, "onSingleImageSelected, path:" + path);
        if (imageConfig.isCrop()) {
            crop(new File(path));
        } else {
            Intent data = new Intent();
            selectedList.add(path);
            data.putStringArrayListExtra(ImageSelector.KEY_SELECTED_LIST, selectedList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onImageSelected(String path) {
        JLog.d(TAG, "onImageSelected, add path:" + path);
        if (!selectedList.contains(path)) {
            selectedList.add(path);
        }
        if (selectedList.size() > 0) {
            titleBarOK.setEnabled(true);
        }
        refreshtitleBarOKTxt();
    }

    @Override
    public void onImageUnselected(String path) {
        JLog.d(TAG, "onImageSelected, remove path:" + path);
        selectedList.remove(path);
        refreshtitleBarOKTxt();
    }

    @Override
    public void onCameraShot(File imageFile) {
        JLog.d(TAG, "onCameraShot, filePath:" + imageFile.getAbsolutePath());
        if (imageFile != null) {
            if (imageConfig.isCrop()) {
                crop(imageFile);
            } else {
                Intent data = new Intent();
                selectedList.add(imageFile.getAbsolutePath());
                data.putStringArrayListExtra(ImageSelector.KEY_SELECTED_LIST, selectedList);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.titleBarBack) {//返回
            onBackKeyClicked();
        } else if (v.getId() == R.id.titleBarOK) {//完成
            if (selectedList != null && selectedList.size() > 0) {
                Intent data = new Intent();
                data.putStringArrayListExtra(ImageSelector.KEY_SELECTED_LIST, selectedList);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content);
        if (f != null && f instanceof ImageSelectorFragment) {
            if (((ImageSelectorFragment) f).onKeyDown(keyCode, event)) {
                return true;
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackKeyClicked();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropConstants.REQUEST_CODE_CROP_IMAGE
                && resultCode == CropConstants.RESULT_CODE_CROP_OK) {//剪切图片回来
            JLog.d(TAG, "crop image back:");
            Intent intent = new Intent();
            //selectedList.add(cropImagePath);
            intent.putStringArrayListExtra(ImageSelector.KEY_SELECTED_LIST, selectedList);
            setResult(RESULT_OK, intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}