package com.ttsea.jlibrary.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.common.JImageLoader;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.photo.crop.CropView;
import com.ttsea.jlibrary.photo.gallery.GalleryActivity;
import com.ttsea.jlibrary.photo.gallery.GalleryConstants;
import com.ttsea.jlibrary.photo.select.ImageConfig;
import com.ttsea.jlibrary.photo.select.ImageItem;
import com.ttsea.jlibrary.photo.select.ImageSelector;
import com.ttsea.jlibrary.utils.CacheDirUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    private ImageView ivImage;
    private TextView tvImagePath;
    private List<ImageItem> selectedList;

    private final int REQUEST_CODE_SELECT_PIC = 100;
    private final int REQUEST_CODE_BROWSE_PIC = 101;

    private final String[] testImages = new String[]{
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/859a27c6a61224fdbc140bf7b6f99f4d",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/68ec5ce7a7694413e367cb2b22bbea49__",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/8c748752d963790074bfae4302d0a84f",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/7f39e67fb78adcbaad955b4466f74fe5",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/8715b4585a1c5f7000b40fe0cbc243df",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/677ef48b95d71957495a745bc6e64263",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/fc90c4c9bb54dae3d63642cf20cbbc12"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_main);

        initView();
        initData();
    }

    private void initView() {
        btnSelect = (Button) findViewById(R.id.btnSelect);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvImagePath = (TextView) findViewById(R.id.tvImagePath);

        btnSelect.setOnClickListener(this);
        ivImage.setOnClickListener(this);
    }

    private void initData() {
        selectedList = new ArrayList<>();
    }

    private void onSelectImageBack(List<ImageItem> list) {
        if (list == null) {
            JImageLoader.getInstance().displayImage(mActivity, null, ivImage);
            tvImagePath.setText("");
            toastMessage("选择图片出错");
            return;
        }
        JLog.d(TAG, "list size:" + list.size());
        if (list.size() < 1) {
            JImageLoader.getInstance().displayImage(mActivity, null, ivImage);
            tvImagePath.setText("");
            toastMessage("未选择图片");
            return;
        }

        JImageLoader.getInstance().displayImage(mActivity, "file://" + list.get(0).getPath(), ivImage);
        String paths = "";
        for (int i = 0; i < list.size(); i++) {
            paths = paths + "\n" + list.get(i).getPath();
        }
        tvImagePath.setText(paths);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {

            case R.id.btnSelect:
                selectPhoto();
                break;

            case R.id.ivImage:
                browseImages(selectedList);
                break;

            default:
                break;
        }
    }

    private void browseImages(List<ImageItem> listImage) {
        selectedList.clear();
        for (int i = 0; i < testImages.length; i++) {
            ImageItem item = new ImageItem(testImages[i]);
            selectedList.add(item);
        }

        if (listImage == null || listImage.size() < 1) {
            toastMessage(R.string.image_no_picture);
            return;
        }
        Intent intent = new Intent(mActivity, GalleryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GalleryConstants.KEY_SELECTED_LIST, (Serializable) listImage);
        bundle.putInt(GalleryConstants.KEY_SELECTED_POSITION, 0);
        bundle.putBoolean(GalleryConstants.KEY_CAN_SAVE, true);
        bundle.putBoolean(GalleryConstants.KEY_CAN_DEL, true);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_BROWSE_PIC);
    }

    private void selectPhoto() {
        ImageConfig config = new ImageConfig.Builder(this)
                .setMutiSelect(true)//设置是否是多选，默认为：true
                .setMaxSize(9)//多选时，最多可选数量，默认为：9
                .setShowCamera(true)//是否显示拍照项，认为：true
                //请求code，用于onActivityResult接收，默认为：ImageSelector.TAKE_PHOTO_BY_GALLERY
                .setRequestCode(REQUEST_CODE_SELECT_PIC)
                .setPathList(selectedList)

                .setCrop(false)//设置是否需要剪切,默认为：false，单选时生效
                //设置剪切图片的输出路径
                .setOutPutPath(CacheDirUtils.getTempDir(mActivity) + File.separator + "photo")
                .setAspectX(4)//设置X比例
                .setAspectY(3)//设置Y比例
                .setOutputX(500)//设置保存图片X最大值
                .setOutputY(500)//设置保存图片Y最大值
                //设置剪切模式，默认为(CROP_MODE_RECTANGLE)矩形
                .setCropModel(CropView.CROP_MODE_RECTANGLE)
                .setImageSuffix(".nomedia")//设置剪切图片后保存和拍照保存后的后缀名，默认为".jpg"
                .setReturnData(false)//是否返回数据
                .setFixedAspectRatio(true)//设置是否保持剪切比例，默认为true
                .setCanMoveFrame(false)//按住剪切框中间，是否可以拖动整个剪切框, 默认为false
                .setCanDragFrameConner(false)//按住剪切框四个角，是否可以拖动剪切框的四个角

                .build();

        JLog.d(TAG, "config:" + config.toString());

        ImageSelector.open(mActivity, config);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_PIC) {//选择图片回来
            if (resultCode == Activity.RESULT_CANCELED) {
                toastMessage("你取消了选择图片");
            } else if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    toastMessage("选择图片出错");
                    return;
                }

                List<ImageItem> list = (List<ImageItem>) data.getExtras()
                        .getSerializable(ImageSelector.KEY_SELECTED_LIST);
                if (list != null) {
                    selectedList.clear();
                    selectedList.addAll(list);
                }
                onSelectImageBack(selectedList);
            }
        } else if (requestCode == REQUEST_CODE_BROWSE_PIC && data != null) {//浏览图片回来
            List<ImageItem> list = (List<ImageItem>) data.getExtras()
                    .getSerializable(GalleryConstants.KEY_SELECTED_LIST);
            if (list != null) {
                selectedList.clear();
                selectedList.addAll(list);
            }
            onSelectImageBack(selectedList);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
