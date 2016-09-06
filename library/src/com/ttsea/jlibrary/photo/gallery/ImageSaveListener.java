package com.ttsea.jlibrary.photo.gallery;

/**
 * PhotoViewAttacher中保存图片监听<br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/9/6 10:04 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/9/6 10:04
 */
interface ImageSaveListener {

    /**
     * 开始保存
     */
    void onStartSave();

    /**
     * 保存成功
     *
     * @param path 保存的地址
     */
    void onSaveComplete(String path);

    /**
     * 保存失败
     *
     * @param reason 失败原因
     */
    void onSaveFailed(String reason);
}
