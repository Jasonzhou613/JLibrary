package com.ttsea.jlibrary.photo.select;

import android.content.Context;
import android.os.Environment;

import com.ttsea.jlibrary.utils.CacheDirUtils;
import com.ttsea.jlibrary.utils.DateUtils;

import java.io.File;
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

    private final static String TAG = "TimeUtils";

    public static String formatPhotoDate(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            long time = file.lastModified();
            return DateUtils.parseString(time, "yyyy-MM-dd");
        }
        return "1970-01-01";
    }

    public static File createTmpFile(Context context, String filePath) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
        String externalStorageState = Environment.getExternalStorageState();
        File dir = new File(Environment.getExternalStorageDirectory() + filePath);
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return new File(dir, timeStamp + ".jpg");
        } else {
            String cacheDir = CacheDirUtils.getImageCacheDir(context);
            return new File(cacheDir, timeStamp + ".jpg");
        }
    }
}
