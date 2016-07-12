package com.ttsea.jlibrary.interfaces;

import android.view.View;

/**
 * 可用于为ListView item中的某个view设置监听 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/5/17 16:16 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/17 16:16
 */
public interface OnItemViewClickListener {

    /**
     * listitem中view被点击
     *
     * @param v        被点击的view
     * @param position v所处位置
     */
    void onItemViewClick(View v, int position);
}
