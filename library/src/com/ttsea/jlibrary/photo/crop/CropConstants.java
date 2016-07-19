package com.ttsea.jlibrary.photo.crop;

/**
 * 剪切图片的常量 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/2/18 14:13 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/2/18 14:13
 */
public class CropConstants {
    /**
     * 剪切图片的action
     */
    public static final String ACTION_CROP = "com.ttsea.photo.crop.action.CROP";

    /**
     * 剪切图片时Intent中所带的图片的路径
     */
    public static final String IMAGE_PATH = "image_path";

    /**
     * 剪切图片时Intent中所带的x比列
     */
    public static final String ASPECT_X = "aspectX";

    /**
     * 剪切图片时Intent中所带的y比列
     */
    public static final String ASPECT_Y = "aspectY";

    /**
     * 设置剪切图片输出的x长度(px)
     */
    public static final String OUTPUT_X = "outputX";

    /**
     * 设置剪切图片输出的y长度(px)
     */
    public static final String OUTPUT_Y = "outputY";

    /**
     * 剪切图片后是否在Intent中设置剪切后的图片的Bitmap
     */
    public static final String RETURN_DATA = "return-data";

    /**
     * 当 return_data为true时，用该data作为key来存储bitmap
     */
    public static final String DATA = "data";

    /**
     * 剪切图片后，图片的输出路径
     */
    public static final String OUT_PUT_PATH = "out_put_path";

    /**
     * 图片的后缀
     */
    public static final String IMAGE_SUFFIX = "image_suffix";

    /**
     * 是否保持长宽比，默认为false
     */
    public static final String FIXED_ASPECT_RATIO = "fixed_aspect_ratio";

    /** 剪切图片 request code */
    public static final int REQUEST_CODE_CROP_IMAGE = 0x101;
    /** 剪切图片 result code，取消 */
    public static final int RESULT_CODE_CROP_CANCLED = 0x102;
    /** 剪切图片 result code，出错 */
    public static final int RESULT_CODE_CROP_ERROR = 0x103;
    /** 剪切图片 result code，成功 */
    public static final int RESULT_CODE_CROP_OK = 0x104;
}
