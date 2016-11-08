
package com.ttsea.jlibrary.component.pageflow;


/**
 * An interface which defines the contract between a ViewFlow and a
 * Indicator.<br/>
 * A Indicator is responsible to show an visual indicator on the total views
 * number and the current visible view.<br/>
 */
public interface Indicator extends PageView.ViewSwitchListener {

    /**
     * Set the current ViewFlow. This method is called by the ViewFlow when the
     * Indicator is attached to it.
     *
     * @param view
     */
    void setPageView(PageView view);

    /**
     * The scroll position has been changed. A Indicator may implement this
     * method to reflect the current position
     *
     * @param h
     * @param v
     * @param oldh
     * @param oldv
     */
    void onScrolled(int h, int v, int oldh, int oldv);
}
