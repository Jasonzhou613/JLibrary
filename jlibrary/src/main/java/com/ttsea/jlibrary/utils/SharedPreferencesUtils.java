package com.ttsea.jlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * SharedPreferences工具 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class SharedPreferencesUtils {

    /**
     * 保存数据的方法，拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context  上下文
     * @param fileName SharedPreferences name
     * @param key      SharedPreferences key
     * @param object   要保存的数据
     * @return 是否保存成功
     */
    public static boolean put(Context context, String fileName, String key, Object object) {

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }

        return editor.commit();
    }

    /**
     * 得到保存数据的方法，默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context       上下文
     * @param fileName      SharedPreferences name
     * @param key           SharedPreferences key
     * @param defaultObject 默认值
     * @return 返回取到的值或者null
     */
    public static Object get(Context context, String fileName, String key, Object defaultObject) {

        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 保存数据String类型数据
     *
     * @param context  上下文
     * @param fileName SharedPreferences name
     * @param key      SharedPreferences key
     * @param value    要保存的数据
     * @return 是否保存成功
     */
    public static boolean putString(Context context, String fileName, String key, String value) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);

        return editor.commit();
    }

    /**
     * 根据key获取String
     *
     * @param context      上下文
     * @param fileName     SharedPreferences name
     * @param key          SharedPreferences key
     * @param defaultValue 默认值
     * @return 返回取到的值
     */
    public static String getString(Context context, String fileName, String key, String defaultValue) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

        return sp.getString(key, defaultValue);
    }


    /**
     * 保存数据int类型数据
     *
     * @param context  上下文
     * @param fileName SharedPreferences name
     * @param key      SharedPreferences key
     * @param value    要保存的数据
     * @return 是否保存成功
     */
    public static boolean putInt(Context context, String fileName, String key, int value) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);

        return editor.commit();
    }

    /**
     * 根据key获取int
     *
     * @param context      上下文
     * @param fileName     SharedPreferences name
     * @param key          SharedPreferences key
     * @param defaultValue 默认值
     * @return 返回取到的值
     */
    public static int getInt(Context context, String fileName, String key, int defaultValue) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

        return sp.getInt(key, defaultValue);
    }


    /**
     * 保存数据Boolean类型数据
     *
     * @param context  上下文
     * @param fileName SharedPreferences name
     * @param key      SharedPreferences key
     * @param value    要保存的数据
     * @return 是否保存成功
     */
    public static boolean putBoolean(Context context, String fileName, String key, boolean value) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);

        return editor.commit();
    }

    /**
     * 根据key获取Boolean
     *
     * @param context      上下文
     * @param fileName     SharedPreferences name
     * @param key          SharedPreferences key
     * @param defaultValue 默认值
     * @return 返回取到的值
     */
    public static boolean getBoolean(Context context, String fileName, String key, boolean defaultValue) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

        return sp.getBoolean(key, defaultValue);
    }


    /**
     * 保存数据Float类型数据
     *
     * @param context  上下文
     * @param fileName SharedPreferences name
     * @param key      SharedPreferences key
     * @param value    要保存的数据
     * @return 是否保存成功
     */
    public static boolean putFloat(Context context, String fileName, String key, float value) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);

        return editor.commit();
    }

    /**
     * 根据key获取Float
     *
     * @param context      上下文
     * @param fileName     SharedPreferences name
     * @param key          SharedPreferences key
     * @param defaultValue 默认值
     * @return 返回取到的值
     */
    public static float getFloat(Context context, String fileName, String key, float defaultValue) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

        return sp.getFloat(key, defaultValue);
    }


    /**
     * 保存数据Long类型数据
     *
     * @param context  上下文
     * @param fileName SharedPreferences name
     * @param key      SharedPreferences key
     * @param value    要保存的数据
     * @return 是否保存成功
     */
    public static boolean putLong(Context context, String fileName, String key, long value) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);

        return editor.commit();
    }

    /**
     * 根据key获取Long
     *
     * @param context      上下文
     * @param fileName     SharedPreferences name
     * @param key          SharedPreferences key
     * @param defaultValue 默认值
     * @return 返回取到的值
     */
    public static long getLong(Context context, String fileName, String key, long defaultValue) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

        return sp.getLong(key, defaultValue);
    }


    /**
     * 保存数据Set<String>类型数据
     *
     * @param context  上下文
     * @param fileName SharedPreferences name
     * @param key      SharedPreferences key
     * @param value    要保存的数据
     * @return 是否保存成功
     */
    public static boolean putStringSet(Context context, String fileName, String key, Set<String> value) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);

        return editor.commit();
    }

    /**
     * 根据key获取Set<String>
     *
     * @param context      上下文
     * @param fileName     SharedPreferences name
     * @param key          SharedPreferences key
     * @param defaultValue 默认值
     * @return 返回取到的值
     */
    public static Set<String> getStringSet(Context context, String fileName, String key, Set<String> defaultValue) {

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

        return sp.getStringSet(key, defaultValue);
    }
}
