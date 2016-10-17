package com.ttsea.jlibrary.component.widget.checkBox;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
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

    public static final int TYPE_RECTANGLE = 0;//矩形
    public static final int TYPE_OVAL = 1;//椭圆

    private final int DEFAULT_STROKE_WIDTH = 5;
    private final ColorStateList DEFAULT_STROKE_UNCHECKED_COLOR = ColorStateList.valueOf(0xFFC0C0C0);
    private final ColorStateList DEFAULT_SOLID_UNCHECKED_COLOR = ColorStateList.valueOf(0);
    private final ColorStateList DEFAULT_STROKE_CHECKED_COLOR = ColorStateList.valueOf(0);
    private final ColorStateList DEFAULT_SOLID_CHECKED_COLOR = ColorStateList.valueOf(0xFFDFDFDF);
    private final int DEFAULT_TICK_WIDTH = 4;
    private final ColorStateList DEFAULT_TICK_COLOR = ColorStateList.valueOf(0xFFFFFFFF);
    private final int DEFAULT_ANIM_DURATION = 300;
    private final boolean DEFAULT_SHOULD_ANIMATE = true;
    private final boolean DEFAULT_CHECKED = false;

    /** 未checked边框颜色 */
    private ColorStateList strokeUncheckedColor = DEFAULT_STROKE_UNCHECKED_COLOR;
    /** 未checked填充颜色 */
    private ColorStateList solidUncheckedColor = DEFAULT_SOLID_UNCHECKED_COLOR;
    /** checked边框颜色 */
    private ColorStateList strokeCheckedColor = DEFAULT_STROKE_CHECKED_COLOR;
    /** checked填充颜色 */
    private ColorStateList solidCheckedColor = DEFAULT_SOLID_CHECKED_COLOR;
    /** 切换的时候是否显示动画,默认为true */
    private boolean shouldAnimate = DEFAULT_SHOULD_ANIMATE;

    private CheckBoxDrawable mDrawable;
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
        int strokeWidth = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_strokeWidth, DEFAULT_STROKE_WIDTH);
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
        int tickWidth = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_tickWidth, DEFAULT_TICK_WIDTH);
        ColorStateList tickColor = a.getColorStateList(R.styleable.SmoothCheckBox_tickColor);
        if (tickColor == null) {
            tickColor = DEFAULT_TICK_COLOR;
        }
        int animDuration = a.getInt(R.styleable.SmoothCheckBox_duration, DEFAULT_ANIM_DURATION);
        int type = a.getInt(R.styleable.SmoothCheckBox_type, TYPE_OVAL);
        shouldAnimate = a.getBoolean(R.styleable.SmoothCheckBox_shouldAnimate, DEFAULT_SHOULD_ANIMATE);
        boolean checked = a.getBoolean(R.styleable.SmoothCheckBox_checked, DEFAULT_CHECKED);

        boolean hasSetRadius = false;
        float[] radius = new float[4];
        radius[Corner.TOP_LEFT] = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_radiusTopLeft, -1);
        radius[Corner.TOP_RIGHT] = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_radiusTopRight, -1);
        radius[Corner.BOTTOM_RIGHT] = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_radiusBottomRight, -1);
        radius[Corner.BOTTOM_LEFT] = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_radiusBottomLeft, -1);

        for (int i = 0; i < radius.length; i++) {
            if (radius[i] < 0) {
                radius[i] = 0f;
            } else {
                hasSetRadius = true;
            }
        }

        if (!hasSetRadius) {
            float radiusOverride = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_radius, -1);
            if (radiusOverride < 0) {
                radiusOverride = 0f;
            }
            for (int i = 0; i < radius.length; i++) {
                radius[i] = radiusOverride;
            }
        }

        if (mDrawable == null) {
            mDrawable = new CheckBoxDrawable();
        }

        mDrawable.setStrokeWidth(strokeWidth);
        mDrawable.setTickWidth(tickWidth);
        mDrawable.setTickColor(tickColor);
        mDrawable.setType(type);
        mDrawable.setShouldAnimate(shouldAnimate);
        mDrawable.setAnimDuration(animDuration);
        mDrawable.setRadius(radius);
        mDrawable.setChecked(checked);

        if (isChecked()) {
            mDrawable.setStrokeColor(strokeCheckedColor);
            mDrawable.setSolidColor(solidCheckedColor);
        } else {
            mDrawable.setStrokeColor(strokeUncheckedColor);
            mDrawable.setSolidColor(solidUncheckedColor);
        }

        JLog.d(TAG, "strokeWidth:" + strokeWidth
                + ",\n strokeUncheckedColor:" + strokeUncheckedColor
                + ",\n strokeCheckedColor:" + strokeCheckedColor
                + ",\n solidUncheckedColor:" + solidUncheckedColor
                + ",\n solidCheckedColor:" + solidCheckedColor
                + ",\n tickWidth:" + tickWidth
                + ",\n tickColor:" + tickColor
                + ",\n type:" + type
                + ",\n shouldAnimate:" + shouldAnimate
                + ",\n animDuration:" + animDuration
                + ",\n radius[TOP_LEFT]:" + radius[Corner.TOP_LEFT]
                + ", radius[TOP_RIGHT]:" + radius[Corner.TOP_RIGHT]
                + ", radius[BOTTOM_RIGHT]:" + radius[Corner.BOTTOM_RIGHT]
                + ", radius[BOTTOM_LEFT]:" + radius[Corner.BOTTOM_LEFT]
        );
    }

    private void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        setBackgroundDrawable(mDrawable);
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
        mDrawable.setShouldAnimate(animate);
        mDrawable.setChecked(checked);

        if (isChecked()) {
            mDrawable.setStrokeColor(strokeCheckedColor);
            mDrawable.setSolidColor(solidCheckedColor);
        } else {
            mDrawable.setStrokeColor(strokeUncheckedColor);
            mDrawable.setSolidColor(solidUncheckedColor);
        }

        invalidateDrawable(mDrawable);

        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, isChecked());
        }
    }

    @Override
    public boolean isChecked() {
        return mDrawable.isChecked();
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

    public int getStrokeWidth() {
        return mDrawable.getStrokeWidth();
    }

    public void setStrokeWidth(int strokeWidth) {
        mDrawable.setStrokeWidth(strokeWidth);
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
        return mDrawable.getTickWidth();
    }

    public void setTickWidth(int tickWidth) {
        mDrawable.setTickWidth(tickWidth);
    }

    public ColorStateList getTickColor() {
        return mDrawable.getTickColor();
    }

    public void setTickColor(ColorStateList color) {
        mDrawable.setTickColor(((color != null) ? color : ColorStateList.valueOf(0)));
    }

    public void setTickColor(@ColorInt int color) {
        setTickColor(ColorStateList.valueOf(color));
    }

    public int getType() {
        return mDrawable.getType();
    }

    public void setType(int type) {
        mDrawable.setType(type);
    }

    public int getAnimDuration() {
        return mDrawable.getAnimDuration();
    }

    public void setAnimDuration(int animDuration) {
        mDrawable.setAnimDuration(animDuration);
    }

    /** 默认获取四个角的最大的弧度 */
    public float getRadius() {
        float radiu = 0;
        for (int i = 0; i < mDrawable.getRadius().length; i++) {
            radiu = Math.max(radiu, mDrawable.getRadius()[i]);
        }
        return radiu;
    }

    /** 0=TopLeft,1=TopRight,2=BottomRight,3=BottomLeft */
    public float getRadius(int index) {
        return (mDrawable.getRadius())[index];
    }

    public void setRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mDrawable.getRadius()[Corner.TOP_LEFT] = topLeft;
        mDrawable.getRadius()[Corner.TOP_RIGHT] = topRight;
        mDrawable.getRadius()[Corner.BOTTOM_RIGHT] = bottomRight;
        mDrawable.getRadius()[Corner.BOTTOM_LEFT] = bottomLeft;
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
