package com.ttsea.jlibrary.photo.select;

import android.content.Context;
import android.os.Environment;

import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.utils.CacheDirUtils;
import com.ttsea.jlibrary.utils.DateUtils;
import com.ttsea.jlibrary.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * //To do <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/7/13 10:45 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/7/13 10:45
 */
class ImageUtils {
    private static final String TAG = "Select.ImageUtils";

    public static String formatPhotoDate(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            long time = file.lastModified();
            return DateUtils.parseString(time, "yyyy-MM-dd");
        }
        return "1970-01-01";
    }

    public static File createTmpFile(String filePath, String suffix) {
        if (Utils.isEmpty(suffix)) {
            suffix = ".jpg";
        }
        String fileName = "IMG_" + DateUtils.parseString(System.currentTimeMillis(), "yyyyMMdd_HHmmss") + suffix;
        File file = new File(filePath, fileName);

        File parentFile = new File(file.getParent());
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                JLog.d(TAG, "IOException e:" + e.toString());
                e.printStackTrace();
            }
        }

        return file;
    }
}
