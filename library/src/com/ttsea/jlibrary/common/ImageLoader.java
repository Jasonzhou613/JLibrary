package com.ttsea.jlibrary.common;

import android.content.Context;
import android.widget.ImageView;

/**
 * 图片加载器，该工程中所有的图片都将同过这个类进行加载 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/7/19 10:27 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/7/19 10:27
 */
public class ImageLoader {
    private final String TAG = "ImageLoader";

    private static ImageLoader loader;

    private ImageLoader() {
    }

    public static ImageLoader getInstance() {
        if (loader == null) {
            loader = new ImageLoader();
        }
        return loader;
    }

    public void displayImage(Context context, String path, ImageView imageView) {
        JLog.d(TAG, "path:" + path);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(path, imageView);
    }
}
