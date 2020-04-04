package com.ttsea.jlibrary.common.imageloader;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.ttsea.jlibrary.debug.JLog;

/**
 * 自定义Glide缓存大小和目录 <br>
 * 在AndroidManifest.xml中配置meta-data：如下 <br>
 * android:name="com.hampoo.baselibrary.common.CustomCachingGlideModule"
 * android:value="GlideModule"
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class CustomCachingGlideModule implements GlideModule {
    private final String TAG = "CustomCachingGlideModule";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        int cacheSizeBytes = 20971520;// 20M

        // builder.setDiskCache(
        // new InternalCacheDiskCacheFactory(context, cacheSize100MegaBytes));

        // builder.setDiskCache(
        // new ExternalCacheDiskCacheFactory(context, cacheSize100MegaBytes));
        String cacheDir = context.getCacheDir().getAbsolutePath();
        builder.setDiskCache(new DiskLruCacheFactory(cacheDir, "glideCache",
                cacheSizeBytes));
        JLog.d(TAG, "cacheDir:" + cacheDir);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // nothing to do here
    }
}
