package com.ttsea.jlibrary.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.common.ImageLoader;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.photo.crop.CropView;
import com.ttsea.jlibrary.photo.select.ImageConfig;
import com.ttsea.jlibrary.photo.select.ImageItem;
import com.ttsea.jlibrary.photo.select.ImageSelector;
import com.ttsea.jlibrary.utils.CacheDirUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * //To do <br/>
 * <p>
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_main);

        initView();
    }

    private void initView() {
        btnSelect = (Button) findViewById(R.id.btnSelect);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvImagePath = (TextView) findViewById(R.id.tvImagePath);

        btnSelect.setOnClickListener(this);
    }

    private void onSelectImageBack(ArrayList<String> list) {
        if (list == null) {
            toastMessage("选择图片出错");
            return;
        }
        if (list.size() < 1) {
            toastMessage("未选择图片");
            return;
        }

        ImageLoader.getInstance().displayImage(mActivity, "file://" + list.get(0), ivImage);
        String paths = "";
        for (int i = 0; i < list.size(); i++) {
            paths = paths + "\n" + list.get(i);
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

            default:
                break;
        }
    }

//    private boolean mutiSelect = true;
//    private boolean showCamera = true;
//    private int maxSize = 9;
//
//    private int titleBgColor = 0xFF000000;
//    private int titleNameTextColor = 0xFFFFFFFF;
//    private int titleOKTextColor = 0xFFFFFFFF;
//    private int steepToolBarColor = 0xFF000000;
//
//    private int requestCode = ImageSelector.TAKE_PHOTO_BY_GALLERY;
//
//    private ArrayList<String> pathList;
//
//    //剪切图片常量
//    private boolean crop = false;
//    private String outPutPath;
//    private String imageSuffix;
//    private int aspectX;
//    private int aspectY;
//    private int outputX;
//    private int outputY;
//    private int cropModel = CropView.CROP_MODE_RECTANGLE;
//    private boolean returnData = false;
//    private boolean fixedAspectRatio = true;
//    private boolean canMoveFrame = false;
//    private boolean canDragFrameConner = false;

    private void selectPhoto() {
        ImageConfig config = new ImageConfig.Builder(this)
                .setMutiSelect(false)//设置是否是多选
                .setMaxSize(9)//多选时，最多可选数量
                .setShowCamera(true)//是否显示拍照项
                .setRequestCode(100)//请求code，用于onActivityResult接收

                .setCrop(true)//设置是否需要剪切
                //设置剪切图片的输出路径
                .setOutPutPath(CacheDirUtils.getTempDir(mActivity) + File.separator + "photo")
                .setAspectX(4)//设置X比例
                .setAspectY(3)//设置Y比例
                .setOutputX(500)//设置保存图片X最大值
                .setOutputY(500)//设置保存图片Y最大值
                //设置剪切模式，默认为(CROP_MODE_RECTANGLE)矩形
                .setCropModel(CropView.CROP_MODE_RECTANGLE)
                .setImageSuffix(".nomedia")//剪切图片后保存的后缀，默认为".jpg"
                .setReturnData(false)//是否返回数据
                .setFixedAspectRatio(false)//设置是否保持剪切比例，默认为true
                .setCanMoveFrame(false)
                .setCanDragFrameConner(false)

                .build();

        JLog.d(TAG, "config:" + config.toString());

        ImageSelector.open(mActivity, config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_CANCELED) {
                toastMessage("你取消了选择图片");
            } else if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    toastMessage("选择图片出错");
                    return;
                }
                ArrayList<String> list = data.getStringArrayListExtra(ImageSelector.KEY_SELECTED_LIST);
                onSelectImageBack(list);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
