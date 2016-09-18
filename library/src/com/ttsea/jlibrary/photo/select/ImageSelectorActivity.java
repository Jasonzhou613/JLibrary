package com.ttsea.jlibrary.photo.select;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.base.BaseFragmentActivity;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.photo.crop.CropConstants;
import com.ttsea.jlibrary.utils.DisplayUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageSelectorActivity extends BaseFragmentActivity implements View.OnClickListener,
        ImageSelectorFragment.OnImageSelectListener {
    private final String TAG = "Select.ImageSelectorActivity";

    private List<ImageItem> selectedList;
    private ImageConfig imageConfig;

    private View llyTitleBar;
    private Button btnLeft;
    private Button btnRight;
    private TextView tvTitleBarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jimageselector_activity);

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

        btnRight.setBackgroundResource(R.drawable.jphoto_select_ok_btn_selector);
        btnRight.setTextColor(getColorById(imageConfig.getTitleSubmitTextColorRes()));
        tvTitleBarName.setTextColor(getColorById(imageConfig.getTitleTextColorRes()));
        llyTitleBar.setBackgroundColor(getColorById(imageConfig.getTitleBgColorRes()));

        selectedList = imageConfig.getPathList();
        if (selectedList == null) {
            selectedList = new ArrayList<ImageItem>();
        }
        for (int i = 0; i < selectedList.size(); i++) {
            selectedList.get(i).setSelected(true);
        }

        ViewGroup.LayoutParams params = btnRight.getLayoutParams();
        int marginLR = DisplayUtils.dip2px(mActivity, 8);
        int marginTB = (marginLR * 2) / 3;
        if (params != null) {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (params instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) params).setMargins(0, 0, marginLR, 0);
            }
            btnRight.setLayoutParams(params);
            btnRight.setPadding(marginLR, marginTB, marginLR, marginTB);
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
        intent.putExtra(CropConstants.RETURN_DATA, imageConfig.isReturnData());
        intent.putExtra(CropConstants.CAN_MOVE_FRAME, imageConfig.isCanMoveFrame());
        intent.putExtra(CropConstants.CAN_DRAG_FRAME_CONNER, imageConfig.isCanDragFrameConner());
        intent.putExtra(CropConstants.FIXED_ASPECT_RATIO, imageConfig.isFixedAspectRatio());

        startActivityForResult(intent, CropConstants.REQUEST_CODE_CROP_IMAGE);
    }

    private void refreshBtnRightStatus() {
        String txt;
        if (selectedList == null || selectedList.size() == 0) {
            txt = getStringById(R.string.finish);
            btnRight.setEnabled(false);
        } else {
            txt = (getStringById(R.string.finish)) +
                    "(" + selectedList.size() + "/" + imageConfig.getMaxSize() + ")";
            btnRight.setEnabled(true);
        }
        btnRight.setText(txt);

        if (imageConfig.isMutiSelect()) {
            btnRight.setVisibility(View.VISIBLE);
        } else {
            btnRight.setVisibility(View.INVISIBLE);
        }
    }

    private void onBackKeyClicked() {
        selectedList.clear();
        finish(RESULT_CANCELED);
    }

    @Override
    public void onSingleImageSelected(ImageItem image) {
        JLog.d(TAG, "onSingleImageSelected, image:" + image.toString());
        if (imageConfig.isCrop()) {
            crop(new File(image.getPath()));
        } else {
            selectedList.clear();
            selectedList.add(image);
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, (Serializable) selectedList);
            data.putExtras(bundle);
            finish(RESULT_OK, data);
        }
    }

    @Override
    public void onImageSelected(List<ImageItem> list, ImageItem image) {
        JLog.d(TAG, "onImageSelected, add image:" + image.toString());
        selectedList = list;
        refreshBtnRightStatus();
    }

    @Override
    public void onImageUnselected(List<ImageItem> list, ImageItem image) {
        JLog.d(TAG, "onImageSelected, remove image:" + image.toString());
        selectedList = list;
        refreshBtnRightStatus();
    }

    @Override
    public void onRefreshSelectedList(List<ImageItem> selectedList) {
        JLog.d(TAG, "onRefreshSelectedList, selectedList.size:" + selectedList.size());
        this.selectedList = selectedList;
        refreshBtnRightStatus();
    }

    @Override
    public void onCameraShot(File imageFile) {
        JLog.d(TAG, "onCameraShot, filePath:" + imageFile.getAbsolutePath());
        if (imageFile != null) {
            if (imageConfig.isCrop() && !imageConfig.isMutiSelect()) {
                crop(imageFile);
            } else {
                ImageItem item = new ImageItem(imageFile.getAbsolutePath());
                item.setSelected(true);
                selectedList.clear();
                selectedList.add(item);

                Intent data = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, (Serializable) selectedList);
                data.putExtras(bundle);
                finish(RESULT_OK, data);
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
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, (Serializable) selectedList);
                data.putExtras(bundle);
                finish(RESULT_OK, data);
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
                ImageItem item = new ImageItem(data.getData().getPath());
                item.setSelected(true);
                selectedList.clear();
                selectedList.add(item);
                JLog.d(TAG, "crop image back, imagePath:" + data.getData().getPath());

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, (Serializable) selectedList);
                intent.putExtras(bundle);
                finish(RESULT_OK, intent);

            } else if (resultCode == CropConstants.RESULT_CODE_CROP_CANCLED) {
                //toastMessage("取消了剪切图片");
            } else if (resultCode == CropConstants.RESULT_CODE_CROP_ERROR) {
                toastMessage("剪切图片出错");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}