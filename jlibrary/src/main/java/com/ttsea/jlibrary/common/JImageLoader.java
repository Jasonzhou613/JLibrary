package com.ttsea.jlibrary.common;

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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ttsea.jlibrary.R;

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

    public static JImageLoader getInstance() {
        if (loader == null) {
            loader = new JImageLoader();
        }
        return loader;
    }

    public void displayImage(Context context, String path, ImageView imageView) {
        // com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(path, imageView);
        displayImage(context,
                path,
                imageView,
                1f,
                R.color.whiteSmoke,
                R.drawable.photo_loading_error,
                false,
                DiskCacheStrategy.ALL,
                Priority.NORMAL,
                null
        );
    }

    public void displayImageAsBitmap(Context context, String path, ImageView imageView) {
        // com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(path, imageView);
        displayImageAsBitmap(context,
                path,
                imageView,
                1f,
                R.color.whiteSmoke,
                R.drawable.photo_loading_error,
                false,
                DiskCacheStrategy.ALL,
                Priority.NORMAL,
                null
        );
    }

    public void displayImage(Context context, String path, ImageView imageView, float thumbnail,
                             int placeholderResId, int errorResId, boolean skipMemoryCache, DiskCacheStrategy strategy,
                             Priority priority, RequestListener<String, GlideDrawable> listener) {
        Glide.with(context)
                .load(path)
                //.load(new String[]{})//显示数组，适用于先显示缩略图，再显示全图
                //.asBitmap()
                .thumbnail(thumbnail)//一开始大小
                //.fitCenter()
                .centerCrop()
                .placeholder(placeholderResId)//默认显示
                .error(errorResId)//错误显示
                .crossFade()//淡入淡出效果
                //.dontAnimate()//无淡入淡出效果
                //.override(200, 150)//重新设置宽和高
                .skipMemoryCache(skipMemoryCache)//是否跳过内存缓存
                .diskCacheStrategy(strategy)//本地磁盘缓存规则
                .priority(priority)//显示优先级
                //.animate(R.anim.jglide_animate)//动画
                .listener(listener)
                //.transform( new RotateTransformation( context, 45f ))
                .into(imageView);
    }

    public void displayImageAsBitmap(Context context, String path, ImageView imageView, float thumbnail,
                                     int placeholderResId, int errorResId, boolean skipMemoryCache, DiskCacheStrategy strategy,
                                     Priority priority, RequestListener<String, Bitmap> listener) {
        Glide.with(context)
                .load(path)
                //.load(new String[]{})//显示数组，适用于先显示缩略图，再显示全图
                .asBitmap()
                .thumbnail(thumbnail)//一开始大小
                //.fitCenter()
                .centerCrop()
                .placeholder(placeholderResId)//默认显示
                .error(errorResId)//错误显示
                //.crossFade()//淡入淡出效果
                //.dontAnimate()//无淡入淡出效果
                //.override(200, 150)//重新设置宽和高
                .skipMemoryCache(skipMemoryCache)//是否跳过内存缓存
                .diskCacheStrategy(strategy)//本地磁盘缓存规则
                .priority(priority)//显示优先级
                .animate(R.anim.jglide_animate)//动画
                .listener(listener)
                //.transform( new RotateTransformation( context, 45f ))
                .into(imageView);
    }

    /** 适用图片浏览 */
    public void displayImageForGallery(final Context context, final String path, final ImageView imageView, final ImageLoadingListener listener) {
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .cacheInMemory(true).cacheOnDisc(true)
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .showImageOnLoading(R.color.transparent)
//                .showImageForEmptyUri(R.drawable.photo_loading_error)
//                .showImageOnFail(R.drawable.photo_loading_error)
//                .build();
//
//        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(path, imageView, options,
//                new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String s, View view) {
//                        if (listener != null) {
//                            listener.onLoadingStarted(s, view);
//                        }
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String s, View view, FailReason failReason) {
//                        if (listener != null) {
//                            String msg = "";
//                            if (failReason != null && failReason.getCause() != null) {
//                                msg = failReason.getCause().getMessage();
//                            }
//                            listener.onLoadingFailed(s, view, msg);
//                        }
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                        if (listener != null) {
//                            listener.onLoadingComplete(s, view, new BitmapDrawable(context.getResources(), bitmap));
//                        }
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String s, View view) {
//                        if (listener != null) {
//                            listener.onLoadingCancelled(s, view);
//                        }
//                    }
//                });

        RequestListener<String, GlideDrawable> RequestListener = new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
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
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (listener != null) {
                    listener.onLoadingComplete(model, imageView, resource.getCurrent());
                }
                return false;
            }
        };

        if (listener != null) {
            listener.onLoadingStarted(path, imageView);
        }

        Glide.with(context)
                .load(path)
                //.asBitmap()
                .thumbnail(0.2f)
                .listener(RequestListener)
                //.placeholder(R.color.red)
                .error(R.drawable.photo_loading_error)
                .into(imageView);
    }

    public void pause(Context context) {
        JLog.d(TAG, "JImageLoader pause...");
        ImageLoader.getInstance().pause();
        Glide.with(context).pauseRequests();
    }

    public void resume(Context context) {
        JLog.d(TAG, "JImageLoader resume...");
        ImageLoader.getInstance().resume();
        Glide.with(context).resumeRequests();
    }

    public void destroy(Context context) {
        JLog.d(TAG, "JImageLoader destroy...");
        ImageLoader.getInstance().destroy();
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
