package com.ttsea.jlibrary.component.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.support.annotation.BoolRes;
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

    private final int DEFAULT_BORDER_WIDTH = 5;
    private final ColorStateList DEFAULT_BORDER_UNCHECKED_COLOR = ColorStateList.valueOf(0xFFC0C0C0);
    private final ColorStateList DEFAULT_SOLID_UNCHECKED_COLOR = ColorStateList.valueOf(0);
    private final ColorStateList DEFAULT_BORDER_CHECKED_COLOR = ColorStateList.valueOf(0);
    private final ColorStateList DEFAULT_SOLID_CHECKED_COLOR = ColorStateList.valueOf(0xFFDFDFDF);
    private final int DEFAULT_TICK_WIDTH = 4;
    private final ColorStateList DEFAULT_TICK_COLOR = ColorStateList.valueOf(0xFFFFFFFF);
    private final int DEFAULT_ANIM_DURATION = 300;
    private final boolean DEFAULT_SHOULD_ANIMATE = true;

    /** 边框宽度 */
    private int borderWidth = DEFAULT_BORDER_WIDTH;
    /** 未checked边框颜色 */
    private ColorStateList borderUncheckedColor = DEFAULT_BORDER_UNCHECKED_COLOR;
    /** 未checked填充颜色 */
    private ColorStateList solidUncheckedColor = DEFAULT_SOLID_UNCHECKED_COLOR;
    /** checked边框颜色 */
    private ColorStateList borderCheckedColor = DEFAULT_BORDER_CHECKED_COLOR;
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
    private ColorStateList borderColor;
    private ColorStateList solidColor;
    private Point mCenterPoint;

    private OnCheckedChangeListener onCheckedChangeListener;

    public SmoothCheckBox(Context context) {
        super(context);
    }

    public SmoothCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmoothCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SmoothCheckBox);
        initStyleable(a);
        init();
    }

    private void initStyleable(TypedArray a) {
        borderWidth = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_strokeWidth, DEFAULT_BORDER_WIDTH);
        borderUncheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_strokeUncheckedColor);
        if (borderUncheckedColor == null) {
            borderUncheckedColor = DEFAULT_BORDER_UNCHECKED_COLOR;
        }
        solidUncheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_solidUncheckedColor);
        if (solidUncheckedColor == null) {
            solidUncheckedColor = DEFAULT_SOLID_UNCHECKED_COLOR;
        }
        borderCheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_strokeCheckedColor);
        if (borderCheckedColor == null) {
            borderCheckedColor = DEFAULT_BORDER_CHECKED_COLOR;
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

        JLog.d(TAG, "borderWidth:" + borderWidth
                + ",\n borderUncheckedColor:" + borderUncheckedColor
                + ",\n solidUncheckedColor:" + solidUncheckedColor
                + ",\n borderCheckedColor:" + borderCheckedColor
                + ",\n solidCheckedColor:" + solidCheckedColor
                + ",\n tickWidth:" + tickWidth
                + ",\n tickColor:" + tickColor
                + ",\n type:" + type
                + ",\n animDuration:" + animDuration
                + ",\n radius:" + radius
                + ",\n shouldAnimate:" + shouldAnimate
        );
    }

    private void init() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    /**
     * checked with animation
     *
     * @param checked checked
     * @param animate change with animation
     */
    public void setChecked(boolean checked, boolean animate) {
        mChecked = checked;
        if (mChecked) {

        } else {

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

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.onCheckedChangeListener = l;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setBorderUncheckedColor(ColorStateList color) {
        this.borderUncheckedColor = (color != null) ? color : DEFAULT_BORDER_UNCHECKED_COLOR;
    }

    public void setBorderUncheckedColor(@ColorInt int color) {
        setBorderUncheckedColor(ColorStateList.valueOf(color));
    }

    public void setSolidUncheckedColor(ColorStateList color) {
        this.solidUncheckedColor = (color != null) ? color : DEFAULT_SOLID_UNCHECKED_COLOR;
    }

    public void setSolidUncheckedColor(@ColorInt int color) {
        setSolidUncheckedColor(ColorStateList.valueOf(color));
    }

    public void setBorderCheckedColor(ColorStateList color) {
        this.borderCheckedColor = (color != null) ? color : DEFAULT_BORDER_CHECKED_COLOR;
    }

    public void setBorderCheckedColor(@ColorInt int color) {
        setBorderCheckedColor(ColorStateList.valueOf(color));
    }

    public void setSolidCheckedColor(ColorStateList color) {
        this.solidCheckedColor = (color != null) ? color : DEFAULT_BORDER_CHECKED_COLOR;
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
