package com.ttsea.jlibrary.utils;

import android.content.Context;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JLog;

import java.io.File;

/**
 * 缓存目录单元，这里可以获取app的缓存目录 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/2/16 17:06 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0
 */
public class CacheDirUtils {
    private static String TAG = "Utils.CacheDirUtils";

    /** release版本所有的缓存数据都存在该目录下，如: /data/data/[packagename]/cache */
    private static String CACHE_DIR;
    /** 调试模式下，所有的缓存数据都存在该目录下 */
    private static String ROOT_CACHE_DIR_DEBUG = null;
    /**
     * 所有的图片缓存都存在该目录，在调试模式中，该目录存在于{@link #ROOT_CACHE_DIR_DEBUG}中；
     * 正式情况存在于{@link #CACHE_DIR}中
     */
    private static String CACHE_IMAGE_DIR = null;
    /**
     * 所有的数据缓存都存在该目录，在调试模式中 该目录存在于{@link #ROOT_CACHE_DIR_DEBUG}中，
     * 正式情况存在于{@link #CACHE_DIR}中
     */
    private static String CACHE_DATA_DIR = null;
    /** 需要存在SD卡里的数据目录 */
    private static String ROOT_SD_DATA_DIR = null;
    /** 临时目录 */
    private static String TEMP_DIR = null;


    /** 如果目录变量为初始化，则初始化 */
    public static void initIfNeed(Context context) {
        if (ROOT_CACHE_DIR_DEBUG == null
                || CACHE_IMAGE_DIR == null
                || CACHE_DATA_DIR == null
                || ROOT_SD_DATA_DIR == null
                || TEMP_DIR == null) {

            ROOT_CACHE_DIR_DEBUG = getStringById(context, R.string._j_root_cache_dir_debug);
            CACHE_IMAGE_DIR = getStringById(context, R.string._j_cache_image_dir);
            CACHE_DATA_DIR = getStringById(context, R.string._j_cache_data_dir);
            ROOT_SD_DATA_DIR = getStringById(context, R.string._j_root_sd_data_dir);
            TEMP_DIR = getStringById(context, R.string._j_tmp_dir);
            JLog.d(TAG, "init dirs: " + "ROOT_CACHE_DIR_DEBUG:" + ROOT_CACHE_DIR_DEBUG +
                    "\n " + "CACHE_IMAGE_DIR:" + CACHE_IMAGE_DIR +
                    "\n " + "CACHE_DATA_DIR:" + CACHE_DATA_DIR +
                    "\n " + "ROOT_SD_DATA_DIR:" + ROOT_SD_DATA_DIR +
                    "\n " + "TEMP_DIR:" + TEMP_DIR +
                    "");
        }
    }

    /**
     * 获取缓存的根目录，返回的是绝对地址<br/>
     * 在调试模式的时候返回的是SD卡的{@link #ROOT_CACHE_DIR_DEBUG}目录
     *
     * @param context 上下文
     * @return String
     */
    public static String getCacheDir(Context context) {
        initIfNeed(context);
        if (JLog.isDebugMode()) {
            String exSDdir = SdStatusUtils.getExternalStorageAbsoluteDir();
            if (exSDdir != null) {
                CACHE_DIR = exSDdir + File.separator + getStringById(context, R.string._j_root_cache_dir_debug);
                createDirIfNeed(CACHE_DIR);
                return CACHE_DIR;
            }
        }

        CACHE_DIR = context.getCacheDir().getAbsolutePath();
        createDirIfNeed(CACHE_DIR);

        return CACHE_DIR;
    }

    /**
     * 获取图片的缓存目录，返回的是绝对地址<br/>
     * 在调试模式的时候返回的是SD卡的{@link #ROOT_CACHE_DIR_DEBUG}目录下的CACHE_IMAGE_DIR
     *
     * @param context 上下文
     * @return String
     */
    public static String getImageCacheDir(Context context) {
        initIfNeed(context);
        String dirPath = getCacheDir(context) + File.separator + CACHE_IMAGE_DIR;
        createDirIfNeed(dirPath);
        return dirPath;
    }

    /**
     * 获取数据缓存的目录，返回的是绝对地址<br/>
     * 在调试模式的时候返回的是SD卡的{@link #ROOT_CACHE_DIR_DEBUG}目录下的CACHE_DATA_DIR
     *
     * @param context 上下文
     * @return String
     */
    public static String getDataCacheDir(Context context) {
        initIfNeed(context);
        String dirPath = getCacheDir(context) + File.separator + CACHE_DATA_DIR;
        createDirIfNeed(dirPath);
        return dirPath;
    }

    /**
     * 获取需要存放在SD卡里的数据的目录
     *
     * @param context 上下文
     * @return String
     */
    public static String getSdDataDir(Context context) {
        initIfNeed(context);
        String exSDdir = SdStatusUtils.getExternalStorageAbsoluteDir();
        if (exSDdir == null) {
            return getCacheDir(context);
        }

        String sdDataDir = exSDdir + File.separator + ROOT_SD_DATA_DIR;
        createDirIfNeed(sdDataDir);

        return sdDataDir;
    }

    public static String getTempDir(Context context) {
        initIfNeed(context);
        String dirPath = getSdDataDir(context) + File.separator + TEMP_DIR;
        createDirIfNeed(dirPath);
        return dirPath;
    }

    /**
     * 如果该目录不存在，则创建
     *
     * @param dirPath 目录路径
     * @return 创建成功:true，创建失败:false
     */
    private static boolean createDirIfNeed(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                JLog.d(TAG, "createDirIfNeed, create success, dir:" + dirPath);
                return true;
            } else {
                JLog.d(TAG, "createDirIfNeed, create failed, dir:" + dirPath);
            }
            return false;
        }
        return true;
    }

    private static String getStringById(Context context, int resId) {
        return context.getResources().getString(resId);
    }
}
