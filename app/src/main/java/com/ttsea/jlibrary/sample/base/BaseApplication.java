package com.ttsea.jlibrary.sample.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ttsea.jlibrary.JLibrary;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.sample.R;
import com.ttsea.jlibrary.sample.config.Config;
import com.ttsea.jlibrary.utils.CacheDirUtils;

import java.io.File;
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
        initImageLoader(this);
    }

    /** 初始化全局变量 */
    private void initGlobalConfig() {
        JLibrary.init(getApplicationContext());
        JLibrary.debugMode(Config.DEBUG);
    }

    /** 初始化ImageLoader */
    private void initImageLoader(Context appContext) {
        File cacheDir = new File(CacheDirUtils.getImageCacheDir(appContext));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，图片太多就这这个。还有其他设置
                // 如Bitmap.Config.ARGB_8888
                .showImageOnLoading(R.color.gainsboro) // 默认图片
                .showImageForEmptyUri(R.color.gainsboro) // url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.color.gainsboro)// 加载失败显示的图片
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800)
                // 缓存在内存的图片的宽和高度
                //.discCacheExtraOptions(480, 800, Bitmap.CompressFormat.PNG, 70, null)
                // CompressFormat.PNG类型，70质量（0-100）
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(2 * 1024 * 1024) // 缓存到内存的最大数据
                .discCacheSize(50 * 1024 * 1024)// 缓存到文件的最大数据
                .discCacheFileCount(1000) // 文件数量
                .discCache(new UnlimitedDiskCache(cacheDir))// 自定义缓存路径
                .defaultDisplayImageOptions(options)// 上面的options对象，一些属性配置
                .build();

        ImageLoader.getInstance().init(config); // 初始化
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