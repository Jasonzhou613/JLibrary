package com.ttsea.jlibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ttsea.jlibrary.common.JLog;

/**
 * Utils <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2015.08.06 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0
 */
public class Utils {

    private static String TAG = "Utils.Utils";

    /** 隐藏软键盘 */
    public static void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 通过Uri获取图片的绝对路径
     *
     * @param activity
     * @param uri
     * @return String or null
     */
    public static String getPathByUri(Activity activity, Uri uri) {
        String path = null;
        if (uri == null) {
            return null;
        }

        try {
            // Bitmap bm = MediaStore.Images.Media
            // .getBitmap(resolver, originalUri); // 先得到bitmap图片
            String[] proj = {MediaStore.Images.Media.DATA};
            // 好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = activity.managedQuery(uri, proj, null,
                    null, null);
            // 按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            // 将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            // 最后根据索引值获取图片路径
            path = cursor.getString(column_index);

        } catch (Exception e) {
            JLog.e(TAG, "getPathByUri, Exception e:" + e.toString());
        }
        return path;
    }

    /** 判断str是否为空 */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() < 1) {
            return true;
        }
        return false;
    }
}
