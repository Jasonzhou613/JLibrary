package com.ttsea.jlibrary.common;

import android.os.Handler;
import android.os.Message;

/**
 * 计时器 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class MyTimer {
    private final String TAG = "MyTimer";

    private final static long DEFAULT_FREQUENCY_MILLIS = 1 * 1000;
    private final static long DEFAULT_TIME_MILLIS = 10 * 1000;
    private final int MESSAGE_TIMER_START = 0x002;
    private final int MESSAGE_TIMER_RUNNING = 0x003;

    /** 每次更新的频率，默认为1秒 */
    private long frequencyMillis = DEFAULT_FREQUENCY_MILLIS;
    /** 总共计时时间，毫秒 */
    private long endTimeMillis = DEFAULT_TIME_MILLIS;

    private boolean isStoped = true;

    private Handler mHander;
    private OnTimerListener onTimerListener;

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
        checkParam();
    }

    public long getFrequencyMillis() {
        return frequencyMillis;
    }

    public void setFrequencyMillis(long frequencyMillis) {
        this.frequencyMillis = frequencyMillis;
        checkParam();
    }

    public void setOnTimerListener(OnTimerListener l) {
        this.onTimerListener = l;
    }

    public MyTimer() {
        this(DEFAULT_TIME_MILLIS);
    }

    public MyTimer(long endTimeMillis) {
        this(endTimeMillis, DEFAULT_FREQUENCY_MILLIS);
    }

    public MyTimer(long endTimeMillis, long frequencyMillis) {
        this.endTimeMillis = endTimeMillis;
        this.frequencyMillis = frequencyMillis;
        init();
    }

    private void init() {
        checkParam();
        mHander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_TIMER_START:
                        if (isStoped) {
                            return;
                        }
                        if (onTimerListener != null) {
                            onTimerListener.onTimer(endTimeMillis);
                        }
                        sendLocalMessage(MESSAGE_TIMER_RUNNING, frequencyMillis);
                        break;

                    case MESSAGE_TIMER_RUNNING:
                        if (isStoped) {
                            return;
                        }
                        endTimeMillis = endTimeMillis - frequencyMillis;
                        if (endTimeMillis > 0) {
                            if (onTimerListener != null) {
                                onTimerListener.onTimer(endTimeMillis);
                            }
                            if (endTimeMillis > frequencyMillis) {
                                sendLocalMessage(MESSAGE_TIMER_RUNNING, frequencyMillis);
                            } else {
                                sendLocalMessage(MESSAGE_TIMER_RUNNING, endTimeMillis);
                            }
                        } else {
                            isStoped = true;
                            if (onTimerListener != null) {
                                onTimerListener.onTimerEnd();
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }

    /** 开始计时 */
    public void start() {
        JLog.d(TAG, "Timer started");
        isStoped = false;
        sendLocalMessage(MESSAGE_TIMER_START, 0);
    }

    /** 停止计时 */
    public void stop() {
        if (isStoped) {
            JLog.d(TAG, "Timer has already stoped");
            return;
        }
        isStoped = true;
        JLog.d(TAG, "Timer stoped");
        if (onTimerListener != null) {
            onTimerListener.onTimerEnd();
        }
    }

    private void sendLocalMessage(final int what, long delayMillis) {
        mHander.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isStoped) {
                    return;
                }
                mHander.sendEmptyMessage(what);
            }
        }, delayMillis);
    }

    private void checkParam() {
        if (endTimeMillis <= 0) {
            throw new IllegalArgumentException("endTimeMillis must greater than 0");
        }

        if (frequencyMillis <= 0) {
            throw new IllegalArgumentException("frequencyMillis must greater than 0");
        }
    }

    public interface OnTimerListener {
        /** 计时中，residualTimeMills剩余时间 */
        void onTimer(long residualTimeMillis);

        /** 计时结束 */
        void onTimerEnd();
    }
}
