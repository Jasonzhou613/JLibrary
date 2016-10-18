package com.ttsea.jlibrary.component.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CheckBox <br/>
 * <p>
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
    private final int DEFAULT_ALPHA = 255;
    private final boolean DEFAULT_SHOULD_ANIMATE = true;
    private final boolean DEFAULT_CHECKED = false;

    /** 未checked边框颜色 */
    private ColorStateList strokeUncheckedColor = DEFAULT_STROKE_UNCHECKED_COLOR;
    /** checked边框颜色 */
    private ColorStateList strokeCheckedColor = DEFAULT_STROKE_CHECKED_COLOR;
    /** 未checked填充颜色 */
    private ColorStateList solidUncheckedColor = DEFAULT_SOLID_UNCHECKED_COLOR;
    /** checked填充颜色 */
    private ColorStateList solidCheckedColor = DEFAULT_SOLID_CHECKED_COLOR;

    private int strokeWidth;
    private int tickWidth;
    private ColorStateList tickColor;
    private int animDuration;
    private int type;
    private int alpha;
    private boolean shouldAnimate;
    private boolean mChecked;
    private float[] radius;

    private Paint mStrokePaint;
    private Paint mSolidPaint;
    private Paint mTickPaint;

    private Point mCenterPoint;
    private Point[] mTickPoints;
    private Path mTickPath;
    private RectF mSolidRect = new RectF();
    private RectF mBorderRect = new RectF();
    private CheckBoxDrawable mDrawable;
    private float mLeftTickDistance, mRightTickDistance;
    private float mLeftLineDistance, mRightLineDistance, mDrewDistance;

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
        strokeCheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_strokeCheckedColor);
        if (strokeCheckedColor == null) {
            strokeCheckedColor = DEFAULT_STROKE_CHECKED_COLOR;
        }
        solidUncheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_solidUncheckedColor);
        if (solidUncheckedColor == null) {
            solidUncheckedColor = DEFAULT_SOLID_UNCHECKED_COLOR;
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
        shouldAnimate = a.getBoolean(R.styleable.SmoothCheckBox_shouldAnimate, DEFAULT_SHOULD_ANIMATE);
        animDuration = a.getInt(R.styleable.SmoothCheckBox_duration, DEFAULT_ANIM_DURATION);
        type = a.getInt(R.styleable.SmoothCheckBox_type, TYPE_OVAL);
        mChecked = a.getBoolean(R.styleable.SmoothCheckBox_checked, DEFAULT_CHECKED);
        alpha = a.getInt(R.styleable.SmoothCheckBox_alpha, DEFAULT_ALPHA);
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 255) {
            alpha = 255;
        }

        boolean hasSetRadius = false;
        radius = new float[4];
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
                + ",\n mChecked:" + mChecked
                + ",\n alpha:" + alpha
                + ",\n radius[TOP_LEFT]:" + radius[Corner.TOP_LEFT]
                + ", radius[TOP_RIGHT]:" + radius[Corner.TOP_RIGHT]
                + ", radius[BOTTOM_RIGHT]:" + radius[Corner.BOTTOM_RIGHT]
                + ", radius[BOTTOM_LEFT]:" + radius[Corner.BOTTOM_LEFT]
        );
    }

    private void init() {
        if (mDrawable == null) {
            mDrawable = new CheckBoxDrawable();
        }

        mCenterPoint = new Point();
        mTickPoints = new Point[3];
        mTickPoints[0] = new Point();
        mTickPoints[1] = new Point();
        mTickPoints[2] = new Point();
        mTickPath = new Path();

        mStrokePaint = new Paint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(getStrokeColor().getDefaultColor());
        mStrokePaint.setStrokeWidth(strokeWidth);

        mSolidPaint = new Paint();
        mSolidPaint.setStyle(Paint.Style.FILL);
        mSolidPaint.setStrokeCap(Paint.Cap.ROUND);
        mSolidPaint.setAntiAlias(true);
        mSolidPaint.setColor(getSolidColor2().getDefaultColor());

        mTickPaint = new Paint();
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setColor(getTickColor().getDefaultColor());
        mTickPaint.setStrokeWidth(tickWidth);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        setBackgroundDrawable(mDrawable);
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked() == checked) {
            return;
        }
        mChecked = checked;

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

    public void setStrokeCheckedColor(ColorStateList color) {
        this.strokeCheckedColor = (color != null) ? color : DEFAULT_STROKE_CHECKED_COLOR;
    }

    public void setStrokeCheckedColor(@ColorInt int color) {
        setStrokeCheckedColor(ColorStateList.valueOf(color));
    }

    public void setSolidUncheckedColor(ColorStateList color) {
        this.solidUncheckedColor = (color != null) ? color : DEFAULT_SOLID_UNCHECKED_COLOR;
    }

    public void setSolidUncheckedColor(@ColorInt int color) {
        setSolidUncheckedColor(ColorStateList.valueOf(color));
    }

    public void setSolidCheckedColor(ColorStateList color) {
        this.solidCheckedColor = (color != null) ? color : DEFAULT_STROKE_CHECKED_COLOR;
    }

    public void setSolidCheckedColor(@ColorInt int color) {
        setSolidCheckedColor(ColorStateList.valueOf(color));
    }

    public ColorStateList getTickColor() {
        return tickColor;
    }

    public void setTickColor(ColorStateList color) {
        this.tickColor = (color != null) ? color : DEFAULT_TICK_COLOR;
    }

    public void setTickColor(@ColorInt int color) {
        setTickColor(ColorStateList.valueOf(color));
    }

    public ColorStateList getSolidColor2() {
        if (isChecked()) {
            return solidCheckedColor;
        } else {
            return solidUncheckedColor;
        }
    }

    public ColorStateList getStrokeColor() {
        if (isChecked()) {
            return strokeCheckedColor;
        } else {
            return strokeUncheckedColor;
        }
    }

    public int getTickWidth() {
        return tickWidth;
    }

    public void setTickWidth(int tickWidth) {
        this.tickWidth = tickWidth;
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
        radius[Corner.TOP_LEFT] = topLeft;
        radius[Corner.TOP_RIGHT] = topRight;
        radius[Corner.BOTTOM_RIGHT] = bottomRight;
        radius[Corner.BOTTOM_LEFT] = bottomLeft;
    }

    /** 透明度，取值0~255 */
    public void setAlpha(int alpha) {
        mStrokePaint.setAlpha(alpha);
        mSolidPaint.setAlpha(alpha);
        mStrokePaint.setAlpha(alpha);
    }

    public int getIntAlpha() {
        return alpha;
    }

    private static int getGradientColor(int startColor, int endColor, float percent) {
        int startA = Color.alpha(startColor);
        int startR = Color.red(startColor);
        int startG = Color.green(startColor);
        int startB = Color.blue(startColor);

        int endA = Color.alpha(endColor);
        int endR = Color.red(endColor);
        int endG = Color.green(endColor);
        int endB = Color.blue(endColor);

        int currentA = (int) (startA * (1 - percent) + endA * percent);
        int currentR = (int) (startR * (1 - percent) + endR * percent);
        int currentG = (int) (startG * (1 - percent) + endG * percent);
        int currentB = (int) (startB * (1 - percent) + endB * percent);
        return Color.argb(currentA, currentR, currentG, currentB);
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

    private class CheckBoxDrawable extends Drawable {
        public CheckBoxDrawable() {
            super();
        }

        @Override
        public void draw(Canvas canvas) {
            drawSolid(canvas);
            drawBorder(canvas);
            drawTick(canvas);
        }

        @Override
        public boolean isStateful() {
            return getStrokeColor().isStateful() || getSolidColor2().isStateful() || getTickColor().isStateful();
        }

        @Override
        protected boolean onStateChange(int[] state) {
            int newStrokeColor = getColorForState(state, getStrokeColor());
            int newSolidColor = getColorForState(state, getSolidColor2());
            int newTickColor = getColorForState(state, getTickColor());

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

            return true;
        }

        private void drawSolid(Canvas canvas) {
            canvas.drawOval(mSolidRect, mSolidPaint);
        }

        private void drawBorder(Canvas canvas) {
            mStrokePaint.setColor(getColorForState(getStrokeColor()));
            if (type == TYPE_OVAL) {
                canvas.drawOval(mBorderRect, mStrokePaint);
            }
        }

        private void drawTick(Canvas canvas) {

        }

        private boolean isPressed() {
            int[] states = getState();
            for (int i = 0; i < states.length; i++) {
                if (states[i] == android.R.attr.state_pressed) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void setAlpha(int alpha) {
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
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);

            mSolidRect.set(bounds);
            int offset = strokeWidth / 2;
            mBorderRect.set(mSolidRect.left + offset, mSolidRect.top + offset, mSolidRect.right - offset, mSolidRect.bottom - offset);
            mCenterPoint.x = (int) mSolidRect.centerX();
            mCenterPoint.y = (int) mSolidRect.centerY();
        }

        private int getColorForState(int[] state, ColorStateList colors) {
            return colors.getColorForState(state, colors.getDefaultColor());
        }

        private int getColorForState(ColorStateList colors) {
            return getColorForState(getState(), colors);
        }

        @Override
        public int getIntrinsicWidth() {
            return mSolidRect == null ? 0 : (int) mSolidRect.width();
        }

        @Override
        public int getIntrinsicHeight() {
            return mSolidRect == null ? 0 : (int) mSolidRect.height();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            Corner.TOP_LEFT, Corner.TOP_RIGHT,
            Corner.BOTTOM_LEFT, Corner.BOTTOM_RIGHT
    })
    @interface Corner {
        int TOP_LEFT = 0;
        int TOP_RIGHT = 1;
        int BOTTOM_RIGHT = 2;
        int BOTTOM_LEFT = 3;
    }
}
