package com.ttsea.jlibrary.photo.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.base.JBaseActivity;
import com.ttsea.jlibrary.common.bitmap.BitmapOption;
import com.ttsea.jlibrary.common.bitmap.BitmapUtils;
import com.ttsea.jlibrary.common.utils.CacheDirUtils;
import com.ttsea.jlibrary.common.utils.DateUtils;
import com.ttsea.jlibrary.common.utils.Utils;
import com.ttsea.jlibrary.debug.JLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 剪切图片Activity <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class CropActivity extends JBaseActivity implements View.OnClickListener {
    private final String TAG = "Crop.CropActivity";

    private CropView cropView;
    private CropImageView ivCropImageView;

    private Button btnLeft;
    private Button btnRight;
    private TextView tvTitleBarName;
    private View llyCropCancel;
    private View llyCropSure;
    private ProgressBar pbProgress;

    private String imagePath;
    private String outPutPath;
    private String imageSuffix;
    private int aspectX;
    private int aspectY;
    private int outputX;
    private int outputY;
    private int cropModel;
    private boolean return_data;
    private boolean canMoveFrame = false;
    private boolean canDragFrameConner = false;
    private boolean fixedAspectRatio;

    private final int DEFAULT_INT = 0;
    private final String DEFAULT_SUFFIX = ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jcrop_activity);

        initParams();
        initView();
    }

    /** 初始化一些参数 */
    private void initParams() {
        if (getIntent() != null && getIntent().getData() != null) {
            imagePath = getIntent().getData().getPath();
            File file = new File(imagePath);
            if (!file.exists()) {
                imagePath = null;
            }
        }
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            outPutPath = bundle.getString(CropConstants.OUT_PUT_PATH);
            imageSuffix = bundle.getString(CropConstants.IMAGE_SUFFIX);
            aspectX = bundle.getInt(CropConstants.ASPECT_X, DEFAULT_INT);
            aspectY = bundle.getInt(CropConstants.ASPECT_Y, DEFAULT_INT);
            outputX = bundle.getInt(CropConstants.OUTPUT_X, DEFAULT_INT);
            outputY = bundle.getInt(CropConstants.OUTPUT_Y, DEFAULT_INT);
            cropModel = bundle.getInt(CropConstants.CROP_MODEL, CropView.CROP_MODE_RECTANGLE);
            return_data = bundle.getBoolean(CropConstants.RETURN_DATA, false);
            fixedAspectRatio = bundle.getBoolean(CropConstants.FIXED_ASPECT_RATIO, true);
            canMoveFrame = bundle.getBoolean(CropConstants.CAN_MOVE_FRAME, false);
            canDragFrameConner = bundle.getBoolean(CropConstants.CAN_DRAG_FRAME_CONNER, false);

            if (Utils.isEmpty(imagePath)) {
                imagePath = bundle.getString(CropConstants.IMAGE_PATH);
            }
            if (Utils.isEmpty(imageSuffix)) {
                imageSuffix = DEFAULT_SUFFIX;
            }
        }

        if (Utils.isEmpty(imagePath)) {
            toastMessage("图片路径有误");
            JLog.e(TAG, "imagePath error, imagePath:" + imagePath);
            finish();
            return;
        }

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            toastMessage("图片不存在");
            JLog.e(TAG, "Image not exists, imagePath:" + imagePath);
            finish();
            return;
        }

        if (Utils.isEmpty(outPutPath)) {
            outPutPath = CacheDirUtils.getSdTempDir(mActivity);
        }

        if (aspectX < 0) {
            JLog.e(TAG, "aspectX should be greater than or equal to 0, aspectX:" + aspectX);
            finish();
            throw (new IllegalArgumentException("aspectX should be greater than or equal to 0"));
        }

        if (aspectY < 0) {
            JLog.e(TAG, "aspectY should be greater than or equal to 0, aspectY:" + aspectY);
            finish();
            throw (new IllegalArgumentException("aspectY should be greater than or equal to 0"));
        }

        if (outputX < 0) {
            JLog.e(TAG, "outputX should be greater than or equal to 0, outputX:" + outputX);
            finish();
            throw (new IllegalArgumentException("outputX should be greater than or equal to 0"));
        }

        if (outputY < 0) {
            JLog.e(TAG, "outputY should be greater than or equal to 0, outputY:" + outputY);
            finish();
            throw (new IllegalArgumentException("outputY should be greater than or equal to 0"));
        }
    }

    private void initView() {
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        tvTitleBarName = (TextView) findViewById(R.id.tvTitleBarName);
        llyCropCancel = findViewById(R.id.llyCropCancel);
        llyCropSure = findViewById(R.id.llyCropSure);
        pbProgress = (ProgressBar) findViewById(R.id.pbProgress);

        cropView = (CropView) findViewById(R.id.cropView);
        ivCropImageView = (CropImageView) findViewById(R.id.ivCropImageView);

        tvTitleBarName.setText(getStringById(R.string.crop_crop_image));
        btnRight.setVisibility(View.INVISIBLE);
        btnLeft.setOnClickListener(this);
        llyCropCancel.setOnClickListener(this);
        llyCropSure.setOnClickListener(this);

        ivCropImageView.setImagePath(imagePath);
        ivCropImageView.setCropView(cropView);
        if (outputX > 0 && outputY > 0) {
            ivCropImageView.setMaxResultImageSizeX(outputX);
            ivCropImageView.setMaxResultImageSizeY(outputY);
        }

        if (aspectX > 0 && aspectY > 0) {
            cropView.setAspectX(aspectX);
            cropView.setAspectY(aspectY);
        }
        cropView.setFixedAspectRatio(fixedAspectRatio);
        cropView.setCropMode(CropView.CROP_MODE_RECTANGLE);
        cropView.setCropImageView(ivCropImageView);
        cropView.setCropMode(cropModel);
        cropView.setCanMoveFrame(canMoveFrame);
        cropView.setCanDragFrameConner(canDragFrameConner);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish(CropConstants.RESULT_CODE_CROP_CANCLED);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLeft
                || v.getId() == R.id.llyCropCancel) {//取消剪切
            finish(CropConstants.RESULT_CODE_CROP_CANCLED);

        } else if (v.getId() == R.id.llyCropSure) {
            new CropImageTask().execute();
        }
    }

    /**
     * 将bitmap保存为图片，如果保存成功则返回图片保存的uri，否则返回空
     *
     * @param bitmap Bitmap
     * @return Uri
     */
    private Uri saveBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Uri uri = null;
        try {
            String fileName = "crop_" + DateUtils.getCurrentTime("yyyyMMdd_HHmmss") + imageSuffix;
            File f = new File(outPutPath, fileName);
            if (f.exists()) {
                f.delete();
            }
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            JLog.d(TAG, "saveBitmap, path:" + f.getAbsolutePath());

            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

            uri = Uri.fromFile(f);

        } catch (FileNotFoundException e) {
            JLog.e(TAG, "FileNotFoundException， e:" + e.toString());
        } catch (IOException e) {
            JLog.e(TAG, "IOException， e:" + e.toString());
        } catch (Exception e) {
            JLog.e(TAG, "Exception， e:" + e.toString());
        }

        return uri;
    }

    class CropImageTask extends AsyncTask<Void, Void, Uri> {
        private Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            //showDialog("正在保存...", false);
            pbProgress.setVisibility(View.VISIBLE);
            bitmap = ivCropImageView.crop();
        }

        @Override
        protected Uri doInBackground(Void... params) {
            Uri uri = saveBitmap(bitmap);
            if (return_data) {
                BitmapOption.Builder builder = new BitmapOption.Builder();
                builder.setMaxWidth(156)
                        .setMaxHeight(156)
                        .setMaxSizeInKB(100)
                        .setConfig(Bitmap.Config.RGB_565)
                        .setLockRatio(true);

                try {
                    bitmap = BitmapUtils.compressBitmap(bitmap, builder.build());

                } catch (Exception e) {
                    JLog.w("Exception e:" + e.getMessage());
                }
            }
            return uri;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            dismissDialog();
            pbProgress.setVisibility(View.GONE);
            if (uri != null) {
                Intent intent = new Intent();
                intent.setData(uri);
                if (return_data) {
                    intent.putExtra(CropConstants.DATA, bitmap);
                }
                finish(CropConstants.RESULT_CODE_CROP_OK, intent);
            } else {
                finish(CropConstants.RESULT_CODE_CROP_ERROR);
            }
        }
    }
}
