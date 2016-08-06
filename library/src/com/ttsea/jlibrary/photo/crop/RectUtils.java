package com.ttsea.jlibrary.photo.crop;

import android.graphics.Rect;
import android.graphics.RectF;

import com.ttsea.jlibrary.common.JLog;


/**
 * RectUtils <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/3/4 10:11 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/3/4 10:11
 */
class RectUtils {
    private static final String TAG = "Crop.RectUtils";

    /**
     * 判断由 left,top,right,bottom组成的矩形，平移offsetX后是否还被包含在boundsRect中
     *
     * @param left       left
     * @param top        top
     * @param right      right
     * @param bottom     bottom
     * @param offsetX    X轴平移量
     * @param boundsRect 总区域
     * @return left, top, right, bottom组成的矩形，平移offsetX后,超出boundsRect边界则返回true，否则返回false
     */
    public static boolean isOutOfBounds_X(int left, int top, int right, int bottom,
                                          int offsetX, Rect boundsRect, CropView.TouchRange touchRange) {
        return isOutOfBounds(left, top, right, bottom, offsetX, 0, boundsRect, touchRange);
    }

    /**
     * 判断由 left,top,right,bottom组成的矩形，平移offsetX后是否还被包含在boundsRect中
     *
     * @param left       left
     * @param top        top
     * @param right      right
     * @param bottom     bottom
     * @param offsetY    Y轴平移量
     * @param boundsRect 总区域
     * @return left, top, right, bottom组成的矩形，平移offsetY后,超出boundsRect边界则返回true，否则返回false
     */
    public static boolean isOutOfBounds_Y(int left, int top, int right, int bottom,
                                          int offsetY, Rect boundsRect, CropView.TouchRange touchRange) {
        return isOutOfBounds(left, top, right, bottom, 0, offsetY, boundsRect, touchRange);
    }

    /**
     * 判断由 left,top,right,bottom组成的矩形，平移offsetX和offsetY后是否还被包含在boundsRect中
     *
     * @param left       left
     * @param top        top
     * @param right      right
     * @param bottom     bottom
     * @param offsetX    X轴平移量
     * @param offsetY    Y轴平移量
     * @param boundsRect 总区域
     * @return left, top, right, bottom组成的矩形，平移offsetX和offsetY后,超出boundsRect边界则返回true，否则返回false
     */
    public static boolean isOutOfBounds(int left, int top, int right, int bottom,
                                        int offsetX, int offsetY, Rect boundsRect, CropView.TouchRange touchRange) {

        switch (touchRange) {
            case LEFT_TOP_RECT://左上角
                left = left + offsetX;
                top = top + offsetY;
                break;

            case LEFT_BOTTOM_RECT://左下角
                left = left + offsetX;
                bottom = bottom + offsetY;
                break;

            case RIGHT_TOP_RECT://右上角
                right = right + offsetX;
                top = top + offsetY;
                break;

            case RIGHT_BOTTOM_RECT://右下角
                right = right + offsetX;
                bottom = bottom + offsetY;
                break;

            case TOP_LINE://上边
                top = top + offsetY;
                break;

            case BOTTOM_LINE://下边
                bottom = bottom + offsetY;
                break;

            case LEFT_LINE://左边
                left = left + offsetX;
                break;

            case RIGHT_LINE://右边
                right = right + offsetX;
                break;

            case ON_FRAME://剪切框以内
                left = left + offsetX;
                top = top + offsetY;
                right = right + offsetX;
                bottom = bottom + offsetY;
                break;

            case OUT_FRAME://剪切框以外
            case UNKNOWN://未知
            default:
                return true;
        }

        if (boundsRect.contains(left, top, right, bottom)) {
            return false;
        }
        return true;
    }

    /**
     * 判断矩形是宽是否小于最小值
     *
     * @param left      left
     * @param right     right
     * @param minLength 最小值
     * @return 小于最小值返回true，否则返回false
     */
    public static boolean isLessThanMinWidth(int left, int right, int minLength) {
        if (right - left < minLength) {
            return true;
        }
        return false;
    }

    /**
     * 判断矩形是长是否小于最小值
     *
     * @param top       top
     * @param bottom    bottom
     * @param minLength 最小值
     * @return 小于最小值返回true，否则返回false
     */
    public static boolean isLessThanMinHeight(int top, int bottom, int minLength) {
        if (bottom - top < minLength) {
            return true;
        }
        return false;
    }

    /**
     * 判断矩形是长宽是否小于最小值
     *
     * @param left      left
     * @param top       top
     * @param right     right
     * @param bottom    bottom
     * @param minLength 最小值
     * @return 小于最小值返回true，否则返回false
     */
    public static boolean isLessThanMinFrame(int left, int top, int right, int bottom, int minLength) {
        if (right - left < minLength
                || bottom - top < minLength) {
            JLog.d(TAG, "isLessThanMinFrame, true");
            return true;
        }
        return false;
    }

    /**
     * 返回矩形r的四个脚的坐标
     *
     * @param r
     * @return
     */
    public static float[] getCornersFromRect(RectF r) {
        return new float[]{
                r.left, r.top,
                r.right, r.top,
                r.right, r.bottom,
                r.left, r.bottom
        };
    }

    /**
     * 返回矩形r的中心
     *
     * @param r
     * @return
     */
    public static float[] getCenterFromRect(RectF r) {
        return new float[]{r.centerX(), r.centerY()};
    }

    /**
     * Takes an array of 2D coordinates representing corners and returns the
     * smallest rectangle containing those coordinates.
     *
     * @param array array of 2D coordinates
     * @return smallest rectangle containing coordinates
     */
    public static RectF trapToRect(float[] array) {
        RectF r = new RectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        for (int i = 1; i < array.length; i += 2) {
            float x = array[i - 1];
            float y = array[i];
            r.left = (x < r.left) ? x : r.left;
            r.top = (y < r.top) ? y : r.top;
            r.right = (x > r.right) ? x : r.right;
            r.bottom = (y > r.bottom) ? y : r.bottom;
        }
        r.sort();
        return r;
    }

    /**
     * Gets a float array of two lengths representing a rectangles width and height
     * The order of the corners in the input float array is:
     * 0------->1
     * ^        |
     * |        |
     * |        v
     * 3<-------2
     *
     * @param corners the float array of corners (8 floats)
     * @return the float array of width and height (2 floats)
     */
    public static float[] getRectSidesFromCorners(float[] corners) {
        return new float[]{(float) Math.sqrt(Math.pow(corners[0] - corners[2], 2) + Math.pow(corners[1] - corners[3], 2)),
                (float) Math.sqrt(Math.pow(corners[2] - corners[4], 2) + Math.pow(corners[3] - corners[5], 2))};
    }
}
