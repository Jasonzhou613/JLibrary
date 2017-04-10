package com.ttsea.jlibrary.photo.select;

import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.utils.DateUtils;
import com.ttsea.jlibrary.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * // to do <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
class ImageUtils {
    private static final String TAG = "Select.ImageUtils";

    static String formatPhotoDate(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            long time = file.lastModified();
            return DateUtils.parseString(time, "yyyy-MM-dd");
        }
        return "1970-01-01";
    }

    static File createTmpFile(String filePath, String suffix) {
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
