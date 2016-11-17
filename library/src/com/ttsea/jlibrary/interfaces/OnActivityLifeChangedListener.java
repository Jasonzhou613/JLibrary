package com.ttsea.jlibrary.interfaces;

/**
 * // to do <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/11/17 10:22 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/11/17 10:22.
 */
public interface OnActivityLifeChangedListener {
    /**
     * 在Activity或者Fragment中的onCreate调用，基本用不上这个方法
     */
    void onCreate();

    /**
     * 在Activity或者Fragment中的onStart调用
     */
    void onStart();

    /**
     * 在Activity或者Fragment中的onResume调用
     */
    void onResume();

    /**
     * 在Activity或者Fragment中的onPause调用
     */
    void onPause();

    /**
     * 在Activity或者Fragment中的onStop调用
     */
    void onStop();

    /**
     * 在Activity或者Fragment中的onDestroy调用
     */
    void onDestroy();
}