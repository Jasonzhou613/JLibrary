package com.ttsea.jlibrary.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ttsea.jlibrary.common.JLog;

/**
 * Utils <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class Utils {

    private static String TAG = "Utils.Utils";

    /** 隐藏软键盘 */
    public static void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /** 显示软键盘 */
    public static void showInput(final Context context, final EditText editText) {
        try {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager keyboard = (InputMethodManager)
                            context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(editText, 0);
                }
            }, 500);
        } catch (Exception e) {
            JLog.e(TAG, "Exception e:" + e.toString());
        }
    }

    /** 判断str是否为空 */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() < 1) {
            return true;
        }
        return false;
    }
}
