
package com.ttsea.jlibrary.component.pageflow;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * An interface which defines the contract between a ViewFlow and a
 * Indicator.<br/>
 * A Indicator is responsible to show an visual indicator on the total views
 * number and the current visible view.<br/>
 */
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
     * Set the current ViewFlow. This method is called by the ViewFlow when the
     * Indicator is attached to it.
     *
     * @param view
     */
    abstract void setPageView(PageView view);

    /**
     * The scroll position has been changed. A Indicator may implement this
     * method to reflect the current position
     *
     * @param h
     * @param v
     * @param oldh
     * @param oldv
     */
    abstract void onScrolled(int h, int v, int oldh, int oldv);
}
