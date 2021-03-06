package com.ttsea.jlibrary.photo.crop;

/**
 * 剪切图片的常量 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
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
     * 按住剪切框中间，是否可以拖动整个剪切框
     */
    public static final String CAN_MOVE_FRAME = "can_move_frame";

    /**
     * 按住剪切框四个角，是否可以拖动剪切框的四个角
     */
    public static final String CAN_DRAG_FRAME_CONNER = "can_drag_frame_conner";

    /**
     * 是否保持长宽比
     */
    public static final String FIXED_ASPECT_RATIO = "fixed_aspect_ratio";

    /**
     * 剪切模式，{@link CropView#CROP_MODE_OVAL} and {@link CropView#CROP_MODE_RECTANGLE}
     */
    public static final String CROP_MODEL = "crop_model";

    /** 剪切图片 request code */
    public static final int REQUEST_CODE_CROP_IMAGE = 0x101;
    /** 剪切图片 result code，取消 */
    public static final int RESULT_CODE_CROP_CANCLED = 0x102;
    /** 剪切图片 result code，出错 */
    public static final int RESULT_CODE_CROP_ERROR = 0x103;
    /** 剪切图片 result code，成功 */
    public static final int RESULT_CODE_CROP_OK = 0x104;
}
