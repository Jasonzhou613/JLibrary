package com.ttsea.jlibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.ttsea.jlibrary.common.JLog;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Bitmap处理类 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016.02.17 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016.02.17
 */
public class BitmapUtils {
    private static String TAG = "Utils.BitmapUtils";

    /**
     * bitmap转为base64
     *
     * @param bitmap bitmap
     * @return String or null
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;

        if (bitmap == null) {
            return null;
        }

        try {

            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] bitmapBytes = baos.toByteArray();
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
            JLog.e(TAG, "bitmapToBase64, Exception e:" + e.toString());

        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 按比例压缩bitmap中的长宽,如果长宽都小于maxWidthAndHeight则不压缩
     *
     * @param bmp
     * @param maxWidthAndHeight 长宽最大值
     * @return
     */
    public static Bitmap compressBitmapWidthAndHeight(Bitmap bmp, int maxWidthAndHeight) {
        if (bmp == null && bmp.isRecycled()) {
            JLog.d(TAG, "compressBitmapWidthAndHeight, bmp is null or bmp is recycled");
            return null;
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        if (width > maxWidthAndHeight || height > maxWidthAndHeight) {
            float scaleWidth = ((float) maxWidthAndHeight) / ((float) width);
            float scaleHeight = ((float) maxWidthAndHeight) / ((float) height);
            float scale = Math.min(scaleWidth, scaleHeight);

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            Log.d(TAG, "compressBitmapWidthAndHeight, maxWidthAndHeight:" + maxWidthAndHeight +
                    ", scale:" + scale +
                    ", outputWidth:" + (width * scale) +
                    ", outputHieght:" + (height * scale) +
                    ", origin width*height:" + width + "*" + height);

            return Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        }

        Log.d(TAG, "compressBitmapWidthAndHeight, not need to compress bitmap width and height");
        return bmp;
    }

    /**
     * 压缩图片，直至bmp的大小 小于maxSizeInKB
     *
     * @param bmp
     * @param maxSizeInKB bitmap最大值，单位KB
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bmp, int maxSizeInKB) {
        if (maxSizeInKB <= 0) {
            throw (new IllegalArgumentException("maxSizeInKB should be greater than 0"));
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = bmp.getWidth();
        options.outHeight = bmp.getHeight();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        int quality = 90;
        int bitmapSize = baos.toByteArray().length / 1024;
        // 循环判断如果压缩后图片是否大于maxSizeInKB,大于继续压缩
        while (bitmapSize > maxSizeInKB) {
            JLog.d(TAG, "compressBitmap, image size:" + bitmapSize + "kb");
            baos.reset();// 重置baos即清空baos
            // 这里压缩options%，把压缩后的数据存放到baos中
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            if (bitmapSize == baos.toByteArray().length / 1024) {
                JLog.d(TAG, "compressBitmap, break");
                break;
            }
            bitmapSize = baos.toByteArray().length / 1024;
            options.inSampleSize = options.inSampleSize * 2;
        }

        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, options);

        return bitmap;
    }

    /**
     * 通过图片地址 将图片解析成bitmap
     *
     * @param path              图片地址
     * @param maxWidthAndHeight 图片输出的最大长宽
     * @return
     */
    public static Bitmap revisionImageSize(String path, int maxWidthAndHeight) {
        Bitmap bitmap = null;
        BufferedInputStream in = null;
        BufferedInputStream in2 = null;
        try {
            in = new BufferedInputStream(new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);

            int scale = 1;
            if (options.outHeight > maxWidthAndHeight
                    || options.outWidth > maxWidthAndHeight) {
                double index = (int) Math.round(
                        Math.log(maxWidthAndHeight / (double) Math.max(options.outHeight, options.outWidth))
                                / Math.log(0.5));
                scale = (int) Math.pow(2, index);
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in2 = new BufferedInputStream(new FileInputStream(new File(path)));

            bitmap = BitmapFactory.decodeStream(in2, null, o2);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JLog.e(TAG, "revisionImageSize, FileNotFoundException e=" + e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            JLog.e(TAG, "revisionImageSize, Exception e=" + e.getMessage());
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (in2 != null) {
                    in2.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                JLog.e(TAG, "revisionImageSize, IOException e=" + e.getMessage());
            }
        }
        return bitmap;
    }

    public static Bitmap revisionImageSize(String path) {
        return revisionImageSize(path, 1024);
    }

    /**
     * 将bitmap保存
     *
     * @param savePath 保存路径
     * @param fileName 保存名称
     * @param bitmap   需要保存的bitmap
     * @return 保存成功：true，保存失败：false
     */
    public boolean saveBitmap(Context context, String savePath, String fileName, Bitmap bitmap) {
        boolean isSaveSuccessful = false;
        if (bitmap == null || bitmap.isRecycled()) {
            return isSaveSuccessful;
        }

        String filePath = savePath + File.separator + fileName;
        File f = new File(filePath);

        if (f.exists()) {
            f.deleteOnExit();
        }
        if (!f.exists()) {
            File parentFile = f.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            try {
                f.createNewFile();

            } catch (IOException e) {
                JLog.e(TAG, "saveBitmap, IOException e：" + e.toString());
                isSaveSuccessful = false;
                return isSaveSuccessful;
            }
        }

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            isSaveSuccessful = true;

        } catch (Exception e) {
            JLog.e(TAG, "saveBitmap, Exception e：" + e.toString());
            isSaveSuccessful = false;
        }

        try {
            if (fOut != null) {
                fOut.flush();
                fOut.close();
            }
        } catch (Exception e) {
            JLog.e(TAG, "saveBitmap, Exception e：" + e.toString());
        }

        if (isSaveSuccessful) {
            // 最后通知图库更新
            Uri imgUri = Uri.parse("file://" + f.getAbsolutePath());
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imgUri);
            context.sendBroadcast(intent);
        }
        return isSaveSuccessful;
    }
}

