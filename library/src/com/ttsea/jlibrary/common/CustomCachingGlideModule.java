package com.ttsea.jlibrary.common;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.ttsea.jlibrary.utils.CacheDirUtils;

/**
 * 自定义Glide缓存大小和目录 <br/>
 * 在AndroidManifest.xml中配置meta-data：如下<br/>
 * android:name="com.ttsea.jlibrary.common.CustomCachingGlideModule"
 * android:value="GlideModule"
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/12/14 10:41 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/12/14 10:41.
 */
public class CustomCachingGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        int cacheSizeBytes = 20971520;//20M

//            builder.setDiskCache(
//            new InternalCacheDiskCacheFactory(context, cacheSize100MegaBytes));

//            builder.setDiskCache(
//            new ExternalCacheDiskCacheFactory(context, cacheSize100MegaBytes));
        builder.setDiskCache(new DiskLruCacheFactory(CacheDirUtils.getCacheDir(context), "glideCache", cacheSizeBytes));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // nothing to do here
    }
}
