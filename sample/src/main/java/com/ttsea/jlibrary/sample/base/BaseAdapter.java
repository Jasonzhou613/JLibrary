package com.ttsea.jlibrary.sample.base;

import android.content.Context;

import com.ttsea.jlibrary.base.JBaseAdapter;

import java.util.List;

/**
 * 适配器基类，继承JBaseAdapter <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/8/6 9:20 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/8/6 9:20
 */
public abstract class BaseAdapter<E> extends JBaseAdapter {

    public BaseAdapter(Context context, List<E> list) {
        super(context, list);
    }
}
