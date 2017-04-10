package com.ttsea.jlibrary.utils;

import android.app.Activity;

import com.ttsea.jlibrary.R;

public class ActivityUtils {

    /**
     * 从上到下切入Activity
     */
    public static void fadeInFromTop(Activity activity) {
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        activity.overridePendingTransition(R.anim.activity_in_from_top, R.anim.activity_out_to_bottom);
    }

    /**
     * 从下到上消失Activity
     */
    public static void fadeOutToTop(Activity activity) {
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        activity.overridePendingTransition(0, R.anim.activity_out_to_top);
    }

    /**
     * 从下到上切入Activity
     */
    public static void fadeInFromBottom(Activity activity) {
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        activity.overridePendingTransition(R.anim.activity_in_from_bottom,
                R.anim.activity_out_to_top);
    }

    /**
     * 从上到下消失Activity
     */
    public static void fadeOutToBottom(Activity activity) {
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        activity.overridePendingTransition(0, R.anim.activity_out_to_bottom);
    }

    /**
     * 从左到右切入Activity
     */
    public static void fadeInFromLeft(Activity activity) {
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        activity.overridePendingTransition(R.anim.activity_in_from_left,
                R.anim.activity_out_to_right);
    }

    /**
     * 从右到左边切出Activity
     */
    public static void fadeOutToLeft(Activity activity) {
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        activity.overridePendingTransition(0, R.anim.activity_out_to_left);
    }

    /**
     * 从右到左切入Activity
     */
    public static void fadeInFromRight(Activity activity) {
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        activity.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_out_to_left);
    }

    /**
     * 从左到右切出Activity
     */
    public static void fadeOutToRight(Activity activity) {
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        activity.overridePendingTransition(0, R.anim.activity_out_to_right);
    }
}
