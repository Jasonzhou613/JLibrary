package com.ttsea.jlibrary.component.widget.checkBox;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.ttsea.jlibrary.common.JLog;

/**
 * //To do <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/10/17 10:42 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/10/17 10:42
 */
class CheckBoxDrawable extends Drawable {
    private final String TAG = "CheckBoxDrawable";

    // 边框的宽度和颜色
    private int strokeWidth;
    private ColorStateList strokeColor;

    // 勾的宽度、颜色和长度
    private int tickWidth;
    private ColorStateList tickColor;
    private float mLeftTickDistance, mRightTickDistance;

    /** CheckBox形状,0为矩形，1为椭圆，默认为1 */
    private int type = SmoothCheckBox.TYPE_OVAL;
    /** 动画时长 */
    private int animDuration;
    /** 这里存储四个角的幅度，0=TopLeft,1=TopRight,2=BottomRight,3=BottomLeft，当type为TYPE_RECTANGLE生效 */
    private float[] radius = new float[4];
    /** 切换的时候是否显示动画,默认为true */
    private boolean shouldAnimate = true;
    private boolean mChecked;

    private ColorStateList solidColor;

    private Paint mStrokePaint;
    private Paint mSolidPaint;
    private Paint mTickPaint;

    private Point mCenterPoint;
    private Point[] mTickPoints;

    private RectF mBounds = new RectF();
    private RectF mBorderRect = new RectF();

    public CheckBoxDrawable() {
        super();
    }

    public void init() {
        mCenterPoint = new Point();
        mTickPoints = new Point[3];
        mTickPoints[0] = new Point();
        mTickPoints[1] = new Point();
        mTickPoints[2] = new Point();

        if (mStrokePaint == null) {
            mStrokePaint = new Paint();
        }
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(getColorForState(strokeColor));
        mStrokePaint.setStrokeWidth(strokeWidth);

        if (mSolidPaint == null) {
            mSolidPaint = new Paint();
        }
        mSolidPaint.setStyle(Paint.Style.FILL);
        mSolidPaint.setStrokeCap(Paint.Cap.ROUND);
        mSolidPaint.setAntiAlias(true);
        mSolidPaint.setColor(getColorForState(solidColor));

        if (mTickPaint == null) {
            mTickPaint = new Paint();
        }
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setColor(getColorForState(tickColor));
        mTickPaint.setStrokeWidth(tickWidth);
    }

    @Override
    public void draw(Canvas canvas) {
        drawSolid(canvas);
        drawBorder(canvas);
        if (isChecked()) {
            drawTick(canvas);
        }
    }

    private void drawSolid(Canvas canvas) {
        canvas.drawOval(mBounds, mSolidPaint);
    }

    private void drawBorder(Canvas canvas) {
        mStrokePaint.setColor(getColorForState(strokeColor));
        if (type == SmoothCheckBox.TYPE_OVAL) {
            canvas.drawOval(mBorderRect, mStrokePaint);
        }
    }

    private void drawTick(Canvas canvas) {

    }

    @Override
    public void setAlpha(int alpha) {
        if (mStrokePaint == null) {
            mStrokePaint = new Paint();
        }
        if (mSolidPaint == null) {
            mSolidPaint = new Paint();
        }
        if (mTickPaint == null) {
            mTickPaint = new Paint();
        }
        mStrokePaint.setAlpha(alpha);
        mSolidPaint.setAlpha(alpha);
        mTickPaint.setAlpha(alpha);
    }

    @Override
    public int getAlpha() {
        return mSolidPaint.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (mStrokePaint == null) {
            mStrokePaint = new Paint();
        }
        if (mSolidPaint == null) {
            mSolidPaint = new Paint();
        }
        if (mTickPaint == null) {
            mTickPaint = new Paint();
        }
        mStrokePaint.setColorFilter(colorFilter);
        mSolidPaint.setColorFilter(colorFilter);
        mTickPaint.setColorFilter(colorFilter);
    }

    @Override
    public ColorFilter getColorFilter() {
        return mSolidPaint.getColorFilter();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public boolean isStateful() {
        return strokeColor.isStateful() || solidColor.isStateful() || tickColor.isStateful();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        int newStrokeColor = getColorForState(state, strokeColor);
        int newSolidColor = getColorForState(state, solidColor);
        int newTickColor = getColorForState(state, tickColor);

        if (newStrokeColor != mStrokePaint.getColor()) {
            mStrokePaint.setColor(newStrokeColor);
        }
        if (newSolidColor != mSolidPaint.getColor()) {
            mSolidPaint.setColor(newSolidColor);
        }
        if (newTickColor != mTickPaint.getColor()) {
            mTickPaint.setColor(newTickColor);
        }
        invalidateSelf();
        return super.onStateChange(state);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        mBounds.set(bounds);
        int offset = strokeWidth / 2;
        mBorderRect.set(mBounds.left + offset, mBounds.top + offset, mBounds.right - offset, mBounds.bottom - offset);
        mCenterPoint.x = (int) mBounds.width() / 2;
        mCenterPoint.y = (int) mBounds.height() / 2;

        JLog.d(TAG, "mBounds:" + mBounds + ", mBorderRect:" + mBorderRect);
    }

    private int getColorForState(int[] state, ColorStateList colors) {
        return colors.getColorForState(state, colors.getDefaultColor());
    }

    private int getColorForState(ColorStateList colors) {
        return getColorForState(getState(), colors);
    }

    @Override
    public int getIntrinsicWidth() {
        return super.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return super.getIntrinsicHeight();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidateSelf();
    }

    public ColorStateList getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(ColorStateList color) {
        this.strokeColor = color;
        invalidateSelf();
    }

    public int getTickWidth() {
        return tickWidth;
    }

    public void setTickWidth(int tickWidth) {
        this.tickWidth = tickWidth;
        invalidateSelf();
    }

    public ColorStateList getTickColor() {
        return tickColor;
    }

    public void setTickColor(ColorStateList color) {
        this.tickColor = color;
        invalidateSelf();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        invalidateSelf();
    }

    public int getAnimDuration() {
        return animDuration;
    }

    public void setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
    }

    public float[] getRadius() {
        return radius;
    }

    public void setRadius(float[] radius) {
        this.radius = radius;
        invalidateSelf();
    }

    public boolean isShouldAnimate() {
        return shouldAnimate;
    }

    public void setShouldAnimate(boolean shouldAnimate) {
        this.shouldAnimate = shouldAnimate;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    public ColorStateList getSolidColor() {
        return solidColor;
    }

    public void setSolidColor(ColorStateList solidColor) {
        this.solidColor = solidColor;
    }
}
