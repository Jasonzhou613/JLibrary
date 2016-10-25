package com.ttsea.jlibrary.component.widget;

import android.animation.ValueAnimator;
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
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
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

    private final ColorStateList DEFAULT_STROKE_UNCHECKED_COLOR = ColorStateList.valueOf(0xFFC0C0C0);
    private final ColorStateList DEFAULT_SOLID_UNCHECKED_COLOR = ColorStateList.valueOf(0);
    private final ColorStateList DEFAULT_STROKE_CHECKED_COLOR = ColorStateList.valueOf(0);
    private final ColorStateList DEFAULT_SOLID_CHECKED_COLOR = ColorStateList.valueOf(0xFFDFDFDF);
    private final ColorStateList DEFAULT_TICK_COLOR = ColorStateList.valueOf(0xFFFFFFFF);
    private final int DEFAULT_STROKE_WIDTH = -1;
    private final int DEFAULT_TICK_WIDTH = -1;
    private final int DEFAULT_ANIM_DURATION = 300;
    private final int DEFAULT_ALPHA = 255;
    private final boolean DEFAULT_SHOULD_ANIMATE = true;
    private final boolean DEFAULT_CHECKED = false;

    /** 未checked边框颜色 */
    private ColorStateList strokeUncheckedColor = DEFAULT_STROKE_UNCHECKED_COLOR;
    /** checked边框颜色 */
    private ColorStateList strokeCheckedColor = DEFAULT_STROKE_CHECKED_COLOR;
    /** 未checked填充颜色 */
    private ColorStateList solidUnCheckedColor = DEFAULT_SOLID_UNCHECKED_COLOR;
    /** checked填充颜色 */
    private ColorStateList solidCheckedColor = DEFAULT_SOLID_CHECKED_COLOR;
    /** 勾的颜色 */
    private ColorStateList tickColor = DEFAULT_TICK_COLOR;

    private int strokeWidth;
    private int tickWidth;
    private int animDuration;
    private int type;
    private int alpha;
    private int radius;
    private boolean shouldAnimate;
    private boolean animateStarting;
    private boolean mTickDrawing;
    private boolean mChecked;

    private CheckBoxPaint mStrokePaint;
    private CheckBoxPaint mSolidPaint;
    private CheckBoxPaint mTickPaint;

    private CheckBoxDrawable mDrawable;
    private boolean checkChangedByClick = false;

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
        strokeWidth = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_scb_strokeWidth, DEFAULT_STROKE_WIDTH);
        strokeUncheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_scb_strokeUncheckedColor);
        if (strokeUncheckedColor == null) {
            strokeUncheckedColor = DEFAULT_STROKE_UNCHECKED_COLOR;
        }
        strokeCheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_scb_strokeCheckedColor);
        if (strokeCheckedColor == null) {
            strokeCheckedColor = DEFAULT_STROKE_CHECKED_COLOR;
        }
        solidUnCheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_scb_solidUncheckedColor);
        if (solidUnCheckedColor == null) {
            solidUnCheckedColor = DEFAULT_SOLID_UNCHECKED_COLOR;
        }
        solidCheckedColor = a.getColorStateList(R.styleable.SmoothCheckBox_scb_solidCheckedColor);
        if (solidCheckedColor == null) {
            solidCheckedColor = DEFAULT_SOLID_CHECKED_COLOR;
        }
        tickWidth = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_scb_tickWidth, DEFAULT_TICK_WIDTH);
        tickColor = a.getColorStateList(R.styleable.SmoothCheckBox_scb_tickColor);
        if (tickColor == null) {
            tickColor = DEFAULT_TICK_COLOR;
        }
        shouldAnimate = a.getBoolean(R.styleable.SmoothCheckBox_scb_shouldAnimate, DEFAULT_SHOULD_ANIMATE);
        animDuration = a.getInt(R.styleable.SmoothCheckBox_scb_duration, DEFAULT_ANIM_DURATION);
        type = a.getInt(R.styleable.SmoothCheckBox_scb_type, TYPE_OVAL);
        mChecked = a.getBoolean(R.styleable.SmoothCheckBox_scb_checked, DEFAULT_CHECKED);
        alpha = a.getInt(R.styleable.SmoothCheckBox_scb_alpha, DEFAULT_ALPHA);
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 255) {
            alpha = 255;
        }

        radius = a.getDimensionPixelOffset(R.styleable.SmoothCheckBox_scb_radius, -1);
        if (radius < 0) {
            radius = 0;
        }

        JLog.d(TAG, "strokeWidth:" + strokeWidth
                + ",\n strokeUncheckedColor:" + strokeUncheckedColor
                + ",\n strokeCheckedColor:" + strokeCheckedColor
                + ",\n solidUnCheckedColor:" + solidUnCheckedColor
                + ",\n solidCheckedColor:" + solidCheckedColor
                + ",\n tickWidth:" + tickWidth
                + ",\n tickColor:" + tickColor
                + ",\n type:" + type
                + ",\n shouldAnimate:" + shouldAnimate
                + ",\n animDuration:" + animDuration
                + ",\n mChecked:" + mChecked
                + ",\n alpha:" + alpha
                + ",\n radius:" + radius
        );
    }

    private void init() {
        if (mDrawable == null) {
            mDrawable = new CheckBoxDrawable();
        }
        mStrokePaint = new CheckBoxPaint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStrokeWidth(strokeWidth);

        mSolidPaint = new CheckBoxPaint();
        mSolidPaint.setStyle(Paint.Style.FILL);
        mSolidPaint.setStrokeCap(Paint.Cap.ROUND);
        mSolidPaint.setAntiAlias(true);

        mTickPaint = new CheckBoxPaint();
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setStrokeWidth(tickWidth);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkChangedByClick = true;
                mDrawable.reset();
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

    public void setSolidUnCheckedColor(ColorStateList color) {
        this.solidUnCheckedColor = (color != null) ? color : DEFAULT_SOLID_UNCHECKED_COLOR;
    }

    public void setSolidUncheckedColor(@ColorInt int color) {
        setSolidUnCheckedColor(ColorStateList.valueOf(color));
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

    public ColorStateList getSolidColorStateList() {
        if (isChecked()) {
            return solidCheckedColor;
        } else {
            return solidUnCheckedColor;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
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

    private static int getAlphaColor(int color, float percent) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int currentA = (int) (a * percent);
        return Color.argb(currentA, r, g, b);
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
        private Point mCenterPoint;
        private RectF mBounds;
        private RectF mSolidRect;
        private RectF mGradientBounds;
        private RectF mBorderRect;

        private int mSolidColor;
        private int mStrokeColor;
        private int mTickColor;

        private Point[] mTickPoints;
        private Path mTickPath;
        private float mLeftLineDistance, mRightLineDistance, mDrewDistance;
        private boolean isReset = false;

        private Handler mHandler;

        public CheckBoxDrawable() {
            super();
            mCenterPoint = new Point();
            mBounds = new RectF();
            mGradientBounds = new RectF();
            mSolidRect = new RectF();
            mBorderRect = new RectF();

            mGradientBounds.setEmpty();
            mSolidRect.setEmpty();
            mBorderRect.setEmpty();

            mTickPoints = new Point[3];
            mTickPoints[0] = new Point();
            mTickPoints[1] = new Point();
            mTickPoints[2] = new Point();

            mTickPath = new Path();
            mHandler = new Handler();
        }

        private void reset() {
            mDrewDistance = 0;
            isReset = true;
            JLog.d(TAG, "isReset = true;");
        }

        @Override
        public void draw(Canvas canvas) {
            if (isChecked()) {
                drawCheckedGraphics(canvas);
                drawTick(canvas);
            } else {
                drawUnCheckedGraphics(canvas);
            }
        }

        private void drawTick(Canvas canvas) {
            mTickPaint.setColor(mTickColor);

            if (!animateStarting) {
                mTickPath.moveTo(mTickPoints[0].x, mTickPoints[0].y);
                mTickPath.lineTo(mTickPoints[1].x, mTickPoints[1].y);
                canvas.drawPath(mTickPath, mTickPaint);

                mTickPath.moveTo(mTickPoints[1].x, mTickPoints[1].y);
                mTickPath.lineTo(mTickPoints[2].x, mTickPoints[2].y);
                canvas.drawPath(mTickPath, mTickPaint);
                return;
            }

            if (!mTickDrawing) {
                return;
            }

            if (mDrewDistance < mLeftLineDistance) {
                float step = (getMeasuredWidth() / 20.0f) < 3 ? 3 : (getMeasuredWidth() / 20.0f);
                mDrewDistance += step;
                float stopX = mTickPoints[0].x + (mTickPoints[1].x - mTickPoints[0].x) * mDrewDistance / mLeftLineDistance;
                float stopY = mTickPoints[0].y + (mTickPoints[1].y - mTickPoints[0].y) * mDrewDistance / mLeftLineDistance;

                mTickPath.reset();
                mTickPath.moveTo(mTickPoints[0].x, mTickPoints[0].y);
                mTickPath.lineTo(stopX, stopY);
                canvas.drawPath(mTickPath, mTickPaint);

                if (mDrewDistance > mLeftLineDistance) {
                    mDrewDistance = mLeftLineDistance;
                }
            } else {
                mTickPath.moveTo(mTickPoints[0].x, mTickPoints[0].y);
                mTickPath.lineTo(mTickPoints[1].x, mTickPoints[1].y);
                canvas.drawPath(mTickPath, mTickPaint);

                // draw right of the tick
                if (mDrewDistance < mLeftLineDistance + mRightLineDistance) {
                    float stopX = mTickPoints[1].x + (mTickPoints[2].x - mTickPoints[1].x) * (mDrewDistance - mLeftLineDistance) / mRightLineDistance;
                    float stopY = mTickPoints[1].y - (mTickPoints[1].y - mTickPoints[2].y) * (mDrewDistance - mLeftLineDistance) / mRightLineDistance;

                    mTickPath.reset();
                    mTickPath.moveTo(mTickPoints[1].x, mTickPoints[1].y);
                    mTickPath.lineTo(stopX, stopY);
                    canvas.drawPath(mTickPath, mTickPaint);

                    float step = (getMeasuredWidth() / 20.0f) < 3 ? 3 : (getMeasuredWidth() / 20.0f);
                    mDrewDistance += step;
                }
            }

            // invalidate
            if (mDrewDistance < mLeftLineDistance + mRightLineDistance) {
                drawTickDelay(10);
            } else {
                mTickDrawing = false;
            }
        }

        private void drawTickDelay(long delayMillis) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTickDrawing = true;
                    invalidateSelf();
                }
            }, delayMillis);
        }

        private void drawCheckedGraphics(Canvas canvas) {
            mSolidPaint.setColor(mSolidColor);
            if (type == TYPE_RECTANGLE) {
                canvas.drawRoundRect(mSolidRect, radius, radius, mSolidPaint);
            } else {
                canvas.drawOval(mSolidRect, mSolidPaint);
            }

            if (shouldAnimate) {
                mSolidPaint.setColor(getColorForState(solidUnCheckedColor));
                if (type == TYPE_RECTANGLE) {
                    canvas.drawRoundRect(mGradientBounds, radius, radius, mSolidPaint);
                } else {
                    canvas.drawOval(mGradientBounds, mSolidPaint);
                }
            }

            //加这句话，是为了显示pressed效果
            if (mGradientBounds.isEmpty()) {
                mSolidPaint.setColor(getColorForState(solidCheckedColor));
                if (type == TYPE_RECTANGLE) {
                    canvas.drawRoundRect(mSolidRect, radius, radius, mSolidPaint);
                } else {
                    canvas.drawOval(mSolidRect, mSolidPaint);
                }
            }

            mStrokePaint.setColor(mStrokeColor);
            if (type == TYPE_RECTANGLE) {
                canvas.drawRoundRect(mBorderRect, radius, radius, mStrokePaint);
            } else {
                canvas.drawOval(mBorderRect, mStrokePaint);
            }
        }

        private void drawUnCheckedGraphics(Canvas canvas) {
            mSolidPaint.setColor(mSolidColor);
            if (type == TYPE_RECTANGLE) {
                canvas.drawRoundRect(mSolidRect, radius, radius, mSolidPaint);
            } else {
                canvas.drawOval(mSolidRect, mSolidPaint);
            }

            mSolidPaint.setColor(getColorForState(solidUnCheckedColor));
            if (type == TYPE_RECTANGLE) {
                canvas.drawRoundRect(mGradientBounds, radius, radius, mSolidPaint);
            } else {
                canvas.drawOval(mGradientBounds, mSolidPaint);
            }

            mStrokePaint.setColor(mStrokeColor);
            if (type == TYPE_RECTANGLE) {
                canvas.drawRoundRect(mBorderRect, radius, radius, mStrokePaint);
            } else {
                canvas.drawOval(mBorderRect, mStrokePaint);
            }
        }

        private void startCommonAnimation() {
            final ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.0f, 0.8f, 1.0f);
            scaleAnimator.setDuration((animDuration * 2) / 3);
            scaleAnimator.setInterpolator(new LinearInterpolator());
            scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mSolidRect = getSolidRect(value);
                    mBorderRect = getBorderRect(value);

                    if (isReset) {
                        scaleAnimator.cancel();
                    }
                }
            });
            scaleAnimator.start();

            final ValueAnimator solidColorAnimator = ValueAnimator.ofFloat(1.0f, 0.3f, 1.0f);
            solidColorAnimator.setDuration((animDuration * 2) / 3);
            solidColorAnimator.setInterpolator(new LinearInterpolator());
            solidColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mSolidColor = getAlphaColor(getColorForState(solidCheckedColor), value);
                    if (isReset) {
                        solidColorAnimator.cancel();
                    }
                }
            });
            solidColorAnimator.start();

            final ValueAnimator strokeColorAnimator = ValueAnimator.ofFloat(0, 1.0f);
            strokeColorAnimator.setDuration((animDuration));
            strokeColorAnimator.setInterpolator(new LinearInterpolator());
            strokeColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    if (value < 0.96) {
                        value = 0.0f;
                    }
                    mStrokeColor = getAlphaColor(getColorForState(getStrokeColor()), value);
                    if (isReset) {
                        strokeColorAnimator.cancel();
                    }
                }
            });
            strokeColorAnimator.start();
        }

        private void startCheckedAnimation() {
            final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.setDuration(animDuration);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();

                    float offsetX = (mSolidRect.width() / 2) * (1 - value);
                    float offsetY = (mSolidRect.height() / 2) * (1 - value);
                    float left = mCenterPoint.x - offsetX;
                    float top = mCenterPoint.y - offsetY;
                    float right = mCenterPoint.x + offsetX;
                    float bottom = mCenterPoint.y + offsetY;
                    mGradientBounds.set(left, top, right, bottom);

                    if (isReset) {
                        animator.cancel();
                        JLog.d(TAG, "animator.cancel()");
                    } else {
                        invalidateSelf();
                    }
                }
            });
            animator.start();

            startCommonAnimation();
            drawTickDelay(animDuration + 50);
        }

        private void startUnCheckedAnimation() {
            final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.setDuration(animDuration);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();

                    float offsetX = (mSolidRect.width() / 2) * value;
                    float offsetY = (mSolidRect.height() / 2) * value;
                    float left = mCenterPoint.x - offsetX;
                    float top = mCenterPoint.y - offsetY;
                    float right = mCenterPoint.x + offsetX;
                    float bottom = mCenterPoint.y + offsetY;
                    mGradientBounds.set(left, top, right, bottom);

                    if (isReset) {
                        animator.cancel();
                        JLog.d(TAG, "animator.cancel()");
                    } else {
                        invalidateSelf();
                    }
                }
            });
            animator.start();

            startCommonAnimation();
        }

        private RectF getSolidRect(float scale) {
            float offsetX = (mBounds.width() / 2) * scale;
            float offsetY = (mBounds.height() / 2) * scale;

            float left = mCenterPoint.x - offsetX;
            float top = mCenterPoint.y - offsetY;
            float right = mCenterPoint.x + offsetX;
            float bottom = mCenterPoint.y + offsetY;
            mSolidRect.set(left, top, right, bottom);

            return mSolidRect;
        }

        private RectF getBorderRect(float scale) {
            float offsetX = ((mBounds.width() / 2) * scale) - (strokeWidth / 2);
            float offsetY = ((mBounds.height() / 2) * scale) - (strokeWidth / 2);

            float left = mCenterPoint.x - offsetX;
            float top = mCenterPoint.y - offsetY;
            float right = mCenterPoint.x + offsetX;
            float bottom = mCenterPoint.y + offsetY;
            mBorderRect.set(left, top, right, bottom);
            return mBorderRect;
        }

        @Override
        public boolean isStateful() {
            return getStrokeColor().isStateful() || getSolidColorStateList().isStateful()
                    || getTickColor().isStateful();
        }

        @Override
        protected boolean onStateChange(int[] state) {
            reset();
            mSolidColor = getColorForState(state, getSolidColorStateList());
            mStrokeColor = getColorForState(state, getStrokeColor());
            mTickColor = getColorForState(state, getTickColor());

            animateStarting = shouldAnimate && checkChangedByClick && !isPressed();
            if (animateStarting) {
                checkChangedByClick = false;
                isReset = false;
                if (isChecked()) {
                    startCheckedAnimation();
                } else {
                    startUnCheckedAnimation();
                }
            } else {
                invalidateSelf();
            }
            return true;
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
            SmoothCheckBox.this.alpha = alpha;
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

            reset();

            int width = getMeasuredWidth();
            int height = getMeasuredHeight();

            if (strokeWidth < 0) {
                strokeWidth = (Math.min(width, height) / 15) < 1 ? 1 : Math.min(width, height) / 15;
                mStrokePaint.setStrokeWidth(strokeWidth);
            }

            if (tickWidth < 0) {
                tickWidth = (Math.min(width, height) / 10) < 1 ? 1 : Math.min(width, height) / 10;
                mTickPaint.setStrokeWidth(tickWidth);
            }

            mBounds.set(bounds);
            mSolidRect.set(bounds);
            mGradientBounds.set(bounds);

            float offset = ((float) strokeWidth) / 2;
            mBorderRect.set(mSolidRect.left + offset, mSolidRect.top + offset, mSolidRect.right - offset, mSolidRect.bottom - offset);

            mCenterPoint.x = (int) mSolidRect.centerX();
            mCenterPoint.y = (int) mSolidRect.centerY();

            mTickPoints[0].x = Math.round((float) width / 30 * 7);
            mTickPoints[0].y = Math.round((float) height / 30 * 14);
            mTickPoints[1].x = Math.round((float) width / 30 * 13);
            mTickPoints[1].y = Math.round((float) height / 30 * 20);
            mTickPoints[2].x = Math.round((float) width / 30 * 22);
            mTickPoints[2].y = Math.round((float) height / 30 * 10);

            mLeftLineDistance = (float) Math.sqrt(Math.pow(mTickPoints[1].x - mTickPoints[0].x, 2) +
                    Math.pow(mTickPoints[1].y - mTickPoints[0].y, 2));
            mRightLineDistance = (float) Math.sqrt(Math.pow(mTickPoints[2].x - mTickPoints[1].x, 2) +
                    Math.pow(mTickPoints[2].y - mTickPoints[1].y, 2));
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

    private class CheckBoxPaint extends Paint {

        @Override
        public void setColor(int color) {
            super.setColor(color);
            if (color != 0) {
                setAlpha(alpha);
            }
        }
    }
}
