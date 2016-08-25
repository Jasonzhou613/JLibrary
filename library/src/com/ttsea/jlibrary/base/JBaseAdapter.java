package com.ttsea.jlibrary.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 适配器基类 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/8/6 9:20 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/8/6 9:20
 */
public abstract class JBaseAdapter<E> extends BaseAdapter {
    public String TAG = JBaseAdapter.class.getSimpleName();

    public Context mContext;
    public List<E> mList;
    public LayoutInflater mInflater;

    public JBaseAdapter(Context context, List<E> list) {
        this.mContext = context;
        this.mList = list;

        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public E getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<E> items) {
        mList.clear();
        if (items != null) {
            mList.addAll(items);
        }
        notifyDataSetChanged();
    }
}
