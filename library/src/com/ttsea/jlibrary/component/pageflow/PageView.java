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
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.interfaces.OnActivityLifeChangedListener;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 自动轮播视图，通过{@link #setAdapter(Adapter)}来绑定数据<br/>
 * 通过{@link #setOnItemClickListener(OnItemClickListener)}来绑定点击事件<br/>
 * <b>注：</b>在activity退出的时候我们需要停止轮播，在activity onResume的时候我们需要重启轮播，这里有两种方法<br/>
 * 1. 在activity退出的时候调用{@link #onDestroy()}来停止自动轮播，在activity onResume的时候调用{@link #onResume()}来重新启动轮播<br/>
 * 2. 或者直接在{@link OnActivityLifeChangedListener}类中完成，在调用{@link BaseActivity#addActivityLifeCycleListener(OnActivityLifeChangedListener)}
 */
public class PageView extends AdapterView<Adapter> {
    private final String TAG = "PageView";
    private final String METHOD = "method";

    private final boolean DEFAULT_CAN_LOOP = true;
    private final boolean DEFAULT_AUTO_PLAY = true;
    private final int NEXT_PAGE = 0x001;
    private final int DEFAULT_LOOP_COUNT = -1;
    private final int DEFAULT_BUFFER_SIZE = 2;
    private final int DEFAULT_PLAY_INTERVAL = 3000;
    private final int SNAP_VELOCITY = 1000;
    private final int INVALID_SCREEN = -1;
    private final int AVAILABLE_SCREEN = 0;

    /** 是否可以循环 */
    private boolean canLoop = DEFAULT_CAN_LOOP;
    /** 在可循环的情况下，设置循环次数，小于0，则无限循环 */
    private int loopCount = DEFAULT_LOOP_COUNT;
    /** 左右缓存的数量 */
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    /** 是否自动播放 */
    private boolean autoPlay = DEFAULT_AUTO_PLAY;
    /** 自动播放间隔 ms */
    private int playIntervalMs = DEFAULT_PLAY_INTERVAL;

    private LinkedList<View> mLoadedViews;
    private Adapter mAdapter;
    private Handler mHandler;
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private ViewGroup viewGroup;
    private Indicator mIndicator;
    private AdapterDataSetObserver mDataSetObserver;
    private OnViewSwitchListener onViewSwitchListener;
    private OnActivityLifeChangedListener onActivityLifeChangedListener;

    private int mCurrentViewIndex = 0;
    private int mRightMostItemIndex = 0;
    private int mLeftMostItemIndex = 0;
    private int mCurrentAdapterIndex = 0;
    /** 当前已循环过的次数 */
    private int mCurrentLoopCount = 0;
    private int mMaximumVelocity;
    private int mTouchSlop;
    private int mNextScreen = INVALID_SCREEN;

    /** 上次滚动的方向，< 0 向左，0 > 向右 */
    private int mLastScrollDirection = 0;
    /**
     * The current scroll state. One of
     * {@link AbsListView.OnScrollListener#SCROLL_STATE_TOUCH_SCROLL}
     * or {@link AbsListView.OnScrollListener#SCROLL_STATE_IDLE}
     * or {@link AbsListView.OnScrollListener#SCROLL_STATE_FLING}
     */
    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private float mLastMotionX;
    private long mDownTimeMillis;

    private Runnable nextRunnable = new Runnable() {
        @Override
        public void run() {
            boolean isLast = getCurrentAdapterIndex() == (getCount() - 1);
            //不允许轮播
            if (!canLoop && isLast) {
                return;
            }
            //运行轮播，但是已经达到了设置的轮播次数
            if (canLoop && (loopCount > 0 && mCurrentLoopCount >= loopCount) && isLast) {
                return;
            }

            mHandler.sendEmptyMessage(NEXT_PAGE);
        }
    };

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageView);
        initStyleable(a);
        init();
    }

    private void initStyleable(TypedArray a) {
        // getDimension
        // 获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘   返回float
        // getDimensionPixelOffset
        // 获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘  返回int
        // getDimensionPixelSize
        // 则不管写的是dp还是sp还是px,都会乘以denstiy.

        canLoop = a.getBoolean(R.styleable.PageView_pvCanLoop, DEFAULT_CAN_LOOP);
        loopCount = a.getInt(R.styleable.PageView_pvLoopCount, DEFAULT_LOOP_COUNT);
        autoPlay = a.getBoolean(R.styleable.PageView_pvAutoPlay, DEFAULT_AUTO_PLAY);
        playIntervalMs = a.getInt(R.styleable.PageView_pvPlayIntervalMs, DEFAULT_PLAY_INTERVAL);
        int bufferSize = a.getInt(R.styleable.PageView_pvBufferSize, DEFAULT_BUFFER_SIZE);
        setBufferSize(bufferSize);

        JLog.d(TAG, "canLoop:" + canLoop
                + ",\n loopCount:" + loopCount
                + ",\n bufferSize:" + bufferSize
                + ",\n autoPlay:" + autoPlay
                + ",\n playIntervalMs:" + playIntervalMs
        );
    }

    private void init() {
        mLoadedViews = new LinkedList<View>();
        mScroller = new Scroller(getContext());
        mDataSetObserver = new AdapterDataSetObserver();

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NEXT_PAGE:
                        if (autoPlay) {
                            if (!mScroller.isFinished()) {
                                mScroller.abortAnimation();
                            }
                            snapToScreen(mCurrentViewIndex + 1);
                            mHandler.postDelayed(nextRunnable, playIntervalMs);
                        }
                        break;

                    default:
                        break;
                }
            }
        };

        onActivityLifeChangedListener = new OnActivityLifeChangedListener() {
            @Override
            public void onCreate() {
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onResume() {
                PageView.this.onResume();
            }

            @Override
            public void onPause() {
            }

            @Override
            public void onStop() {
            }

            @Override
            public void onDestroy() {
                PageView.this.onDestroy();
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        JLog.d(METHOD, "onMeasure...");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY && !isInEditMode()) {
            throw new IllegalStateException("ViewFlow can only be used in EXACTLY mode.");
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY && !isInEditMode()) {
            throw new IllegalStateException("ViewFlow can only be used in EXACTLY mode.");
        }

        // The children are given the same width and height as the workspace
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        JLog.d(METHOD, "onLayout...");

        int childLeft = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }

        mScroller.startScroll(0, getScrollY(), mCurrentViewIndex * getWidth(), 0, 0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        JLog.d(METHOD, "onInterceptTouchEvent...");
        if (getChildCount() == 0) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = ev.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                int xDiff = (int) Math.abs(ev.getX() - mLastMotionX);
                // 移动的距离达到了触发事件的临界值
                if (xDiff > mTouchSlop) {
                    mLastMotionX = ev.getX();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityX = (int) velocityTracker.getXVelocity();
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                if (velocityX > SNAP_VELOCITY) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        JLog.d(METHOD, "onTouchEvent...");
        if (getChildCount() == 0) {
            return false;
        }
        if (mIndicator != null) {
            mIndicator.onPageViewTouchEvent(ev);
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                stopAutoPlayIfNeed();
                // Remember where the motion event started
                mLastMotionX = ev.getX();
                mDownTimeMillis = System.currentTimeMillis();
                return true;

            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastMotionX - ev.getX());
                int scrollX = getScrollX();

                if (Math.abs(deltaX) > mTouchSlop) {
                    mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
                }

                if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // 如果不允许轮播
                    if (!canLoop) {
                        if (deltaX > 0 && getCurrentAdapterIndex() == (getCount() - 1)) {//往右,并且已经是最后一个
                            JLog.d(TAG, "not allow repeat...");
                            return false;

                        } else if (deltaX < 0 && getCurrentAdapterIndex() == 0) {//往左,并且已经是第一个
                            JLog.d(TAG, "not allow repeat...");
                            return false;
                        }
                    }
                    //允许轮播，但是轮播次数已经达到设置值
                    if (canLoop && (loopCount > 0 && mCurrentLoopCount >= loopCount)) {
                        if (deltaX > 0 && getCurrentAdapterIndex() == (getCount() - 1)) {//往右,并且已经是最后一个
                            JLog.d(TAG, "allow repeat, but mCurrentLoopCount >= loopCount，mCurrentLoopCount:" + mCurrentLoopCount);
                            return false;

                        } else if (deltaX < 0 && getCurrentAdapterIndex() == 0) {//往左,并且已经是第一个
                            JLog.d(TAG, "allow repeat, but mCurrentLoopCount >= loopCount，mCurrentLoopCount:" + mCurrentLoopCount);
                            return false;
                        }
                    }

                    scrollBy(deltaX, 0);
                    mLastMotionX = ev.getX();
                    JLog.d(TAG, "onTouchEvent, move..., deltaX:" + deltaX + ", scrollX:" + scrollX);
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                startAutoPlayIfNeed();
                if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                        || (getScrollX() % getWidth() != 0)) {
                    mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

                    VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }

                    // 如果不允许轮播
                    if (!canLoop) {
                        if (velocityX < -SNAP_VELOCITY && getCurrentAdapterIndex() == (getCount() - 1)) {//往右,并且已经是最后一个
                            JLog.d(TAG, "not allow repeat...");
                            return false;

                        } else if (velocityX > SNAP_VELOCITY && getCurrentAdapterIndex() == 0) {//往左,并且已经是第一个
                            JLog.d(TAG, "not allow repeat...");
                            return false;
                        }
                    }
                    //允许轮播，但是轮播次数已经达到设置值
                    if (canLoop && (loopCount > 0 && mCurrentLoopCount >= loopCount)) {
                        if (velocityX < -SNAP_VELOCITY && getCurrentAdapterIndex() == (getCount() - 1)) {//往右,并且已经是最后一个
                            JLog.d(TAG, "allow repeat, but mCurrentLoopCount >= loopCount，mCurrentLoopCount:" + mCurrentLoopCount);
                            return false;

                        } else if (velocityX > SNAP_VELOCITY && getCurrentAdapterIndex() == 0) {//往左,并且已经是第一个
                            JLog.d(TAG, "allow repeat, but mCurrentLoopCount >= loopCount，mCurrentLoopCount:" + mCurrentLoopCount);
                            return false;
                        }
                    }

                    if (velocityX > SNAP_VELOCITY) {
                        // Fling hard enough to move left
                        snapToScreen(mCurrentViewIndex - 1);
                    } else if (velocityX < -SNAP_VELOCITY) {
                        // Fling hard enough to move right
                        snapToScreen(mCurrentViewIndex + 1);
                    } else {
                        snapToDestination();
                    }

                    JLog.d(TAG, "onTouchEvent, up..., velocityX:" + velocityX + ", scrollX:" + getScrollX());
                    return true;
                } else {
                    long timeInterval = System.currentTimeMillis() - mDownTimeMillis;
                    JLog.d("jason", "timeInterval:" + timeInterval);
                    if (5 < timeInterval && timeInterval < 200) {
                        View view = mLoadedViews.get(mCurrentViewIndex);
                        performItemClick(view, getCurrentAdapterIndex(), view.getId());
                    }
                }
                mDownTimeMillis = 0;
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void snapToDestination() {
        JLog.d(METHOD, "snapToDestination...");
        int screenWidth = getWidth();
        int scrollX = getScrollX();
        int whichScreen = (scrollX + (screenWidth / 2)) / screenWidth;

        snapToScreen(whichScreen);
    }

    private void snapToScreen(int whichScreen) {
        JLog.d(METHOD, "snapToScreen...");
        if (!mScroller.isFinished()) {
            return;
        }

        if (whichScreen > mCurrentViewIndex) {
            mLastScrollDirection = 1;
            mCurrentAdapterIndex++;
            //轮播一圈
            if (getCurrentAdapterIndex() == (getCount() - 1)
                    && mCurrentLoopCount < Integer.MAX_VALUE) {
                mCurrentLoopCount++;
                JLog.d(TAG, "loop once, mCurrentLoopCount:" + mCurrentLoopCount);
            }
        } else if (whichScreen < mCurrentViewIndex) {
            mLastScrollDirection = -1;
            mCurrentAdapterIndex--;
        } else {
            mLastScrollDirection = 0;
        }

        int tempIndex = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (mCurrentViewIndex == tempIndex) {
            mNextScreen = INVALID_SCREEN;
        } else {
            mNextScreen = AVAILABLE_SCREEN;
        }
        mCurrentViewIndex = tempIndex;

        int newX = mCurrentViewIndex * getWidth();
        int delta = newX - getScrollX();
        int duration = Math.abs(delta) * 2;
        mScroller.startScroll(getScrollX(), getScrollY(), delta, 0, duration);
        if (mIndicator != null) {
            if (mLastScrollDirection > 0) {
                mIndicator.scrollNextIndex(duration);
            } else if (mLastScrollDirection < 0) {
                mIndicator.scrollPreIndex(duration);
            } else {
                mIndicator.resetIndex(duration);
            }
            mIndicator.postInvalidate();
        }
        invalidate();
    }

    @Override
    public void computeScroll() {
        JLog.d(METHOD, "computeScroll...");
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();

        } else if (mNextScreen != INVALID_SCREEN) {
            mNextScreen = INVALID_SCREEN;
            postViewSwitched();

        } else {
        }
    }

    private void postViewSwitched() {
        JLog.d(METHOD, "postViewSwitched...");
        if (mLastScrollDirection == 0) {
            return;
        }

        if (mLastScrollDirection > 0) { // to the right
            View recycleView = null;
            // Remove view outside buffer range
            if (mCurrentViewIndex > bufferSize) {
                recycleView = mLoadedViews.removeFirst();
                detachViewFromParent(recycleView);
                mCurrentViewIndex--;
                mLeftMostItemIndex++;
                JLog.d(TAG, "detach left view, mCurrentViewIndex:" + mCurrentViewIndex + ", size:" + mLoadedViews.size());
            }
            // 如果右侧的缓存数目未达到bufferSize，则增加一个
            if (mLoadedViews.size() - 1 - mCurrentViewIndex < bufferSize) {
                mRightMostItemIndex++;
                // Add new view to buffer
                mLoadedViews.addLast(makeAndAddView(getRightMostItemIndex(), true, recycleView));
                JLog.d(TAG, "new right buffer view, mCurrentViewIndex:" + mCurrentViewIndex + ", size:" + mLoadedViews.size());
            }

        } else { // to the left
            View recycleView = null;
            // Remove view outside buffer range
            if (getCount() - 1 - mCurrentViewIndex > bufferSize) {
                recycleView = mLoadedViews.removeLast();
                detachViewFromParent(recycleView);
                mRightMostItemIndex--;
                JLog.d(TAG, "detach right view, mCurrentViewIndex:" + mCurrentViewIndex + ", size:" + mLoadedViews.size());
            }
            // 如果左侧侧的缓存数目为达到bufferSize，则增加一个
            if (mCurrentViewIndex < bufferSize) {
                mLeftMostItemIndex--;
                mCurrentViewIndex++;
                mLoadedViews.addFirst(makeAndAddView(getLeftMostItemIndex(), false, recycleView));
                JLog.d(TAG, "new left buffer view, mCurrentViewIndex:" + mCurrentViewIndex + ", size:" + mLoadedViews.size());
            }
        }

        mLastScrollDirection = 0;
        mNextScreen = INVALID_SCREEN;

        if (mIndicator != null) {
            mIndicator.onSwitched(mLoadedViews.get(mCurrentViewIndex), getCurrentAdapterIndex());
        }
        if (onViewSwitchListener != null) {
            onViewSwitchListener.onSwitched(mLoadedViews.get(mCurrentViewIndex), getCurrentAdapterIndex());
        }

        requestLayout();
        postInvalidate();
        printlnViewsId();
    }

    private void resetFocus(int adapterIndex) {
        JLog.d(METHOD, "resetFocus...");
        mCurrentViewIndex = 0;
        mRightMostItemIndex = 0;
        mLeftMostItemIndex = 0;
        mCurrentLoopCount = 0;
        mNextScreen = INVALID_SCREEN;
        mLastScrollDirection = 0;
        mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

        //确保index不越界
        adapterIndex = Math.max(adapterIndex, 0);
        adapterIndex = Math.min(adapterIndex, getCount() - 1);

        ArrayList<View> recycleViews = new ArrayList<View>();
        View recycleView;
        while (!mLoadedViews.isEmpty()) {
            recycleViews.add(recycleView = mLoadedViews.remove());
            detachViewFromParent(recycleView);
        }

        View currentView = makeAndAddView(adapterIndex, true, (recycleViews.isEmpty() ? null : recycleViews.remove(0)));
        mLoadedViews.addLast(currentView);

        for (int offset = 1; bufferSize - offset >= 0; offset++) {
            mLeftMostItemIndex = (adapterIndex - offset) % getCount();
            mRightMostItemIndex = (adapterIndex + offset) % getCount();
            mLoadedViews.addFirst(makeAndAddView(getLeftMostItemIndex(), false, (recycleViews.isEmpty() ? null : recycleViews.remove(0))));
            mLoadedViews.addLast(makeAndAddView(getRightMostItemIndex(), true, (recycleViews.isEmpty() ? null : recycleViews.remove(0))));
        }

        mCurrentViewIndex = mLoadedViews.indexOf(currentView);

        for (View view : recycleViews) {
            removeDetachedView(view, false);
        }

        requestLayout();
        stopAutoPlayIfNeed();
        startAutoPlayIfNeed();
        printlnViewsId();
    }

    private View makeAndAddView(int position, boolean addToEnd, View convertView) {
        JLog.d(METHOD, "makeAndAddView...");
        View child = mAdapter.getView(position, convertView, this);
        LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 0);
        }
        if (convertView != null) {
            attachViewToParent(child, (addToEnd ? -1 : 0), p);
        } else {
            addViewInLayout(child, (addToEnd ? -1 : 0), p, true);
        }
        return child;
    }

    private void startAutoPlayIfNeed() {
        stopAutoPlayIfNeed();
        if (autoPlay && mHandler != null) {
            mHandler.postDelayed(nextRunnable, playIntervalMs);
        }
    }

    private void stopAutoPlayIfNeed() {
        if (mHandler == null) {
            return;
        }
        mHandler.removeCallbacks(nextRunnable);
        mHandler.removeMessages(NEXT_PAGE);
    }

    /** 一般在Activity onResume中调用，重新启动轮播 */
    public void onResume() {
        startAutoPlayIfNeed();
    }

    /** 一般在Activity onDestroy，以便停止自动轮播 */
    public void onDestroy() {
        stopAutoPlayIfNeed();
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
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        this.mAdapter = adapter;
        if (mAdapter == null) {
            return;
        }
        if (getCount() < 2 && mIndicator != null) {
            mIndicator.setVisibility(View.INVISIBLE);
        } else if (mIndicator != null) {
            mIndicator.setVisibility(View.VISIBLE);
        }
        mAdapter.registerDataSetObserver(mDataSetObserver);
        setSelection(position);
    }

    @Override
    public View getSelectedView() {
        if (mLoadedViews == null || mLoadedViews.size() < 1) {
            return null;
        }
        return mLoadedViews.get(mCurrentViewIndex);
    }

    @Override
    public void setSelection(int position) {
        mScroller.forceFinished(true);
        if (mAdapter == null) {
            return;
        }
        resetFocus(position);
        if (mIndicator != null) {
            mIndicator.onSwitched(mLoadedViews.get(mCurrentViewIndex), position);
        }
        if (onViewSwitchListener != null) {
            onViewSwitchListener.onSwitched(mLoadedViews.get(mCurrentViewIndex), position);
        }
    }

    private int getLeftMostItemIndex() {
        int index = mLeftMostItemIndex % getCount();
        if (index < 0) {
            index = getCount() + index;
        }
        mLeftMostItemIndex = index;
        return index;
    }

    private int getRightMostItemIndex() {
        int index = mRightMostItemIndex % getCount();
        if (index < 0) {
            index = getCount() + index;
        }
        mRightMostItemIndex = index;
        return index;
    }

    // 用于测试，需要adapter配合，将view的Tag设置为position
    private void printlnViewsId() {
//        String ids = "";
//        for (int i = 0; i < mLoadedViews.size(); i++) {
//            View view = mLoadedViews.get(i);
//            ids = ids + ", " + String.valueOf(view.getTag());
//        }
//        JLog.d(TAG, "view ids:" + ids + ", mCurrentViewIndex:" + mCurrentViewIndex
//                + ", mLeftMostItemIndex:" + getLeftMostItemIndex()
//                + ", mRightMostItemIndex:" + getRightMostItemIndex()
//                + ", mCurrentAdapterIndex:" + getCurrentAdapterIndex()
//                + ", scrollX:" + getScrollX());
    }

    public int getCurrentAdapterIndex() {
        int index = mCurrentAdapterIndex % getCount();
        if (index < 0) {
            index = getCount() + index;
        }
        mCurrentAdapterIndex = index;
        return index;
    }

    @Override
    public int getCount() {
        return getAdapter() == null ? 0 : getAdapter().getCount();
    }

    public int getViewsCount() {
        return mLoadedViews == null ? 0 : mLoadedViews.size();
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    public void setViewGroup(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    public boolean isCanLoop() {
        return canLoop;
    }

    /**
     * 设置是否可以轮播
     *
     * @param canLoop boolean
     */
    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }

    public int getLoopCount() {
        return loopCount;
    }

    /**
     * 设置可轮播次数，小于0为无限轮播，该项只有在{@link #canLoop}为true时才生效
     *
     * @param loopCount
     */
    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * 设置pageView左右缓存数目
     *
     * @param bufferSize int
     */
    public void setBufferSize(int bufferSize) {
        if (bufferSize < 0) {
            throw new IllegalArgumentException("bufferSize should be greater than 0.");
        }
        if (bufferSize < 1) {
            bufferSize = 1;
        }
        this.bufferSize = bufferSize;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    /** 设置是否自动播放，默认为true */
    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public int getPlayIntervalMs() {
        return playIntervalMs;
    }

    /** 设置自动播放时间间隔，建议设置3秒以上，单位：毫秒 */
    public void setPlayIntervalMs(int playIntervalMs) {
        this.playIntervalMs = playIntervalMs;
    }

    public void setIndicator(Indicator indicator) {
        if (indicator != null) {
            indicator.setPageView(this);
        }
        this.mIndicator = indicator;
    }

    public int getScrollState() {
        return mScrollState;
    }

    public OnViewSwitchListener getOnViewSwitchListener() {
        return onViewSwitchListener;
    }

    public void setOnViewSwitchListener(OnViewSwitchListener l) {
        this.onViewSwitchListener = l;
    }

    public OnActivityLifeChangedListener getOnActivityLifeChangedListener() {
        return onActivityLifeChangedListener;
    }

    //    @Override
//    public void setOnItemClickListener(OnItemClickListener l) {
//        this.onItemClickListener = l;
//    }

    class AdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            int index = (mLeftMostItemIndex + bufferSize) % getCount();
            if (index < 0) {
                index = getCount() + index;
            }
            index = Math.max(index, 0);
            index = Math.min(index, getCount() - 1);
            resetFocus(index);
        }

        @Override
        public void onInvalidated() {

        }
    }

    public interface OnViewSwitchListener {

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