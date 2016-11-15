package com.ttsea.jlibrary.component.pageflow;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JLog;


public class PageIndicator extends Indicator {
    private final String TAG = "PageIndicator";

    public static final int STYLE_STROKE = 0;
    public static final int STYLE_FILL = 1;
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;

    private final int DEFAULT_WIDTH = 12;
    private final int DEFAULT_HEIGHT = 12;
    private final int DEFAULT_RADIUS = 6;
    private final int DEFAULT_STROKE_WIDTH = 2;
    private final int DEFAULT_INDICATOR_SPACE = (3 * DEFAULT_WIDTH) / 2;
    private final int DEFAULT_ACTIVE_COLOR = 0xFFFFFFFF;
    private final int DEFAULT_INACTIVE_COLOR = 0xFF808080;

    /** 两个单个指示器宽度 */
    private int indicatorWidth = DEFAULT_WIDTH;
    /** 两个单个指示器高度 */
    private int indicatorHeight = DEFAULT_HEIGHT;
    private float radius = DEFAULT_WIDTH;
    private float strokeWidth = DEFAULT_STROKE_WIDTH;
    /** 两个指示器间距 */
    private int indicatorSpace = DEFAULT_INDICATOR_SPACE;
    /** 激活的指示器颜色 */
    private int activeColor = DEFAULT_ACTIVE_COLOR;
    /** 未激活的指示器颜色 */
    private int inActiveColor = DEFAULT_INACTIVE_COLOR;
    /** 激活的指示器样式 */
    private int activeStyle = STYLE_FILL;
    /** 未激活的指示器样式 */
    private int inActiveStyle = STYLE_STROKE;
    /** 两个单个指示器方向，默认为水平 */
    private int orientation = ORIENTATION_HORIZONTAL;

    private Context mContext;
    private Paint mPaintActive;
    private Paint mPaintInActive;
    private PageView pageView;
    private RectF inActiveRectF;
    private RectF activeRectF;
    private int mCount = 2;
    private int mCurrentIndicatorIndex = 0;
    private float mCurrentScroll = 0;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator);
        initStyleable(a);
        init();
    }

    private void initStyleable(TypedArray a) {
        // getDimension
        // 获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘   返回float
        // getDimensionPixelOffset
        // 获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘  返回int
        // getDimensionPixelSize
        // 则不管写的是dp还是sp还是px,都会乘以denstiy.

        indicatorWidth = a.getDimensionPixelOffset(R.styleable.PageIndicator_piIndicatorWidth, DEFAULT_WIDTH);
        indicatorHeight = a.getDimensionPixelOffset(R.styleable.PageIndicator_piIndicatorHeight, DEFAULT_HEIGHT);
        radius = a.getDimensionPixelOffset(R.styleable.PageIndicator_piRadius, DEFAULT_RADIUS);
        strokeWidth = a.getDimensionPixelOffset(R.styleable.PageIndicator_piStrokeWidth, DEFAULT_STROKE_WIDTH);
        indicatorSpace = a.getDimensionPixelOffset(R.styleable.PageIndicator_piIndicatorSpace, DEFAULT_INDICATOR_SPACE);
        activeColor = a.getColor(R.styleable.PageIndicator_piActiveColor, DEFAULT_ACTIVE_COLOR);
        inActiveColor = a.getColor(R.styleable.PageIndicator_piInActiveColor, DEFAULT_INACTIVE_COLOR);
        activeStyle = a.getInt(R.styleable.PageIndicator_piActiveStyle, STYLE_FILL);
        inActiveStyle = a.getInt(R.styleable.PageIndicator_piInActiveStyle, STYLE_FILL);
        orientation = a.getInt(R.styleable.PageIndicator_piOrientation, ORIENTATION_HORIZONTAL);

        JLog.d(TAG, "indicatorWidth:" + indicatorWidth
                + ",\n indicatorHeight:" + indicatorHeight
                + ",\n radius:" + radius
                + ",\n strokeWidth:" + strokeWidth
                + ",\n indicatorSpace:" + indicatorSpace
                + ",\n activeColor:" + activeColor
                + ",\n inActiveColor:" + inActiveColor
                + ",\n activeStyle:" + activeStyle
                + ",\n inActiveStyle:" + inActiveStyle
                + ",\n orientation:" + orientation
        );
    }

    private void init() {
        mPaintActive = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintInActive = new Paint(Paint.ANTI_ALIAS_FLAG);
        inActiveRectF = new RectF();
        activeRectF = new RectF();
        updatePaints();
    }

    private void updatePaints() {
        if (activeStyle == STYLE_STROKE) {
            mPaintActive.setStyle(Style.STROKE);
            mPaintActive.setStrokeWidth(strokeWidth);
        } else {
            mPaintActive.setStyle(Style.FILL);
        }
        mPaintActive.setColor(activeColor);

        if (inActiveStyle == STYLE_STROKE) {
            mPaintInActive.setStyle(Style.STROKE);
            mPaintInActive.setStrokeWidth(strokeWidth);
        } else {
            mPaintInActive.setStyle(Style.FILL);
        }
        mPaintInActive.setColor(inActiveColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;

        } else {
            // Calculate the width according the views count
            if (getCount() == 0) {
                return result;

            } else if (getCount() == 1) {
                result = getPaddingLeft() + getPaddingRight() + indicatorWidth;

            } else {
                if (orientation == ORIENTATION_HORIZONTAL) {
                    result = getPaddingLeft() + getPaddingRight() + (getCount() - 1) * indicatorSpace + getCount() * indicatorWidth;
                } else {
                    result = getPaddingLeft() + getPaddingRight() + indicatorWidth;
                }
            }
            // Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;

        } else {
            // Calculate the width according the views count
            if (getCount() == 0) {
                return result;

            } else if (getCount() == 1) {
                result = getPaddingTop() + getPaddingBottom() + indicatorWidth;

            } else {
                if (orientation == ORIENTATION_HORIZONTAL) {
                    result = getPaddingTop() + getPaddingBottom() + indicatorHeight;
                } else {
                    result = getPaddingTop() + getPaddingBottom() + (getCount() - 1) * indicatorSpace + getCount() * indicatorHeight;
                }
            }
            // Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int leftPadding = getPaddingLeft();
        int topPadding = getPaddingTop();
        inActiveRectF.setEmpty();
        activeRectF.setEmpty();

        if (orientation == ORIENTATION_VERTICAL) {
            for (int i = 0; i < getCount(); i++) {
                float l = (float) leftPadding;
                float t = (float) topPadding + (i * (indicatorHeight + indicatorSpace));
                float r = l + indicatorWidth;
                float b = t + indicatorHeight;
                inActiveRectF.set(l, t, r, b);

                canvas.drawRoundRect(inActiveRectF, radius, radius, mPaintInActive);
            }
            activeRectF.set(leftPadding, topPadding + mCurrentScroll, leftPadding + indicatorWidth, topPadding + mCurrentScroll + indicatorHeight);
            canvas.drawRoundRect(activeRectF, radius, radius, mPaintActive);

        } else {
            for (int i = 0; i < getCount(); i++) {
                float l = (float) leftPadding + i * (indicatorWidth + indicatorSpace);
                float t = (float) topPadding;
                float r = l + indicatorWidth;
                float b = t + indicatorHeight;
                inActiveRectF.set(l, t, r, b);

                canvas.drawRoundRect(inActiveRectF, radius, radius, mPaintInActive);
            }
            activeRectF.set(leftPadding + mCurrentScroll, topPadding, leftPadding + mCurrentScroll + indicatorWidth, topPadding + indicatorHeight);
            canvas.drawRoundRect(activeRectF, radius, radius, mPaintActive);
        }
    }

    @Override
    public void onSwitched(View view, int position) {
        mCurrentIndicatorIndex = position;
        JLog.d(TAG, "mCurrentIndicatorIndex:" + mCurrentIndicatorIndex);
    }

    @Override
    public void onScrolled(int h, int v, int oldh, int oldv) {
        if (pageView == null || pageView.getWidth() == 0) {
            return;
        }

        int dx = oldh - h;
        float scrollPercentage = 0.0f;
        float offset = 0.0f;

        if (orientation == ORIENTATION_HORIZONTAL) {
            if (pageView.getScrollState() == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                scrollPercentage = ((float) dx) / pageView.getWidth();
                offset = (indicatorSpace + indicatorWidth) * scrollPercentage;
                mCurrentScroll = mCurrentIndicatorIndex * (indicatorSpace + indicatorWidth) + offset;
            } else {

                scrollPercentage = ((float) dx) / pageView.getWidth();
                offset = (indicatorSpace + indicatorWidth) * scrollPercentage;
                mCurrentScroll = mCurrentIndicatorIndex * (indicatorSpace + indicatorWidth) + offset;
            }
            JLog.d(TAG, "adapterIndex:" + pageView.getCurrentAdapterIndex() + ", offset:" + offset + ", mCurrentScroll:" + mCurrentScroll);

        } else if (orientation == ORIENTATION_VERTICAL) {

        }

        invalidate();
    }

    public int getIndicatorWidth() {
        return indicatorWidth;
    }

    public void setIndicatorWidth(int flowWidthPx) {
        this.indicatorWidth = flowWidthPx;
        updatePaints();
        postInvalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setIndicatorHeight(int flowHeightPx) {
        this.indicatorHeight = flowHeightPx;
        updatePaints();
        postInvalidate();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radiusPx) {
        this.radius = radiusPx;
        updatePaints();
        postInvalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidthPx) {
        this.strokeWidth = strokeWidthPx;
        updatePaints();
        postInvalidate();
    }

    public float getIndicatorSpace() {
        return indicatorSpace;
    }

    public void setIndicatorSpace(int indicatorSpacePx) {
        this.indicatorSpace = indicatorSpacePx;
        updatePaints();
        postInvalidate();
    }

    public int getActiveColor() {
        return activeColor;
    }

    public void setActiveColorRes(int colorRes) {
        setActiveColor(mContext.getResources().getColor(colorRes));
    }

    public void setActiveColor(int activeColor) {
        this.activeColor = activeColor;
        updatePaints();
        postInvalidate();
    }

    public int getInActiveColor() {
        return inActiveColor;
    }

    public void setInActiveColorRes(int colorRes) {
        setInActiveColor(mContext.getResources().getColor(colorRes));
    }

    public void setInActiveColor(int inActiveColor) {
        this.inActiveColor = inActiveColor;
        updatePaints();
        postInvalidate();
    }

    public int getActiveStyle() {
        return activeStyle;
    }

    /**
     * 设置激活的指示器的样式，如{@link #STYLE_FILL} and {@link #STYLE_STROKE}
     *
     * @param activeStyle fill and stroke
     */
    public void setActiveStyle(int activeStyle) {
        this.activeStyle = activeStyle;
        updatePaints();
        postInvalidate();
    }

    public int getInActiveStyle() {
        return inActiveStyle;
    }

    /**
     * 设置未激活的指示器的样式，如{@link #STYLE_FILL} and {@link #STYLE_STROKE}
     *
     * @param inActiveStyle fill and stroke
     */
    public void setInActiveStyle(int inActiveStyle) {
        this.inActiveStyle = inActiveStyle;
        updatePaints();
        postInvalidate();
    }

    public int getOrientation() {
        return orientation;
    }

    /**
     * 设置指示器方向，如{@link #ORIENTATION_HORIZONTAL} and {@link #ORIENTATION_VERTICAL}
     *
     * @param orientation 水平和垂直方向
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        postInvalidate();
    }

    public int getCount() {
        if (pageView != null) {
            mCount = pageView.getCount();
        }
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public void setPageView(PageView pageView) {
        this.pageView = pageView;
        postInvalidate();
    }
}