package com.ttsea.jlibrary.photo.crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.utils.BitmapUtils;

/**
 * TransformImageView，涉及到旋转、平移、缩放 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/2/19 16:13 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/2/19 16:13
 */
class TransformImageView extends ImageView {
    private final String TAG = "Crop.TransformImageView";

    /** 是否允许缩放，默认为true */
    private boolean canScale = true;
    /** 是否允许旋转，默认为true */
    private boolean canRotate = true;
    /** 是否允许移动，默认为true */
    private boolean canTranslate = true;

    protected final float[] mCurrentImageCorners = new float[8];
    protected final float[] mCurrentImageCenter = new float[2];
    protected final float[] mMatrixValues = new float[9];

    /**
     * Matrix 3*3 矩阵<br/>
     * _                             _<br/>
     * |  MSCALE_X  MSKEW_X MTRANS_X  |<br/>
     * |  MSKEW_Y  MSCALE_Y MTRANS_Y  |<br/>
     * |_ MPERSP_0 MPERSP_1 MPERSP_2 _|<br/>
     */
    protected Matrix mCurrentImageMatrix;
    protected TransformImageListener mTransformImageListener;
    protected int mThisWidth, mThisHeight;
    private int mMaxBitmapSize = 0;
    private String mImagePath;

    private float[] mInitialImageCorners;
    private float[] mInitialImageCenter;

    public TransformImageView(Context context) {
        this(context, null);
    }

    public TransformImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransformImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TransformImageView);
        initStyleable(a);
        init();
    }

    private void initStyleable(TypedArray a) {
        canScale = a.getBoolean(R.styleable.TransformImageView_canScale, true);
        canRotate = a.getBoolean(R.styleable.TransformImageView_canRotate, true);
        canTranslate = a.getBoolean(R.styleable.TransformImageView_canTranslate, true);

        JLog.d(TAG, "initStyleable, canScale" + canScale
                + ",\n canRotate:" + canRotate
                + ",\n canTranslate:" + canTranslate
        );
    }

    private void init() {
        mCurrentImageMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
    }

    public boolean isCanScale() {
        return canScale;
    }

    public void setCanScale(boolean canScale) {
        this.canScale = canScale;
    }

    public boolean isCanRotate() {
        return canRotate;
    }

    public void setCanRotate(boolean canRotate) {
        this.canRotate = canRotate;
    }

    public boolean isCanTranslate() {
        return canTranslate;
    }

    public void setCanTranslate(boolean canTranslate) {
        this.canTranslate = canTranslate;
    }

    public void setTransformImageListener(TransformImageListener l) {
        this.mTransformImageListener = l;
    }

    /** 设置图片的路径 */
    public void setImagePath(String path) {
        JLog.d(TAG, "setImagePath, path:" + path);
        mImagePath = path;
        setImageBitmap(BitmapUtils.revisionImageSize(path));
    }

    public String getImagePath() {
        return mImagePath;
    }

    public int getMaxBitmapSize() {
        if (mMaxBitmapSize <= 0) {
            mMaxBitmapSize = calculateMaxBitmapSize();
        }
        return mMaxBitmapSize;
    }

    public void setMaxBitmapSize(int maxBitmapSize) {
        this.mMaxBitmapSize = maxBitmapSize;
    }

    @Override
    public void setImageBitmap(final Bitmap bitmap) {
        setImageDrawable(new FastBitmapDrawable(bitmap));
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType);
        } else {
            Log.w(TAG, "Invalid ScaleType. Only ScaleType.MATRIX can be used");
        }
    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
        updateCurrentImagePoints();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            mThisWidth = right - left;
            mThisHeight = bottom - top;

            onImageLayout();
        }
    }

    /**
     * This method calculates maximum size of both width and height of bitmap.
     * It is the device screen diagonal for default implementation.
     *
     * @return - max bitmap size in pixels.
     */
    protected int calculateMaxBitmapSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        int width, height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            width = size.x;
            height = size.y;
        } else {
            width = display.getWidth();
            height = display.getHeight();
        }
        return (int) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
    }

    protected void onImageLayout() {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();

        JLog.d(TAG, String.format("Image size: [%d:%d]", (int) w, (int) h));

        RectF initialImageRect = new RectF(0, 0, w, h);
        mInitialImageCorners = RectUtils.getCornersFromRect(initialImageRect);
        mInitialImageCenter = RectUtils.getCenterFromRect(initialImageRect);
    }

    /**
     * This method updates current image corners and center points that are stored in
     * {@link #mCurrentImageCorners} and {@link #mCurrentImageCenter} arrays.
     * Those are used for several calculations.
     */
    private void updateCurrentImagePoints() {
        //用当前矩阵改变src的值，并且存储到数组dst中
        mCurrentImageMatrix.mapPoints(mCurrentImageCorners, mInitialImageCorners);
        mCurrentImageMatrix.mapPoints(mCurrentImageCenter, mInitialImageCenter);
    }

    /**
     * This method returns Matrix value for given index.
     *
     * @param matrix     - valid Matrix object
     * @param valueIndex - index of needed value. See {@link Matrix#MSCALE_X} and others.
     * @return - matrix value for index
     */
    protected float getMatrixValue(Matrix matrix, int valueIndex) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[valueIndex];
    }

    /**
     * This method calculates scale value for given Matrix object.
     */
    public float getMatrixScale(Matrix matrix) {
        return (float) Math.sqrt(Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X), 2)
                + Math.pow(getMatrixValue(matrix, Matrix.MSKEW_Y), 2));
    }

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    public float getMatrixAngle(Matrix matrix) {
        return (float) -(Math.atan2(getMatrixValue(matrix, Matrix.MSKEW_X),
                getMatrixValue(matrix, Matrix.MSCALE_X)) * (180 / Math.PI));
    }

    public float getCurrentScale() {
        return getMatrixScale(mCurrentImageMatrix);
    }

    public float getCurrentAngle() {
        return getMatrixAngle(mCurrentImageMatrix);
    }

    public Bitmap getBitmap() {
        if (getDrawable() == null || !(getDrawable() instanceof FastBitmapDrawable)) {
            return null;
        } else {
            return ((FastBitmapDrawable) getDrawable()).getBitmap();
        }
    }

    /** 移动 */
    public void postTranslate(float dx, float dy) {
        if ((dx != 0 || dy != 0) && canTranslate) {
            JLog.d(TAG, "postTranslate, dx:" + dx + ", dy:" + dy);
            mCurrentImageMatrix.postTranslate(dx, dy);
            setImageMatrix(mCurrentImageMatrix);
        }
    }

    /** 缩放 */
    public void postScale(float deltaScale, float px, float py) {
        if (deltaScale != 0 && canScale) {
            JLog.d(TAG, "postScale, deltaScale:" + deltaScale + " px:" + px + ", py:" + py);
            mCurrentImageMatrix.postScale(deltaScale, deltaScale, px, py);
            setImageMatrix(mCurrentImageMatrix);
            if (mTransformImageListener != null) {
                mTransformImageListener.onScale(getMatrixScale(mCurrentImageMatrix));
            }
        }
    }

    /** 旋转 */
    public void postRotate(float degrees, float px, float py) {
        if (degrees != 0 && canRotate) {
            JLog.d(TAG, "postRotate, degrees:" + degrees + " px:" + px + ", py:" + py);
            mCurrentImageMatrix.postRotate(degrees, px, py);
            setImageMatrix(mCurrentImageMatrix);
            if (mTransformImageListener != null) {
                mTransformImageListener.onRotate(getMatrixAngle(mCurrentImageMatrix));
            }
        }
    }

    public interface TransformImageListener {

        void onRotate(float currentAngle);

        void onScale(float currentScale);
    }
}
