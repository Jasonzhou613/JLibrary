package com.ttsea.jlibrary.common.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.utils.CacheDirUtils;
import com.ttsea.jlibrary.debug.JLog;

import java.io.File;

/**
 * 图片加载器，该工程中所有的图片都将同过这个类进行加载 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class JImageLoader {
    private final String TAG = "Common.JImageLoader";

    private static JImageLoader loader;

    protected JImageLoader() {
    }

    /** 初始化ImageLoader，只需要在Application中的onCreate方法中调用即可 */
    public static void init(Context context) {
        initImageLoader(context.getApplicationContext());
    }

    /** 初始化ImageLoader */
    private static void initImageLoader(Context appContext) {
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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(appContext)
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

    public static JImageLoader getInstance() {
        if (loader == null) {
            loader = new JImageLoader();
        }
        return loader;
    }

    public void displayImage(Context context, String path, ImageView imageView) {
        displayImage(context, path, imageView, 1f, R.color.whiteSmoke,
                R.drawable.photo_loading_error, false, DiskCacheStrategy.ALL,
                Priority.NORMAL, null);
    }

    public void displayImageAsBitmap(Context context, String path,
                                     ImageView imageView) {
        displayImageAsBitmap(context, path, imageView, 1f, R.color.whiteSmoke,
                R.drawable.photo_loading_error, false, DiskCacheStrategy.ALL,
                Priority.NORMAL, null);
    }

    public void displayImage(Context context, String path, ImageView imageView,
                             float thumbnail, int placeholderResId, int errorResId,
                             boolean skipMemoryCache, DiskCacheStrategy strategy,
                             Priority priority, RequestListener<String, GlideDrawable> listener) {
        Glide.with(context).load(path)
                // .load(new String[]{})//显示数组，适用于先显示缩略图，再显示全图
                // .asBitmap()
                .thumbnail(thumbnail)// 一开始大小
                .fitCenter()
                // .centerCrop()
                .placeholder(placeholderResId)// 默认显示
                .error(errorResId)// 错误显示
                .crossFade()// 淡入淡出效果
                // .dontAnimate()//无淡入淡出效果
                // .override(200, 150)//重新设置宽和高
                .skipMemoryCache(skipMemoryCache)// 是否跳过内存缓存
                .diskCacheStrategy(strategy)// 本地磁盘缓存规则
                .priority(priority)// 显示优先级
                .animate(R.anim.jglide_animate)// 动画
                .listener(listener)
                // .transform( new RotateTransformation( context, 45f ))
                .into(imageView);
    }

    public void displayImageAsBitmap(Context context, String path,
                                     ImageView imageView, float thumbnail, int placeholderResId,
                                     int errorResId, boolean skipMemoryCache,
                                     DiskCacheStrategy strategy, Priority priority,
                                     RequestListener<String, Bitmap> listener) {
        Glide.with(context).load(path)
                // .load(new String[]{})//显示数组，适用于先显示缩略图，再显示全图
                .asBitmap().thumbnail(thumbnail)// 一开始大小
                // .fitCenter()
                .centerCrop().placeholder(placeholderResId)// 默认显示
                .error(errorResId)// 错误显示
                // .crossFade()//淡入淡出效果
                // .dontAnimate()//无淡入淡出效果
                // .override(200, 150)//重新设置宽和高
                .skipMemoryCache(skipMemoryCache)// 是否跳过内存缓存
                .diskCacheStrategy(strategy)// 本地磁盘缓存规则
                .priority(priority)// 显示优先级
                .animate(R.anim.jglide_animate)// 动画
                .listener(listener)
                // .transform( new RotateTransformation( context, 45f ))
                .into(imageView);
    }

    /** 适用图片浏览 */
    public void displayImageForGallery(final Context context,
                                       final String path, final ImageView imageView,
                                       final ImageLoadingListener listener) {
        RequestListener<String, GlideDrawable> RequestListener = new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model,
                                       Target<GlideDrawable> target, boolean isFirstResource) {
                if (listener != null) {
                    String errorMsg = "";
                    if (e != null) {
                        errorMsg = e.toString();
                    }
                    listener.onLoadingFailed(model, imageView, errorMsg);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource,
                                           String model, Target<GlideDrawable> target,
                                           boolean isFromMemoryCache, boolean isFirstResource) {
                if (listener != null) {
                    listener.onLoadingComplete(model, imageView,
                            resource.getCurrent());
                }
                return false;
            }
        };

        if (listener != null) {
            listener.onLoadingStarted(path, imageView);
        }

        Glide.with(context).load(path)
                // .asBitmap()
                .thumbnail(0.2f).listener(RequestListener)
                // .placeholder(R.color.red)
                .error(R.drawable.photo_loading_error).into(imageView);
    }

    public void pause(Context context) {
        JLog.d(TAG, "HPImageLoader pause...");
        // JImageLoader.getInstance().pause();
        Glide.with(context).pauseRequests();
    }

    public void resume(Context context) {
        JLog.d(TAG, "HPImageLoader resume...");
        // JImageLoader.getInstance().resume();
        Glide.with(context).resumeRequests();
    }

    public void destroy(Context context) {
        JLog.d(TAG, "HPImageLoader destroy...");
        // JImageLoader.getInstance().destroy();
        Glide.with(context).onDestroy();
    }

    public interface ImageLoadingListener {
        void onLoadingStarted(String s, View view);

        void onLoadingFailed(String s, View view, String failReason);

        void onLoadingComplete(String s, View view, Drawable drawable);

        void onLoadingCancelled(String s, View view);
    }

    public interface ImageLoadingProgressListener {
        void onProgressUpdate(String var1, View var2, int var3, int var4);
    }
}
