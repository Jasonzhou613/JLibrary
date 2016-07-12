package com.ttsea.jlibrary.interfaces;

import android.view.View;

/**
 * 可用于为ExpandableListView goup item中的某个view设置监听 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/5/17 16:16 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/17 16:16
 */
public interface OnGroupViewClickListener {

    /**
     * group item中的view被点击
     *
     * @param v             被点击的View
     * @param groupPosition v所处的组位置
     */
    void onGroupViewClick(View v, int groupPosition);

}
