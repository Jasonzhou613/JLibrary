package com.ttsea.jlibrary.photo.gallery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ttsea.jlibrary.common.JLog;

public class ViewPagerFixed extends android.support.v4.view.ViewPager {
    private final String TAG = "ViewPagerFixed";

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            JLog.e(TAG, "IllegalArgumentException ex:" + ex.toString());
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            JLog.e(TAG, "IllegalArgumentException ex:" + ex.toString());
        }
        return false;
    }
}
