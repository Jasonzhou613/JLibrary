
package com.ttsea.jlibrary.component.pageflow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public abstract class Indicator extends View implements PageView.OnViewSwitchListener {

    public Indicator(Context context) {
        super(context);
    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Indicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set the current PageView. This method is called by the ViewFlow when the
     * Indicator is attached to it.
     *
     * @param view
     */
    abstract void setPageView(PageView view);

    /**
     * The current PageView motion event
     *
     * @param event
     */
    abstract boolean onPageViewTouchEvent(MotionEvent event);

    /**
     * 移动到指定点
     */
    abstract void scrollToIndex(int position, int duration);

    /**
     * 移动到下一个点
     */
    abstract void scrollNextIndex(int duration);

    /**
     * 移动到上一个点
     */
    abstract void scrollPreIndex(int duration);

    /**
     * 回到原有的点上
     */
    abstract void resetIndex(int duration);
}
