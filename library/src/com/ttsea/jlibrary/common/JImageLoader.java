package com.ttsea.jlibrary.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ttsea.jlibrary.R;

/**
 * 图片加载器，该工程中所有的图片都将同过这个类进行加载 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/7/19 10:27 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/7/19 10:27
 */
public class JImageLoader {
    private final String TAG = "Common.JImageLoader";

    private static JImageLoader loader;

    private JImageLoader() {
    }

    public static JImageLoader getInstance() {
        if (loader == null) {
            loader = new JImageLoader();
        }
        return loader;
    }

    public void displayImage(Context context, String path, ImageView imageView) {
        // com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(path, imageView);
        Glide.with(context)
                .load(path)
                .asBitmap()
                .thumbnail(0.2f)
                .centerCrop()
                .placeholder(R.color.whiteSmoke)
                .error(R.drawable.photo_loading_error)
                .into(imageView);
    }


    /** 适用图片浏览 */
    public void displayImageForGallery(Context context, String path, ImageView imageView, final ImageLoadingListener listener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.color.transparent)
                .showImageForEmptyUri(R.drawable.photo_loading_error)
                .showImageOnFail(R.drawable.photo_loading_error)
                .build();

        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(path, imageView, options,
                new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        if (listener != null) {
                            listener.onLoadingStarted(s, view);
                        }
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        if (listener != null) {
                            String msg = "";
                            if (failReason != null && failReason.getCause() != null) {
                                msg = failReason.getCause().getMessage();
                            }
                            listener.onLoadingFailed(s, view, msg);
                        }
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        if (listener != null) {
                            listener.onLoadingComplete(s, view, bitmap);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        if (listener != null) {
                            listener.onLoadingCancelled(s, view);
                        }
                    }
                });

//        Glide.with(context)
//                .load(path)
//                //.asBitmap()
//                .thumbnail(0.2f)
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
//                        if (listener != null) {
//                            listener.onLoadingFailed(s, target., e.getMessage());
//                        }
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
//                        if (listener != null) {
//                            listener.onLoadingComplete(s, target, null);
//                        }
//                        return false;
//                    }
//                })
//                .centerCrop()
//                .placeholder(R.color.whiteSmoke)
//                .error(R.drawable.photo_loading_error)
//                .into(imageView);
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

        void onLoadingComplete(String s, View view, Bitmap bitmap);

        void onLoadingCancelled(String s, View view);
    }

    public interface ImageLoadingProgressListener {
        void onProgressUpdate(String var1, View var2, int var3, int var4);
    }
}
