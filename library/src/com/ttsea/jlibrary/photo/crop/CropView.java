package com.ttsea.jlibrary.photo.crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JLog;

/**
 * 剪切框 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/2/19 16:16 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/2/19 16:16
 */
public class CropView extends View {
    private final String TAG = "Crop.CropView";

    /** 剪切模式，矩形 */
    public static final int CROP_MODE_RECTANGLE = 0x01;
    /*** 剪切模式，椭圆 */
    public static final int CROP_MODE_OVAL = 0x02;

    /** 剪切线的显示模式，不显示 */
    private static final int LINE_SHOW_MODE_NOT_SHOW = 0x01;
    /** 剪切线的显示模式，一直显示 */
    private static final int LINE_SHOW_MODE_SHOW_ALWAYS = 0x02;
    /** 剪切线的显示模式，触摸时显示 */
    private static final int LINE_SHOW_MODE_SHOW_ON_TOUCH = 0x03;

    //默认值 start----------
    private final int DEFAULT_LINE_COLOR = 0xFF00CC00;
    private final int DEFAULT_FRAME_SHADOW_COLOR = 0xBC000000;
    private final int DEFAULT_FRAME_LINE_WIDTH = 4;
    private final int DEFAULT_HANDLE_LINE_WIDTH = 6;
    private final int DEFAULT_HANDLE_LINE_HEIGHT = 60;
    private final int DEFAULT_GUIDE_LINE_WIDTH = 3;
    private final int DEFAULT_GRID_ROW_COUNT = 2;
    private final int DEFAULT_GRID_COLUMN_COUNT = 2;
    //默认值 end----------

    //属性 start----------
    /** 剪切模式 */
    private int cropMode = CROP_MODE_RECTANGLE;

    /** 剪切框的最小值, 是 handleLineWidth的两倍 */
    private int minFrameWidth;
    /** 剪切边框的显示模式 */
    private int frameLineShowMode = LINE_SHOW_MODE_SHOW_ALWAYS;
    /** 剪切边框的颜色 */
    private int frameLineColor = DEFAULT_LINE_COLOR;
    /** 剪切边框的宽度px */
    private int frameLineWidth = DEFAULT_FRAME_LINE_WIDTH;

    /** 四个角的手柄线显示模式 */
    private int handleLineShowMode = LINE_SHOW_MODE_SHOW_ALWAYS;
    /** 四个角的手柄线的颜色 */
    private int handleLineColor = DEFAULT_LINE_COLOR;
    /** 四个角的手柄线的宽度px */
    private int handleLineWidth = DEFAULT_HANDLE_LINE_WIDTH;
    /** 四个角的手柄线的高度px */
    private int handleLineLength = DEFAULT_HANDLE_LINE_HEIGHT;

    /** 网格线的显示模式 */
    private int gridLineShowMode = LINE_SHOW_MODE_SHOW_ON_TOUCH;
    /** 网格线的颜色 */
    private int gridLineColor = DEFAULT_LINE_COLOR;
    /** 网格线的宽度px */
    private int gridLineWidth = DEFAULT_GUIDE_LINE_WIDTH;
    /** 网格行数 */
    private int gridRowCount = DEFAULT_GRID_ROW_COUNT;
    /** 网格列数 */
    private int gridColumnCount = DEFAULT_GRID_COLUMN_COUNT;

    /** 剪切时阴影部分的颜色 */
    private int frameShadowColor = DEFAULT_FRAME_SHADOW_COLOR;

    /** 按住剪切框中间，是否可以拖动整个剪切框, 默认为false */
    private boolean canMoveFrame = false;
    /** 按住剪切框四个角，是否可以拖动剪切框的四个角 */
    private boolean canDragFrameConner = false;
    /** 是否需要保持长宽比，默认为保持，只有当canDragFrameConner未true时生效 */
    private boolean fixedAspectRatio = true;
    private int aspectX = 1;
    private int aspectY = 1;
    //属性 end----------

    // 全局变量
    private Paint framePaint;
    private Paint handlePaint;
    private Paint gridPaint;
    private Rect frameRect;
    private Rect boundsRect;

    private int viewWidth = 0;
    private int viewHeight = 0;

    /** 记录用户是否正在触摸屏幕 */
    private boolean isOnTouching = false;
    private boolean isTouchOnCorner = false;
    private boolean isTouchOnBounds = false;
    private boolean isTouchOnFrameInside = false;

    private float downX = 0;
    private float downY = 0;
    private CropImageView cropImageView;
    private TouchRange touchRange;

    /** 触摸方位 */
    public enum TouchRange {
        LEFT_TOP_RECT,//左上角
        LEFT_BOTTOM_RECT,//左下角
        RIGHT_TOP_RECT,//右上角
        RIGHT_BOTTOM_RECT,//右下角
        TOP_LINE,//上边
        BOTTOM_LINE,//下边
        LEFT_LINE,//左边
        RIGHT_LINE,//右边
        ON_FRAME,//剪切框以内
        OUT_FRAME,//剪切框以外
        UNKNOWN//未知
    }

    public CropView(Context context) {
        this(context, null);
    }

    public CropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CropView);
        initStyleable(a);
        init();
    }

    public int getAspectX() {
        return aspectX;
    }

    public void setAspectX(int aspectX) {
        if (aspectX <= 0) {
            aspectX = 1;
        }
        this.aspectX = aspectX;
    }

    public int getAspectY() {
        return aspectY;
    }

    public void setAspectY(int aspectY) {
        if (aspectY <= 0) {
            aspectY = 1;
        }
        this.aspectY = aspectY;
    }

    public int getCropMode() {
        return cropMode;
    }

    public void setCropMode(int cropMode) {
        if (cropMode != CROP_MODE_RECTANGLE && cropMode != CROP_MODE_OVAL) {
            cropMode = CROP_MODE_RECTANGLE;
        }
        this.cropMode = cropMode;
    }

    public boolean isFixedAspectRatio() {
        return fixedAspectRatio;
    }

    public void setFixedAspectRatio(boolean fixedAspectRatio) {
        this.fixedAspectRatio = fixedAspectRatio;
    }

    public boolean isCanMoveFrame() {
        return canMoveFrame;
    }

    public void setCanMoveFrame(boolean canMoveFrame) {
        this.canMoveFrame = canMoveFrame;
    }

    public boolean isCanDragFrameConner() {
        return canDragFrameConner;
    }

    public void setCanDragFrameConner(boolean canDragFrameConner) {
        this.canDragFrameConner = canDragFrameConner;
    }

    public Rect getFrameRect() {
        return frameRect;
    }

    public CropImageView getCropImageView() {
        return cropImageView;
    }

    public void setCropImageView(CropImageView cropImageView) {
        this.cropImageView = cropImageView;
    }

    private void initStyleable(TypedArray a) {
        // getDimension
        // 获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘   返回float
        // getDimensionPixelOffset
        // 获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘  返回int
        // getDimensionPixelSize
        // 则不管写的是dp还是sp还是px,都会乘以denstiy.

        cropMode = a.getInt(R.styleable.CropView_cv_cropMode, CROP_MODE_RECTANGLE);

        frameLineShowMode = a.getInt(R.styleable.CropView_cv_frameLineShowMode, LINE_SHOW_MODE_SHOW_ALWAYS);
        frameLineColor = a.getColor(R.styleable.CropView_cv_frameLineColor, DEFAULT_LINE_COLOR);
        frameLineWidth = a.getDimensionPixelOffset(R.styleable.CropView_cv_frameLineWidth, DEFAULT_FRAME_LINE_WIDTH);

        handleLineShowMode = a.getInt(R.styleable.CropView_cv_handleLineShowMode, LINE_SHOW_MODE_SHOW_ALWAYS);
        handleLineColor = a.getColor(R.styleable.CropView_cv_handleLineColor, DEFAULT_LINE_COLOR);
        handleLineWidth = a.getDimensionPixelOffset(R.styleable.CropView_cv_handleLineWidth, DEFAULT_HANDLE_LINE_WIDTH);
        handleLineLength = a.getDimensionPixelOffset(R.styleable.CropView_cv_handleLineLength, DEFAULT_HANDLE_LINE_HEIGHT);

        gridLineShowMode = a.getInt(R.styleable.CropView_cv_gridLineShowMode, LINE_SHOW_MODE_SHOW_ON_TOUCH);
        gridLineColor = a.getColor(R.styleable.CropView_cv_gridLineColor, DEFAULT_LINE_COLOR);
        gridLineWidth = a.getDimensionPixelOffset(R.styleable.CropView_cv_gridLineWidth, DEFAULT_GUIDE_LINE_WIDTH);
        gridRowCount = a.getInt(R.styleable.CropView_cv_gridRowCount, DEFAULT_GRID_ROW_COUNT);
        gridColumnCount = a.getInt(R.styleable.CropView_cv_gridColumnCount, DEFAULT_GRID_COLUMN_COUNT);

        frameShadowColor = a.getColor(R.styleable.CropView_cv_frameShadowColor, DEFAULT_FRAME_SHADOW_COLOR);

        canMoveFrame = a.getBoolean(R.styleable.CropView_cv_canMoveFrame, false);
        canDragFrameConner = a.getBoolean(R.styleable.CropView_cv_canDragFrameConner, false);
        fixedAspectRatio = a.getBoolean(R.styleable.CropView_cv_fixedAspectRatio, true);

        minFrameWidth = handleLineLength * 2 + 5;
        if (minFrameWidth <= 0) {
            minFrameWidth = 20;
        }

        JLog.d(TAG, "cropMode:" + getCropModeByInt(cropMode)
                + ",\n minFrameWidth:" + minFrameWidth
                + ",\n frameLineShowMode:" + getLineShowModeByInt(frameLineShowMode)
                + ",\n frameLineColor:" + frameLineColor
                + ",\n frameLineWidth:" + frameLineWidth
                + ",\n handleLineShowMode:" + getLineShowModeByInt(handleLineShowMode)
                + ",\n handleLineColor:" + handleLineColor
                + ",\n handleLineWidth:" + handleLineWidth
                + ",\n handleLineLength:" + handleLineLength
                + ",\n gridLineShowMode:" + getLineShowModeByInt(gridLineShowMode)
                + ",\n gridLineColor:" + gridLineColor
                + ",\n gridLineWidth:" + gridLineWidth
                + ",\n gridRowCount:" + gridRowCount
                + ",\n gridColumnCount:" + gridColumnCount
                + ",\n frameShadowColor:" + frameShadowColor
        );
    }

    private String getCropModeByInt(int cropMode) {
        switch (cropMode) {
            case CROP_MODE_OVAL:
                return "oval";
            case CROP_MODE_RECTANGLE:
                return "rectangle";
            default:
                return "unKnow";
        }
    }

    private String getLineShowModeByInt(int lineShowMode) {
        switch (lineShowMode) {
            case LINE_SHOW_MODE_NOT_SHOW:
                return "not_show";
            case LINE_SHOW_MODE_SHOW_ALWAYS:
                return "show_always";
            case LINE_SHOW_MODE_SHOW_ON_TOUCH:
                return "show_on_touch";
            default:
                return "unKnow";
        }
    }

    private void init() {
        framePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        framePaint.setColor(frameLineColor);
        framePaint.setStrokeWidth(frameLineWidth);
        framePaint.setStyle(Paint.Style.STROKE);

        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(handleLineColor);
        handlePaint.setStrokeWidth(handleLineWidth);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(gridLineColor);
        gridPaint.setStrokeWidth(gridLineWidth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            viewWidth = right - left;
            viewHeight = bottom - top;
            boundsRect = new Rect(left, top, right, bottom);
            JLog.d(TAG, "onLayout, viewWidth:" + viewWidth + ", viewHeight:" + viewHeight
                    + ", boundsRect:" + boundsRect.toString());
            setupFrameBounds();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFrameShadow(canvas);
        drawFrame(canvas);
        drawHandleLine(canvas);
        drawGride(canvas);
        //canvas.drawRect(tempRect, framePaint);
    }

    private void setupFrameBounds() {
        if (frameRect == null) {
            frameRect = new Rect();
        }

        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        int pr = getPaddingRight();
        int pb = getPaddingBottom();

        int frameWidth;
        int frameHeight;
        if (viewWidth < viewHeight) {
            frameWidth = viewWidth;
            frameHeight = viewWidth;
        } else {
            frameWidth = viewHeight;
            frameHeight = viewHeight;
        }

        if (frameWidth < minFrameWidth) {
            JLog.e(TAG, "Invalid minFrameWidth, it should less than frameWidth");
            frameWidth = minFrameWidth;
        }
        if (frameHeight < minFrameWidth) {
            JLog.e(TAG, "Invalid minFrameWidth, it should less than frameHeight");
            frameHeight = minFrameWidth;
        }

//        if (fixedAspectRatio) {
        frameWidth = (int) (frameWidth * 0.9f);
        frameHeight = (int) (frameHeight * 0.9f);
        float ratio = (float) aspectX / (float) aspectY;
        float maxRatio = ((float) frameWidth) / ((float) minFrameWidth);
        float minRatio = ((float) minFrameWidth) / ((float) frameWidth);

        if (ratio > maxRatio) {
            JLog.e(TAG, "Invalid ratio, the ratio should less than " + maxRatio + ", will set ratio to " + maxRatio);
            ratio = maxRatio;
        }
        if (ratio < minRatio) {
            JLog.e(TAG, "Invalid ratio, the ratio should greater than " + minRatio + ", will set ratio to " + minRatio);
            ratio = minRatio;
        }

        if (ratio < 1) {
            frameWidth = (int) (frameHeight * ratio);
        } else {
            frameHeight = (int) (frameWidth / ratio);
        }
//        } else {
//            frameWidth = frameWidth - 100;
//            frameHeight = frameHeight - 100;
//        }

        int offsetX = (viewWidth - frameWidth) / 2;
        int offsetY = (viewHeight - frameHeight) / 2;

        frameRect.left = pl + offsetX;
        frameRect.top = pt + offsetY;
        frameRect.right = pl + frameWidth + offsetX;
        frameRect.bottom = pt + frameHeight + offsetY;

        if (cropImageView != null) {
            cropImageView.setCropRect(frameRect);
        }

        JLog.d(TAG, "setupFrameBounds, viewWidth:" + viewWidth
                + ",\n viewHeight:" + viewHeight
                + ",\n frameWidth:" + frameWidth
                + ",\n frameHeight:" + frameHeight
                + ",\n paddingLeft:" + pl
                + ",\n paddingTop:" + pt
                + ",\n paddingRight:" + pr
                + ",\n paddingBottom:" + pb
                + ",\n offsetX:" + offsetX
                + ",\n offsetY:" + offsetY
                + ",\n frameRect.toString:" + frameRect.toString()
                + ",\n fixedAspectRatio:" + fixedAspectRatio
                + ",\n canMoveFrame:" + canMoveFrame
                + ",\n canDragFrameConner:" + canDragFrameConner
                + ",\n aspectX:" + aspectX
                + ",\n aspectY:" + aspectY
        );
    }


    /** 绘制裁剪框，即：frameLine */
    private void drawFrame(Canvas canvas) {
        if (frameRect == null
                || !isNeedShowFrameLine()) {
            return;
        }
        canvas.drawRect(frameRect, framePaint);
    }

    /** 绘画表格，即：gridLine */
    private void drawGride(Canvas canvas) {
        if (frameRect == null
                || !isNeedShowGrideLine()) {
            return;
        }

        int frameWith = frameRect.right - frameRect.left;
        int frameHeight = frameRect.bottom - frameRect.top;
        int columnSpace = (frameWith - (gridColumnCount * gridLineWidth)) / (gridColumnCount + 1);
        int rowSpace = (frameHeight - (gridRowCount * gridLineWidth)) / (gridRowCount + 1);

        for (int i = 0; i < gridColumnCount; i++) {
            int offset = columnSpace * (i + 1) + gridLineWidth * i;
            float startX = (float) (frameRect.left + offset);
            float startY = (float) (frameRect.top);
            float stopX = (float) (frameRect.left + offset);
            float stopY = (float) (frameRect.bottom);
            canvas.drawLine(startX, startY, stopX, stopY, gridPaint);
        }

        for (int i = 0; i < gridRowCount; i++) {
            int offset = rowSpace * (i + 1) + gridLineWidth * i;
            float startX = (float) (frameRect.left);
            float startY = (float) (frameRect.top + offset);
            float stopX = (float) (frameRect.right);
            float stopY = (float) (frameRect.top + offset);
            canvas.drawLine(startX, startY, stopX, stopY, gridPaint);
        }
    }

    /** 绘制四个角的手柄线即：handleLine */
    private void drawHandleLine(Canvas canvas) {
        if (frameRect == null
                || !isNeedShowHandleLine()) {
            return;
        }

        final float lateralOffset = handleLineWidth / 2f;
        final float startOffset = handleLineWidth;

        //左上角，上边
        canvas.drawLine(frameRect.left - startOffset, frameRect.top - lateralOffset,
                frameRect.left - startOffset + handleLineLength, frameRect.top - lateralOffset, handlePaint);
        //左上角，左边
        canvas.drawLine(frameRect.left - lateralOffset, frameRect.top - startOffset,
                frameRect.left - lateralOffset, frameRect.top - lateralOffset + handleLineLength, handlePaint);

        //右上角，上边
        canvas.drawLine(frameRect.right - handleLineLength + startOffset, frameRect.top - lateralOffset,
                frameRect.right + startOffset, frameRect.top - lateralOffset, handlePaint);
        //右上角，右边
        canvas.drawLine(frameRect.right + lateralOffset, frameRect.top - startOffset,
                frameRect.right + lateralOffset, frameRect.top - lateralOffset + handleLineLength, handlePaint);

        //左下角，下边
        canvas.drawLine(frameRect.left - startOffset, frameRect.bottom + lateralOffset,
                frameRect.left - startOffset + handleLineLength, frameRect.bottom + lateralOffset, handlePaint);
        //左下角，左边
        canvas.drawLine(frameRect.left - lateralOffset, frameRect.bottom - handleLineLength + startOffset,
                frameRect.left - lateralOffset, frameRect.bottom + startOffset, handlePaint);

        //右下角，下边
        canvas.drawLine(frameRect.right - handleLineLength + startOffset, frameRect.bottom + lateralOffset,
                frameRect.right + startOffset, frameRect.bottom + lateralOffset, handlePaint);
        //右下角，右边
        canvas.drawLine(frameRect.right + lateralOffset, frameRect.bottom - handleLineLength + startOffset,
                frameRect.right + lateralOffset, frameRect.bottom + startOffset, handlePaint);
    }

    /** 绘制剪切框以外的阴影 */
    private void drawFrameShadow(Canvas canvas) {
        if (frameRect == null) {
            return;
        }
        Paint shadowPaint = new Paint();
        shadowPaint.setColor(frameShadowColor);

        Rect topRect = new Rect(0, 0, getWidth(), frameRect.top);
        Rect bottomRect = new Rect(0, frameRect.bottom, getWidth(), getHeight());
        Rect leftRect = new Rect(0, frameRect.top, frameRect.left, frameRect.bottom);
        Rect rightRect = new Rect(frameRect.right, frameRect.top, getWidth(), frameRect.bottom);

        canvas.drawRect(topRect, shadowPaint);
        canvas.drawRect(bottomRect, shadowPaint);
        canvas.drawRect(leftRect, shadowPaint);
        canvas.drawRect(rightRect, shadowPaint);
    }

    private boolean isNeedShowFrameLine() {
        if (frameLineShowMode == LINE_SHOW_MODE_SHOW_ALWAYS)
            return true;
        if (frameLineShowMode == LINE_SHOW_MODE_NOT_SHOW)
            return false;
        if (frameLineShowMode == LINE_SHOW_MODE_SHOW_ON_TOUCH
                && isOnTouching) {
            return true;
        }
        return false;
    }

    private boolean isNeedShowGrideLine() {
        if (gridLineShowMode == LINE_SHOW_MODE_SHOW_ALWAYS)
            return true;
        if (gridLineShowMode == LINE_SHOW_MODE_NOT_SHOW)
            return false;
        if (gridLineShowMode == LINE_SHOW_MODE_SHOW_ON_TOUCH
                && isOnTouching) {
            return true;
        }
        return false;
    }

    private boolean isNeedShowHandleLine() {
        if (handleLineShowMode == LINE_SHOW_MODE_SHOW_ALWAYS)
            return true;
        if (handleLineShowMode == LINE_SHOW_MODE_NOT_SHOW)
            return false;
        if (handleLineShowMode == LINE_SHOW_MODE_SHOW_ON_TOUCH
                && isOnTouching) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event.getX(), event.getY());
                //但触摸到四个角和四条边的时候，响应触摸事件
                return (isTouchOnBounds || isTouchOnCorner || isTouchOnFrameInside);
            // return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onActionUp();
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX(), event.getY());
                return false;

            default:
                return false;
        }
    }

    /** 判断是否触摸到剪切框的四个角 */
    private boolean isTouchOnCorner(float x, float y) {
        if (frameRect == null) {
            return false;
        }
        int left = frameRect.left;
        int top = frameRect.top;
        int right = frameRect.right;
        int bottom = frameRect.bottom;
        int lenght = handleLineLength;
        int eX = (int) x;
        int eY = (int) y;

        Rect leftTopRect = new Rect(left, top, left + lenght, top + lenght);
        Rect rightTopRect = new Rect(right - lenght, top, right, top + lenght);
        Rect leftBottomRect = new Rect(left, bottom - lenght, left + lenght, bottom);
        Rect rightBottomRect = new Rect(right - lenght, bottom - lenght, right, bottom);

        if (leftTopRect.contains(eX, eY)) {
            JLog.d(TAG, "isTouchOnCorner, leftTopRect.");
            touchRange = TouchRange.LEFT_TOP_RECT;
            return true;
        }
        if (rightTopRect.contains(eX, eY)) {
            JLog.d(TAG, "isTouchOnCorner, rightTopRect.");
            touchRange = TouchRange.RIGHT_TOP_RECT;
            return true;
        }
        if (leftBottomRect.contains(eX, eY)) {
            JLog.d(TAG, "isTouchOnCorner, leftBottomRect.");
            touchRange = TouchRange.LEFT_BOTTOM_RECT;
            return true;
        }

        if (rightBottomRect.contains(eX, eY)) {
            JLog.d(TAG, "isTouchOnCorner, rightBottomRect.");
            touchRange = TouchRange.RIGHT_BOTTOM_RECT;
            return true;
        }
        return false;
    }

    /** 判断是否触摸到剪切框的四条边界 */
    private boolean isTouchOnBounds(float x, float y) {
        if (frameRect == null) {
            return false;
        }
        int left = frameRect.left;
        int top = frameRect.top;
        int right = frameRect.right;
        int bottom = frameRect.bottom;
        int lenght = handleLineLength;
        int width = handleLineWidth * 15;
        int eX = (int) x;
        int eY = (int) y;

        Rect leftRect = new Rect(left, top + lenght, left + width, bottom - lenght);
        Rect topRect = new Rect(left + lenght, top, right - lenght, top + width);
        Rect rightRect = new Rect(right - width, top + lenght, right, bottom - lenght);
        Rect bottomRect = new Rect(left + lenght, bottom - width, right - lenght, bottom);

        if (leftRect.contains(eX, eY)) {
            JLog.d(TAG, "isTouchOnBounds, left_line.");
            touchRange = TouchRange.LEFT_LINE;
            return true;
        }
        if (topRect.contains(eX, eY)) {
            JLog.d(TAG, "isTouchOnBounds, top_line.");
            touchRange = TouchRange.TOP_LINE;
            return true;
        }
        if (rightRect.contains(eX, eY)) {
            JLog.d(TAG, "isTouchOnBounds, right_line.");
            touchRange = TouchRange.RIGHT_LINE;
            return true;
        }
        if (bottomRect.contains(eX, eY)) {
            JLog.d(TAG, "isTouchOnBounds, bottom_line.");
            touchRange = TouchRange.BOTTOM_LINE;
            return true;
        }
        return false;
    }

    /** 判断是否触摸到剪切框的里面 */
    private boolean isTouchOnFrameInside(float x, float y) {
        if (frameRect == null || !canMoveFrame) {
            return false;
        }
        int offset = handleLineLength;
        int left = frameRect.left + offset;
        int top = frameRect.top + offset;
        int right = frameRect.right - offset;
        int bottom = frameRect.bottom - 5;

        int centerX = ((right - left) / 2) + left;
        int centerY = ((bottom - top) / 2) + top;

        int eX = (int) x;
        int eY = (int) y;

        Rect rect = new Rect(centerX - offset, centerY - offset, centerX + offset, centerY + offset);
        if (rect.contains(eX, eY)) {
            JLog.d(TAG, "isTouchOnBounds, on_frame.");
            touchRange = TouchRange.ON_FRAME;
            return true;
        }

        return false;
    }

    /** 按下屏幕时 */
    private void onActionDown(float x, float y) {
        if (frameRect.contains((int) x, (int) y)) {
            downX = x;
            downY = y;
            isOnTouching = true;
            isTouchOnCorner = isTouchOnCorner(x, y);
            isTouchOnBounds = isTouchOnBounds(x, y);
            isTouchOnFrameInside = isTouchOnFrameInside(x, y);

            if (!isTouchOnBounds && !isTouchOnCorner && !isTouchOnFrameInside) {
                touchRange = TouchRange.OUT_FRAME;
            } else {
                invalidate();
            }
            return;
        }
        touchRange = TouchRange.OUT_FRAME;
    }

    /** 离开或者取消时 */
    private void onActionUp() {
        isOnTouching = false;
        isTouchOnCorner = false;
        isTouchOnBounds = false;
        isTouchOnFrameInside = false;
        touchRange = TouchRange.UNKNOWN;
        cropImageView.setCropRect(frameRect, true);
        invalidate();
    }

    /** 移动 */
    private void onActionMove(float x, float y) {
        if (isOnTouching) {
            if (isTouchOnBounds && canDragFrameConner) {
                reSetBounds(downX, downY, x, y);
            } else if (isTouchOnCorner && canDragFrameConner) {
                if (fixedAspectRatio) {
                    fixedBoundsWithRatio(downX, downY, x, y);
                } else {
                    fixedBoundsWithoutRatio(downX, downY, x, y);
                }
            } else if (isTouchOnFrameInside) {
                moveFrame(downX, downY, x, y);
            }
            invalidate();
        }
    }

    /** 平移剪切框 */
    private void moveFrame(float fromX, float fromY, float toX, float toY) {
        int offsetX = (int) (toX - fromX);
        int offsetY = (int) (toY - fromY);
        if (RectUtils.isOutOfBounds_X(frameRect.left, frameRect.top,
                frameRect.right, frameRect.bottom, offsetX, boundsRect, touchRange)) {
            offsetX = 0;
        }
        if (RectUtils.isOutOfBounds_Y(frameRect.left, frameRect.top,
                frameRect.right, frameRect.bottom, offsetY, boundsRect, touchRange)) {
            offsetY = 0;
        }

        frameRect.offset(offsetX, offsetY);
        downX = toX;
        downY = toY;
        invalidate();
    }


    /** 触摸四条边时，放大或缩小剪切框 */
    private void reSetBounds(float fromX, float fromY, float toX, float toY) {
        if (fixedAspectRatio) {
            return;
        }
        int offsetX = (int) (toX - fromX);
        int offsetY = (int) (toY - fromY);

        int left = frameRect.left;
        int top = frameRect.top;
        int bottom = frameRect.bottom;
        int right = frameRect.right;

        if (touchRange == TouchRange.TOP_LINE) {//触摸到上边
            if (RectUtils.isOutOfBounds_Y(left, top, right, bottom, offsetY, boundsRect, touchRange)) {
                offsetY = 0;
            }
            top = top + offsetY;

            if (RectUtils.isLessThanMinHeight(top, bottom, minFrameWidth)) {
                top = bottom - minFrameWidth;
            }
        }

        if (touchRange == TouchRange.BOTTOM_LINE) {//触摸到下边
            if (RectUtils.isOutOfBounds_Y(left, top, right, bottom, offsetY, boundsRect, touchRange)) {
                offsetY = 0;
            }
            bottom = bottom + offsetY;

            if (RectUtils.isLessThanMinHeight(top, bottom, minFrameWidth)) {
                bottom = top + minFrameWidth;
            }
        }
        if (touchRange == TouchRange.LEFT_LINE) {//触摸到左边
            if (RectUtils.isOutOfBounds_X(left, top, right, bottom, offsetX, boundsRect, touchRange)) {
                offsetX = 0;
            }
            left = left + offsetX;

            if (RectUtils.isLessThanMinWidth(left, right, minFrameWidth)) {
                left = right - minFrameWidth;
            }
        }
        if (touchRange == TouchRange.RIGHT_LINE) {//触摸到右边
            if (RectUtils.isOutOfBounds_X(left, top, right, bottom, offsetX, boundsRect, touchRange)) {
                offsetX = 0;
            }
            right = right + offsetX;

            if (RectUtils.isLessThanMinWidth(left, right, minFrameWidth)) {
                right = left + minFrameWidth;
            }
        }
        frameRect.set(left, top, right, bottom);
        downX = toX;
        downY = toY;
        invalidate();
    }

    /** 触摸四个角时，放大或缩小剪切框，不保持长宽比 */
    private void fixedBoundsWithoutRatio(float fromX, float fromY, float toX, float toY) {
        int offsetX = (int) (toX - fromX);
        int offsetY = (int) (toY - fromY);

        int left = frameRect.left;
        int top = frameRect.top;
        int bottom = frameRect.bottom;
        int right = frameRect.right;

        if (touchRange == TouchRange.LEFT_TOP_RECT) {//触摸左上角
            if (RectUtils.isOutOfBounds_X(left, top, right, bottom, offsetX, boundsRect, touchRange)) {
                offsetX = 0;
            }
            if (RectUtils.isOutOfBounds_Y(left, top, right, bottom, offsetY, boundsRect, touchRange)) {
                offsetY = 0;
            }
            top = top + offsetY;
            left = left + offsetX;

            if (RectUtils.isLessThanMinWidth(left, right, minFrameWidth)) {
                left = right - minFrameWidth;
            }
            if (RectUtils.isLessThanMinHeight(top, bottom, minFrameWidth)) {
                top = bottom - minFrameWidth;
            }
        }
        if (touchRange == TouchRange.LEFT_BOTTOM_RECT) {//触左下角
            if (RectUtils.isOutOfBounds_X(left, top, right, bottom, offsetX, boundsRect, touchRange)) {
                offsetX = 0;
            }
            if (RectUtils.isOutOfBounds_Y(left, top, right, bottom, offsetY, boundsRect, touchRange)) {
                offsetY = 0;
            }
            left = left + offsetX;
            bottom = bottom + offsetY;

            if (RectUtils.isLessThanMinWidth(left, right, minFrameWidth)) {
                left = right - minFrameWidth;
            }
            if (RectUtils.isLessThanMinHeight(top, bottom, minFrameWidth)) {
                bottom = top + minFrameWidth;
            }
        }
        if (touchRange == TouchRange.RIGHT_TOP_RECT) {//触摸右上角
            if (RectUtils.isOutOfBounds_X(left, top, right, bottom, offsetX, boundsRect, touchRange)) {
                offsetX = 0;
            }
            if (RectUtils.isOutOfBounds_Y(left, top, right, bottom, offsetY, boundsRect, touchRange)) {
                offsetY = 0;
            }
            right = right + offsetX;
            top = top + offsetY;

            if (RectUtils.isLessThanMinWidth(left, right, minFrameWidth)) {
                right = left + minFrameWidth;
            }
            if (RectUtils.isLessThanMinHeight(top, bottom, minFrameWidth)) {
                top = bottom - minFrameWidth;
            }
        }
        if (touchRange == TouchRange.RIGHT_BOTTOM_RECT) {//触摸右下角
            if (RectUtils.isOutOfBounds_X(left, top, right, bottom, offsetX, boundsRect, touchRange)) {
                offsetX = 0;
            }
            if (RectUtils.isOutOfBounds_Y(left, top, right, bottom, offsetY, boundsRect, touchRange)) {
                offsetY = 0;
            }
            right = right + offsetX;
            bottom = bottom + offsetY;

            if (RectUtils.isLessThanMinWidth(left, right, minFrameWidth)) {
                right = left + minFrameWidth;
            }
            if (RectUtils.isLessThanMinHeight(top, bottom, minFrameWidth)) {
                bottom = top + minFrameWidth;
            }
        }
        frameRect.set(left, top, right, bottom);
        downX = toX;
        downY = toY;
        invalidate();
    }

    /** 触摸四个角时，放大或缩小剪切框，持长宽比 */
    private void fixedBoundsWithRatio(float fromX, float fromY, float toX, float toY) {
        int dx, dy;
        int offsetX = (int) (toX - fromX);
        int offsetY = (int) (toY - fromY);

        int left = frameRect.left;
        int top = frameRect.top;
        int bottom = frameRect.bottom;
        int right = frameRect.right;

        if (left == right || top == bottom) {
            return;
        }

        if (touchRange == TouchRange.LEFT_TOP_RECT) {//触摸左上角
            dx = (int) toX;
            dy = (((bottom - top) * dx) / (right - left)) + top - (((bottom - top) * left) / (right - left));
            if (!RectUtils.isOutOfBounds(dx, dy, right, bottom, offsetX, offsetY, boundsRect, touchRange)
                    && !RectUtils.isLessThanMinFrame(dx, dy, right, bottom, minFrameWidth)) {
                frameRect.set(dx, dy, right, bottom);
            }
        }
        if (touchRange == TouchRange.RIGHT_BOTTOM_RECT) {//触摸右下角
            dx = (int) toX;
            dy = (((bottom - top) * dx) / (right - left)) + top - (((bottom - top) * left) / (right - left));

            if (!RectUtils.isOutOfBounds(left, top, dx, dy, offsetX, offsetY, boundsRect, touchRange)
                    && !RectUtils.isLessThanMinFrame(left, top, dx, dy, minFrameWidth)) {
                frameRect.set(left, top, dx, dy);
            }
        }
        if (touchRange == TouchRange.LEFT_BOTTOM_RECT) {//触左下角
            dx = (int) toX;
            dy = (((bottom - top) * dx) / (left - right)) + top - (((bottom - top) * right) / (left - right));

            if (!RectUtils.isOutOfBounds(dx, top, right, dy, offsetX, offsetY, boundsRect, touchRange)
                    && !RectUtils.isLessThanMinFrame(dx, top, right, dy, minFrameWidth)) {
                frameRect.set(dx, top, right, dy);
            }
        }
        if (touchRange == TouchRange.RIGHT_TOP_RECT) {//触摸右上角
            dx = (int) toX;
            dy = (((bottom - top) * dx) / (left - right)) + top - (((bottom - top) * right) / (left - right));

            if (!RectUtils.isOutOfBounds(left, dy, dx, bottom, offsetX, offsetY, boundsRect, touchRange)
                    && !RectUtils.isLessThanMinFrame(left, dy, dx, bottom, minFrameWidth)) {
                frameRect.set(left, dy, dx, bottom);
            }
        }
        downX = toX;
        downY = toY;
        invalidate();
    }
}
