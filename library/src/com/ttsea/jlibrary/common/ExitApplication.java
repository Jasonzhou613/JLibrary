package com.ttsea.jlibrary.common;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/**
 * 管理Activity，方便程序彻底退出<br/>
 * 将Activity添加到堆栈中,然后再调用它的exitApplication()方法就可以让程序彻底退出。<br/>
 * 使用方法：<br/>
 * 1.在Activity的onCreate方法里调用ExitApplication.getInstance().addActivity(Activity)
 * 方法<br/>
 * 2.在要退出程序的时候调用：ExitApplication.getInstance().exitApplication()即可
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2013.10.20 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016.02.17
 */
public class ExitApplication extends Application {
    private final String TAG = "Common.ExitApplication";
    private static List<Activity> mActivityList = new LinkedList<Activity>();
    private static ExitApplication instance;

    /**
     * 单例模式中获取唯一的ExitApplication实例
     */
    public static ExitApplication getInstance() {
        if (null == instance) {
            instance = new ExitApplication();
        }
        return instance;
    }

    /** 添加Activity到ActivityList中 */
    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    /** 遍历ActivityList中所有的Activity并finish，退出整个程序 */
    public void exitApplication() {
        JLog.d(TAG, "Application has exited.");
        for (Activity activity : mActivityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
