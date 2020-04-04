package com.ttsea.jlibrary.sample.base;

import android.app.Activity;
import android.app.Application;

import com.ttsea.jlibrary.JLibrary;
import com.ttsea.jlibrary.debug.Config;
import com.ttsea.jlibrary.debug.JLog;

import java.util.LinkedList;
import java.util.List;

/**
 * // to do <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 11:11 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";
    private List<Activity> mActivityList = new LinkedList<Activity>();

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

        initGlobalConfig();
    }

    /** 初始化全局变量 */
    private void initGlobalConfig() {
        JLibrary.init(getApplicationContext());
        JLibrary.debugMode(Config.DEBUG);
    }


    /**
     * 添加Activity到ActivityList中
     */
    public static void addActivity(Activity activity) {
        if (activity != null && activity.getApplication() instanceof BaseApplication) {
            List<Activity> activities = ((BaseApplication) activity.getApplication()).mActivityList;
            if (!activities.contains(activity)) {
                activities.add(activity);
            }
            JLog.d(TAG, "size:" + activities.size());
        }
    }

    /**
     * 从ActivityList中移除Activity
     */
    public static void removeActivity(Activity activity) {
        if (activity != null && activity.getApplication() instanceof BaseApplication) {
            List<Activity> activities = ((BaseApplication) activity.getApplication()).mActivityList;
            activities.remove(activity);
            JLog.d(TAG, "size:" + activities.size());
        }
    }

    /**
     * 遍历ActivityList中所有的Activity并finish，退出整个程序
     */
    public static void exitApplication(Activity activity) {
        if (activity != null) {
            exitApplication(activity.getApplication());
        }
    }

    /**
     * 遍历ActivityList中所有的Activity并finish，退出整个程序
     */
    public static void exitApplication(Application application) {
        if (application instanceof BaseApplication) {
            List<Activity> activities = ((BaseApplication) application).mActivityList;
            for (Activity a : activities) {
                a.finish();
            }
            JLog.d(TAG, "Application exit...");
            System.exit(0);
        }
    }
}