package com.ttsea.jlibrary.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.ttsea.jlibrary.common.imageloader.JImageLoader;
import com.ttsea.jlibrary.debug.GlobalCrashHandler;
import com.ttsea.jlibrary.debug.JLog;

import java.util.LinkedList;
import java.util.List;

/**
 * 管理Activity，方便程序彻底退出<br>
 * 将Activity添加到堆栈中,然后再调用它的exitApplication()方法就可以让程序彻底退出。<br>
 * 使用方法：<br>
 * 1.在Activity的onCreate方法里调用JBaseApplication.addActivity(Activity)方法<br>
 * 2.在Activity的onDestroy方法里调用JBaseApplication.removeActivity(Activity)方法<br>
 * 3.在要退出程序的时候调用：JBaseApplication.exitApplication()即可
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
class JBaseApplication extends Application {
    private static final String TAG = "Base.Application";
    private List<Activity> mActivityList = new LinkedList<Activity>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 解决AsyncTask.onPostExecute不执行问题, start
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 解决AsyncTask.onPostExecute不执行问题, end

        // 异常捕捉
        GlobalCrashHandler.init(this);
        //初始化图片加载器
        JImageLoader.init(this);
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
        if (activity != null) {
            exitApplication(activity.getApplication());
        }
    }

    /** 遍历ActivityList中所有的Activity并finish，退出整个程序 */
    public static void exitApplication(Application application) {
        if (application instanceof JBaseApplication) {
            List<Activity> activities = ((JBaseApplication) application).mActivityList;
            for (Activity a : activities) {
                a.finish();
            }
            JLog.d(TAG, "Application exit...");
            System.exit(0);
        }
    }
}
