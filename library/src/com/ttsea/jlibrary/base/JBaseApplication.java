package com.ttsea.jlibrary.base;

import android.app.Activity;
import android.app.Application;

import com.ttsea.jlibrary.common.JLog;

import java.util.LinkedList;
import java.util.List;

/**
 * 管理Activity，方便程序彻底退出<br/>
 * 将Activity添加到堆栈中,然后再调用它的exitApplication()方法就可以让程序彻底退出。<br/>
 * 使用方法：<br/>
 * 1.在Activity的onCreate方法里调用JBaseApplication.addActivity(Activity)
 * 方法<br/>
 * 2.在Activity的onDestroy方法里调用JBaseApplication.removeActivity(Activity)
 * 方法<br/>
 * 3.在要退出程序的时候调用：JBaseApplication.exitApplication()即可
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2013.10.20 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016.02.17
 */
public class JBaseApplication extends Application {
    private static final String TAG = "Base.JBaseApplication";
    private List<Activity> mActivityList = new LinkedList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /** 添加Activity到ActivityList中 */
    public static void addActivity(Activity activity) {
        if (activity != null && activity.getApplication() instanceof JBaseApplication) {
            List<Activity> activities = ((JBaseApplication) activity.getApplication()).mActivityList;
            if (!activities.contains(activity)) {
                activities.add(activity);
            }
            JLog.d(TAG, "size:" + activities.size());
        }
    }

    /** 从ActivityList中移除Activity */
    public static void removeActivity(Activity activity) {
        if (activity != null && activity.getApplication() instanceof JBaseApplication) {
            List<Activity> activities = ((JBaseApplication) activity.getApplication()).mActivityList;
            activities.remove(activity);
            JLog.d(TAG, "size:" + activities.size());
        }
    }

    /** 遍历ActivityList中所有的Activity并finish，退出整个程序 */
    public static void exitApplication(Activity activity) {
        if (activity != null && activity.getApplication() instanceof JBaseApplication) {
            List<Activity> activities = ((JBaseApplication) activity.getApplication()).mActivityList;
            for (Activity a : activities) {
                a.finish();
            }
            JLog.d(TAG, "Application exit...");
            System.exit(0);
        }
    }
}
