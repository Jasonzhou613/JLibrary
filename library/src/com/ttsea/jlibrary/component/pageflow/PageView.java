/***
 * Copyright (C) 2011 Patrik Åkerfeldt
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***/
package com.ttsea.jlibrary.component.pageflow;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;


/**
 * viewflow
 * <p/>
 * Andy Update By 2014-3-18
 **/
public class PageView extends AdapterView<Adapter> {
    private final String TAG = "PageView";

    private Adapter mAdapter;
    private Handler mHandler;


    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHandler = new Handler();
    }


    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        setAdapter(adapter, 0);
    }

    public void setAdapter(Adapter adapter, int position) {

    }

    @Override
    public View getSelectedView() {
        return null;
    }


    @Override
    public void setSelection(int position) {

    }


    /**
     * 手势监听（用于识别手势滑动）
     */
    class YScrollDetector extends SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            /**
             * if we're scrolling more closer to x direction, return false, let
             * subview to process it
             */
            if (Math.abs(distanceX) > 10) {
                return false;
            }
            return (Math.abs(distanceY) > Math.abs(distanceX));
        }
    }

    public interface ViewSwitchListener {

        /**
         * This method is called when a new View has been scrolled to.
         *
         * @param view     the {@link View} currently in focus.
         * @param position The position in the adapter of the {@link View} currently
         *                 in focus.
         */
        void onSwitched(View view, int position);
    }
}