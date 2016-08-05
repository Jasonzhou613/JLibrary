package com.ttsea.jlibrary.photo.select;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.base.BaseFragmentActivity;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.photo.crop.CropConstants;

import java.io.File;
import java.util.ArrayList;

public class ImageSelectorActivity extends BaseFragmentActivity implements View.OnClickListener,
        ImageSelectorFragment.OnImageSelectListener {
    private final String TAG = "ImageSelectorActivity";

    private ArrayList<String> selectedList;
    private ImageConfig imageConfig;

    private View llyTitleBar;
    private Button btnLeft;
    private Button btnRight;
    private TextView tvTitleBarName;

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
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        tvTitleBarName = (TextView) findViewById(R.id.tvTitleBarName);
        llyTitleBar = findViewById(R.id.llyTitleBar);

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

        btnRight.setTextColor(imageConfig.getTitleSubmitTextColor());
        tvTitleBarName.setTextColor(imageConfig.getTitleTextColor());
        llyTitleBar.setBackgroundColor(imageConfig.getTitleBgColor());

        selectedList = imageConfig.getPathList();
        if (selectedList == null) {
            selectedList = new ArrayList<String>();
        }

        refreshBtnRightStatus();
    }

    /** 跳转到剪切图片页面 */
    private void crop(File imageFile) {
        String imagePath = imageFile.getAbsolutePath();
        Intent intent = new Intent(CropConstants.ACTION_CROP);
        intent.setDataAndType(Uri.fromFile(new File(imagePath)), "image/*");
        intent.putExtra(CropConstants.OUT_PUT_PATH, imageConfig.getOutPutPath());
        intent.putExtra(CropConstants.ASPECT_X, imageConfig.getAspectX());
        intent.putExtra(CropConstants.ASPECT_Y, imageConfig.getAspectY());
        intent.putExtra(CropConstants.OUTPUT_X, imageConfig.getOutputX());
        intent.putExtra(CropConstants.OUTPUT_Y, imageConfig.getOutputY());
        intent.putExtra(CropConstants.CROP_MODEL, imageConfig.getCropModel());
        intent.putExtra(CropConstants.IMAGE_SUFFIX, imageConfig.getImageSuffix());
        intent.putExtra(CropConstants.RETURN_DATA, false);
        intent.putExtra(CropConstants.FIXED_ASPECT_RATIO, true);

        startActivityForResult(intent, CropConstants.REQUEST_CODE_CROP_IMAGE);
    }

    private void refreshBtnRightStatus() {
        btnRight.setText((getResources().getText(R.string.finish)) +
                "(" + selectedList.size() + "/" + imageConfig.getMaxSize() + ")");
        if (selectedList.size() == 0) {
            btnRight.setEnabled(false);
        } else {
            btnRight.setEnabled(true);
        }

        if (imageConfig.isMutiSelect()) {
            btnRight.setVisibility(View.VISIBLE);
        } else {
            btnRight.setVisibility(View.INVISIBLE);
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
        refreshBtnRightStatus();
    }

    @Override
    public void onImageUnselected(String path) {
        JLog.d(TAG, "onImageSelected, remove path:" + path);
        selectedList.remove(path);
        refreshBtnRightStatus();
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
        if (v.getId() == R.id.btnLeft) {//返回
            onBackKeyClicked();
        } else if (v.getId() == R.id.btnRight) {//完成
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
        if (requestCode == CropConstants.REQUEST_CODE_CROP_IMAGE) {//剪切图片回来
            if (resultCode == CropConstants.RESULT_CODE_CROP_OK) {
                if (data.getData() == null) {
                    toastMessage("剪切图片出错");
                    return;
                }
                selectedList.clear();
                selectedList.add(data.getData().getPath());
                JLog.d(TAG, "crop image back, imagePath:" + data.getData().getPath());

                Intent intent = new Intent();
                intent.putStringArrayListExtra(ImageSelector.KEY_SELECTED_LIST, selectedList);
                setResult(RESULT_OK, intent);
                finish();

            } else if (resultCode == CropConstants.RESULT_CODE_CROP_CANCLED) {
                //toastMessage("取消了剪切图片");
            } else if (resultCode == CropConstants.RESULT_CODE_CROP_ERROR) {
                toastMessage("剪切图片出错");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}