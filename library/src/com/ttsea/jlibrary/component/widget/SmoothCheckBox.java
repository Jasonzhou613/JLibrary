package com.ttsea.jlibrary.component.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JLog;

/**
 * CheckBox <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/10/12 16:37 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/10/12 16:37
 */
public class SmoothCheckBox extends View implements Checkable {
    private final String TAG = "SmoothCheckBox";

    private final int TYPE_RECTANGLE = 0;//矩形
    private final int TYPE_OVAL = 1;//椭圆

    private final int DEFAULT_STROKE_WIDTH = 5;
    private final ColorStateList DEFAULT_STROKE_UNCHECKED_COLOR = ColorStateList.valueOf(0xFFC0C0C0);
    private final ColorStateList DEFAULT_SOLID_UNCHECKED_COLOR = ColorStateList.valueOf(0);
    private final ColorStateList DEFAULT_STROKE_CHECKED_COLOR = ColorStateList.valueOf(0);
    private final ColorStateList DEFAULT_SOLID_CHECKED_COLOR = ColorStateList.valueOf(0xFFDFDFDF);
    private final int DEFAULT_TICK_WIDTH = 4;
    private final ColorStateList DEFAULT_TICK_COLOR = ColorStateList.valueOf(0xFFFFFFFF);
    private final int DEFAULT_ANIM_DURATION = 300;
    private final boolean DEFAULT_SHOULD_ANIMATE = true;

    /** 边框宽度 */
    private int strokeWidth = DEFAULT_STROKE_WIDTH;
    /** 未checked边框颜色 */
    private ColorStateList strokeUncheckedColor = DEFAULT_STROKE_UNCHECKED_COLOR;
    /** 未checked填充颜色 */
    private ColorStateList solidUncheckedColor = DEFAULT_SOLID_UNCHECKED_COLOR;
    /** checked边框颜色 */
    private ColorStateList strokeCheckedColor = DEFAULT_STROKE_CHECKED_COLOR;
    /** checked填充颜色 */
    private ColorStateList solidCheckedColor = DEFAULT_SOLID_CHECKED_COLOR;
    /** 勾的宽度 */
    private int tickWidth = DEFAULT_TICK_WIDTH;
    /** 勾的颜色 */
    private ColorStateList tickColor = DEFAULT_TICK_COLOR;
    /** CheckBox形状,0为矩形，1为椭圆，默认为1 */
    private int type = TYPE_OVAL;
    /** 动画时长 */
    private int animDuration = DEFAULT_ANIM_DURATION;
    /** 这里存储四个角的幅度，0=TopLeft,1=TopRight,2=BottomRight,3=BottomLeft，当type为TYPE_RECTANGLE生效 */
    private float[] radius = new float[4];
    /** 切换的时候是否显示动画,默认为true */
    private boolean shouldAnimate = DEFAULT_SHOULD_ANIMATE;

    private boolean mChecked;
    private int width, height;
    private float mLeftTickDistance, mRightTickDistance;
    private Paint mTickPaint;
    private ColorStateList strokeColor;
    private ColorStateList solidColor;
    private Point mCenterPoint;
    private Point[] mTickPoints;

    private OnCheckedChangeListener onCheckedChangeListener;

    public SmoothCheckBox(Context context) {
        this(context, null);
    }

    public SmoothCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SmoothCheckBox);
        initStyleable(a);
        init();
    }

    private void initStyleable(TypedArray a) {
        strokeWidth = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_strokeWidth, DEFAULT_STROKE_WIDTH);
        strokeUncheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_strokeUncheckedColor);
        if (strokeUncheckedColor == null) {
            strokeUncheckedColor = DEFAULT_STROKE_UNCHECKED_COLOR;
        }
        solidUncheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_solidUncheckedColor);
        if (solidUncheckedColor == null) {
            solidUncheckedColor = DEFAULT_SOLID_UNCHECKED_COLOR;
        }
        strokeCheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_strokeCheckedColor);
        if (strokeCheckedColor == null) {
            strokeCheckedColor = DEFAULT_STROKE_CHECKED_COLOR;
        }
        solidCheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_solidCheckedColor);
        if (solidCheckedColor == null) {
            solidCheckedColor = DEFAULT_SOLID_CHECKED_COLOR;
        }
        tickWidth = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_tickWidth, DEFAULT_TICK_WIDTH);
        tickColor = a.getColorStateList(R.styleable.SmoothCheckBox_tickColor);
        if (tickColor == null) {
            tickColor = DEFAULT_TICK_COLOR;
        }
        animDuration = a.getInt(R.styleable.SmoothCheckBox_duration, DEFAULT_ANIM_DURATION);
        type = a.getInt(R.styleable.SmoothCheckBox_type, TYPE_OVAL);
        shouldAnimate = a.getBoolean(R.styleable.SmoothCheckBox_shouldAnimate, DEFAULT_SHOULD_ANIMATE);

        boolean hasSetRadius = false;
        radius[0] = a.getFloat(R.styleable.SmoothCheckBox_radiusTopLeft, -1);
        radius[1] = a.getFloat(R.styleable.SmoothCheckBox_radiusTopRight, -1);
        radius[2] = a.getFloat(R.styleable.SmoothCheckBox_radiusBottomRight, -1);
        radius[3] = a.getFloat(R.styleable.SmoothCheckBox_radiusBottomLeft, -1);

        for (int i = 0; i < radius.length; i++) {
            if (radius[i] < 0) {
                radius[i] = 0f;
            } else {
                hasSetRadius = true;
            }
        }

        if (!hasSetRadius) {
            float radiusOverride = a.getFloat(R.styleable.SmoothCheckBox_radius, 0);
            if (radiusOverride < 0) {
                radiusOverride = 0f;
            }
            for (int i = 0; i < radius.length; i++) {
                radius[i] = radiusOverride;
            }
        }

        JLog.d(TAG, "strokeWidth:" + strokeWidth
                + ",\n strokeUncheckedColor:" + strokeUncheckedColor
                + ",\n solidUncheckedColor:" + solidUncheckedColor
                + ",\n strokeCheckedColor:" + strokeCheckedColor
                + ",\n solidCheckedColor:" + solidCheckedColor
                + ",\n tickWidth:" + tickWidth
                + ",\n tickColor:" + tickColor
                + ",\n type:" + type
                + ",\n animDuration:" + animDuration
                + ",\n radius[0]:" + radius[0] + ", radius[1]:" + radius[1]
                + ", radius[2]:" + radius[2] + ", radius[3]:" + radius[3]
                + ",\n shouldAnimate:" + shouldAnimate
        );
    }

    private void init() {
        if (isChecked()) {
            strokeColor = strokeCheckedColor;
            solidColor = solidCheckedColor;
        } else {
            strokeColor = strokeUncheckedColor;
            solidColor = solidUncheckedColor;
        }

        mCenterPoint = new Point();
        mTickPoints = new Point[3];
        mTickPoints[0] = new Point();
        mTickPoints[1] = new Point();
        mTickPoints[2] = new Point();

        mTickPaint = new Paint();
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mTickPaint.setColor(tickColor.getDefaultColor());
        mTickPaint.setStrokeWidth(strokeWidth);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        mCenterPoint.x = width / 2;
        mCenterPoint.y = height / 2;

        mTickPoints[0].x = Math.round((float) getMeasuredWidth() / 30 * 7);
        mTickPoints[0].y = Math.round((float) getMeasuredHeight() / 30 * 14);
        mTickPoints[1].x = Math.round((float) getMeasuredWidth() / 30 * 13);
        mTickPoints[1].y = Math.round((float) getMeasuredHeight() / 30 * 20);
        mTickPoints[2].x = Math.round((float) getMeasuredWidth() / 30 * 22);
        mTickPoints[2].y = Math.round((float) getMeasuredHeight() / 30 * 10);

        mLeftTickDistance = (float) Math.sqrt(Math.pow(mTickPoints[1].x - mTickPoints[0].x, 2) +
                Math.pow(mTickPoints[1].y - mTickPoints[0].y, 2));
        mRightTickDistance = (float) Math.sqrt(Math.pow(mTickPoints[2].x - mTickPoints[1].x, 2) +
                Math.pow(mTickPoints[2].y - mTickPoints[1].y, 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawSolid(canvas);
        drawBorder(canvas);
        drawTick(canvas);
    }

    private void drawSolid(Canvas canvas) {
        JLog.d(TAG, "drawSolid...");
    }

    private void drawBorder(Canvas canvas) {
        JLog.d(TAG, "drawBorder...");
    }

    private void drawTick(Canvas canvas) {
        JLog.d(TAG, "drawTick...");
    }

    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, shouldAnimate);
    }

    /**
     * checked with animation
     *
     * @param checked checked
     * @param animate change with animation
     */
    public void setChecked(boolean checked, boolean animate) {
        JLog.d(TAG, "checked:" + checked + ", animate:" + animate);
        mChecked = checked;
        if (isChecked()) {
            strokeColor = strokeCheckedColor;
            solidColor = solidCheckedColor;
        } else {
            strokeColor = strokeUncheckedColor;
            solidColor = solidUncheckedColor;
        }

        invalidate();
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, isChecked());
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        this.setChecked(!isChecked());
    }

    private void reset() {

    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.onCheckedChangeListener = l;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setStrokeUncheckedColor(ColorStateList color) {
        this.strokeUncheckedColor = (color != null) ? color : DEFAULT_STROKE_UNCHECKED_COLOR;
    }

    public void setBorderUncheckedColor(@ColorInt int color) {
        setStrokeUncheckedColor(ColorStateList.valueOf(color));
    }

    public void setSolidUncheckedColor(ColorStateList color) {
        this.solidUncheckedColor = (color != null) ? color : DEFAULT_SOLID_UNCHECKED_COLOR;
    }

    public void setSolidUncheckedColor(@ColorInt int color) {
        setSolidUncheckedColor(ColorStateList.valueOf(color));
    }

    public void setStrokeCheckedColor(ColorStateList color) {
        this.strokeCheckedColor = (color != null) ? color : DEFAULT_STROKE_CHECKED_COLOR;
    }

    public void setBorderCheckedColor(@ColorInt int color) {
        setStrokeCheckedColor(ColorStateList.valueOf(color));
    }

    public void setSolidCheckedColor(ColorStateList color) {
        this.solidCheckedColor = (color != null) ? color : DEFAULT_STROKE_CHECKED_COLOR;
    }

    public void setSolidCheckedColor(@ColorInt int color) {
        setSolidCheckedColor(ColorStateList.valueOf(color));
    }

    public int getTickWidth() {
        return tickWidth;
    }

    public void setTickWidth(int tickWidth) {
        this.tickWidth = tickWidth;
    }

    public ColorStateList getTickColor() {
        return tickColor;
    }

    public void setTickColor(ColorStateList tickColor) {
        this.tickColor = (tickColor != null) ? tickColor : ColorStateList.valueOf(0);
    }

    public void setTickColor(@ColorInt int color) {
        setTickColor(ColorStateList.valueOf(color));
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAnimDuration() {
        return animDuration;
    }

    public void setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
    }

    /** 默认获取四个角的最大的弧度 */
    public float getRadius() {
        float radiu = 0;
        for (int i = 0; i < radius.length; i++) {
            radiu = Math.max(radiu, radius[i]);
        }
        return radiu;
    }

    /** 0=TopLeft,1=TopRight,2=BottomRight,3=BottomLeft */
    public float getRadius(int index) {
        return radius[index];
    }

    public void setRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        radius[0] = topLeft;
        radius[1] = topRight;
        radius[2] = bottomRight;
        radius[3] = bottomLeft;
    }

    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of smoothCheckBox has changed.
         *
         * @param smoothCheckBox The smoothCheckBox button view whose state has changed.
         * @param isChecked      The new checked state of buttonView.
         */
        void onCheckedChanged(SmoothCheckBox smoothCheckBox, boolean isChecked);
    }
}
