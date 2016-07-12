package com.ttsea.jlibrary.interfaces;

import android.view.View;

/**
 * 可用于为ExpandableListView child item中的某个view设置监听 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/5/17 16:19 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/17 16:19
 */
public interface OnChildViewClickListener {

    /**
     * child item中的view被点击
     *
     * @param v             被点击的View
     * @param groupPosition v所处的组位置
     * @param childPosition v所处的child位置
     */
    void onChildViewClick(View v, int groupPosition, int childPosition);
}
