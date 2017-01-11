package com.ttsea.jlibrary.interfaces;

import android.view.View;

/**
 * 防止view多次重复点击 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2017/1/11 17:15 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2017/1/11 17:15.
 */
public abstract class OnSingleClickListener implements View.OnClickListener {

    /** 默认连续两次点击事件间隔 */
    private static final long DEFAULT_CLICK_INTERVAL = 1000;

    private long clickInterval = DEFAULT_CLICK_INTERVAL;
    private long clickPointTime = 0;

    /**
     * 默认构成函数，连续两次点击时间间隔为{@link #clickInterval}
     */
    public OnSingleClickListener() {
        this(DEFAULT_CLICK_INTERVAL);
    }

    /**
     * 构造函数
     *
     * @param clickInterval 连续两次点击的事件间隔
     */
    public OnSingleClickListener(long clickInterval) {
        this.clickInterval = clickInterval;
    }

    @Override
    public void onClick(View v) {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - clickPointTime < clickInterval) {
            return;
        }
        clickPointTime = timeMillis;
        onSingleClick(v);
    }

    public abstract void onSingleClick(View v);

}
