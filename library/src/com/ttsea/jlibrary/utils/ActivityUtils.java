package com.ttsea.jlibrary.utils;

import android.app.Activity;

import com.ttsea.jlibrary.R;


public class ActivityUtils {
    /**
     * 从右到左切入Activity
     *
     * @param activity
     */
    public static void fadeInfromRightToLeft(Activity activity) {
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        activity.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_out_from_left);
    }

    /**
     * 从左到右切出Activity
     *
     * @param activity
     */
    public static void fadeInfromLeftToRight(Activity activity) {
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        activity.overridePendingTransition(R.anim.activity_in_from_left,
                R.anim.activity_out_from_right);
    }
}
