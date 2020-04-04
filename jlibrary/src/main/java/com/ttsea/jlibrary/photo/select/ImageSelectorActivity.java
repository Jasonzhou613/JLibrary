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
import com.ttsea.jlibrary.base.JBaseActivity;
import com.ttsea.jlibrary.debug.JLog;
import com.ttsea.jlibrary.photo.crop.CropActivity;
import com.ttsea.jlibrary.photo.crop.CropConstants;
import com.ttsea.jlibrary.common.utils.CacheDirUtils;
import com.ttsea.jlibrary.common.utils.DisplayUtils;

import java.io.File;
import java.util.ArrayList;

public class ImageSelectorActivity extends JBaseActivity implements View.OnClickListener,
        ImageSelectorFragment.OnImageSelectListener {
    private final String TAG = "Select.ImageSelectorActivity";

    private ArrayList<ImageItem> selectedList;
    private SelectConfig selectConfig;
    private CropConfig cropConfig;

    private View llyTitleBar;
    private Button btnLeft;
    private Button btnRight;
    private TextView tvTitleBarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jimageselector_activity);

        if (mActivity != null && mActivity.getIntent() != null
                && mActivity.getIntent().getExtras() != null) {
            Bundle bundle = mActivity.getIntent().getExtras();
            selectConfig = (SelectConfig) bundle.getSerializable("selectConfig");
            cropConfig = (CropConfig) bundle.getSerializable("cropConfig");
        }

        if (selectConfig == null) {
            throw new NullPointerException("selectConfig could not be null");
        }

        if (ImageUtils.isEmpty(selectConfig.getOutPutPath())) {
            selectConfig.getBuilder().setOutPutPath(CacheDirUtils.getSdRootDir(mActivity));
        }

        if (cropConfig == null) {
            cropConfig = new CropConfig.Builder().build();
        }
        if (ImageUtils.isEmpty(cropConfig.getOutPutPath())) {
            cropConfig.getBuilder().setOutPutPath(CacheDirUtils.getImageCacheDir(mActivity));
        }

        JLog.d(TAG, "selectConfig:" + selectConfig.toString());
        JLog.d(TAG, "cropConfig:" + cropConfig.toString());

        initView();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, Fragment.instantiate(this, ImageSelectorFragment.class.getName(), null))
                .commit();
    }

    private void initView() {
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        tvTitleBarName = (TextView) findViewById(R.id.tvTitleBarName);
        llyTitleBar = findViewById(R.id.llyTitleBar);

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

        btnRight.setBackgroundResource(R.drawable.jphoto_select_ok_btn_selector);
        btnRight.setTextColor(getColorById(selectConfig.getTitleSubmitTextColorRes()));
        tvTitleBarName.setTextColor(getColorById(selectConfig.getTitleTextColorRes()));
        llyTitleBar.setBackgroundColor(getColorById(selectConfig.getTitleBgColorRes()));

        selectedList = selectConfig.getPathList();
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
            params.height = DisplayUtils.dip2px(mActivity, 30);
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
        //Intent intent = new Intent(CropConstants.ACTION_CROP);
        Intent intent = new Intent(mActivity, CropActivity.class);
        intent.setDataAndType(Uri.fromFile(new File(imagePath)), "image/*");
        intent.putExtra(CropConstants.OUT_PUT_PATH, cropConfig.getOutPutPath());
        intent.putExtra(CropConstants.ASPECT_X, cropConfig.getAspectX());
        intent.putExtra(CropConstants.ASPECT_Y, cropConfig.getAspectY());
        intent.putExtra(CropConstants.OUTPUT_X, cropConfig.getOutputX());
        intent.putExtra(CropConstants.OUTPUT_Y, cropConfig.getOutputY());
        intent.putExtra(CropConstants.CROP_MODEL, cropConfig.getCropModel());
        intent.putExtra(CropConstants.IMAGE_SUFFIX, cropConfig.getImageSuffix());
        intent.putExtra(CropConstants.RETURN_DATA, cropConfig.isReturnData());
        intent.putExtra(CropConstants.CAN_MOVE_FRAME, cropConfig.isCanMoveFrame());
        intent.putExtra(CropConstants.CAN_DRAG_FRAME_CONNER, cropConfig.isCanDragFrameConner());
        intent.putExtra(CropConstants.FIXED_ASPECT_RATIO, cropConfig.isFixedAspectRatio());

        startActivityForResult(intent, CropConstants.REQUEST_CODE_CROP_IMAGE);
    }

    private void refreshBtnRightStatus() {
        String txt;
        if (selectedList == null || selectedList.size() == 0) {
            txt = getStringById(R.string.finish);
            btnRight.setEnabled(false);
        } else {
            txt = (getStringById(R.string.finish)) +
                    "(" + selectedList.size() + "/" + selectConfig.getMaxSize() + ")";
            btnRight.setEnabled(true);
        }
        btnRight.setText(txt);

        if (selectConfig.isMultiSelect()) {
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
        JLog.d(TAG, "image:" + image.toString());
        if (selectConfig.isCrop()) {
            crop(new File(image.getPath()));

        } else {
            selectedList.clear();
            selectedList.add(image);
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, selectedList);
            data.putExtras(bundle);
            finish(RESULT_OK, data);
        }
    }

    @Override
    public void onImageSelected(ArrayList<ImageItem> list, ImageItem image) {
        JLog.d(TAG, "add image:" + image.toString());
        selectedList.clear();
        if (list != null) {
            selectedList.addAll(list);
        }
        refreshBtnRightStatus();
    }

    @Override
    public void onImageUnselected(ArrayList<ImageItem> list, ImageItem image) {
        JLog.d(TAG, "remove image:" + image.toString());
        selectedList.clear();
        if (list != null) {
            selectedList.addAll(list);
        }
        refreshBtnRightStatus();
    }

    @Override
    public void onRefreshSelectedList(ArrayList<ImageItem> list) {
        JLog.d(TAG, "onRefreshSelectedList, list.size:" + list.size());
        selectedList.clear();
        if (list != null) {
            selectedList.addAll(list);
        }
        refreshBtnRightStatus();
    }

    @Override
    public void onCameraShot(File imageFile) {
        JLog.d(TAG, "onCameraShot, filePath:" + imageFile.getAbsolutePath());

        if (selectConfig.isCrop() && !selectConfig.isMultiSelect()) {
            crop(imageFile);
        } else {
            ImageItem item = new ImageItem(imageFile.getAbsolutePath());
            item.setSelected(true);
            selectedList.clear();
            selectedList.add(item);

            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, selectedList);
            data.putExtras(bundle);
            finish(RESULT_OK, data);
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
                bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, selectedList);
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
                    toastMessage(R.string.photo_crop_image_error);
                    return;
                }
                ImageItem item = new ImageItem(data.getData().getPath());
                item.setSelected(true);
                selectedList.clear();
                selectedList.add(item);
                JLog.d(TAG, "crop image back, imagePath:" + data.getData().getPath());

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, selectedList);
                intent.putExtras(bundle);
                finish(RESULT_OK, intent);

            } else if (resultCode == CropConstants.RESULT_CODE_CROP_CANCLED) {
                //toastMessage("取消了剪切图片");

            } else if (resultCode == CropConstants.RESULT_CODE_CROP_ERROR) {
                toastMessage(R.string.photo_crop_image_error);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}