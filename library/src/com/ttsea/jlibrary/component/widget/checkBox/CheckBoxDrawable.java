package com.ttsea.jlibrary.component.widget.checkBox;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
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
        init();
    }

    private void init() {
        mCenterPoint = new Point();
        mTickPoints = new Point[3];
        mTickPoints[0] = new Point();
        mTickPoints[1] = new Point();
        mTickPoints[2] = new Point();

        mStrokePaint = new Paint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(getColorForState(strokeColor));
        mStrokePaint.setStrokeWidth(strokeWidth);

        mSolidPaint = new Paint();
        mSolidPaint.setStyle(Paint.Style.FILL);
        mSolidPaint.setStrokeCap(Paint.Cap.ROUND);
        mSolidPaint.setAntiAlias(true);
        mSolidPaint.setColor(getColorForState(solidColor));

        mTickPaint = new Paint();
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setColor(getColorForState(tickColor));
        mTickPaint.setStrokeWidth(tickWidth);
    }

    @Override
    public void draw(Canvas canvas) {
        JLog.d(TAG, "draw...");
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public int getAlpha() {
        return super.getAlpha();
    }


    @Override
    public boolean isStateful() {
        return super.isStateful();
    }


    @Override
    public Drawable getCurrent() {
        return super.getCurrent();
    }


    @Override
    protected boolean onStateChange(int[] state) {
        return super.onStateChange(state);
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }

    @Override
    public int getIntrinsicWidth() {
        return super.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return super.getIntrinsicHeight();
    }

    private int getColorForState(ColorStateList colors) {
        return colors.getColorForState(getState(), colors.getDefaultColor());
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

    public void setBounds(RectF bounds) {
        this.mBounds = bounds;
    }
}
