package com.ttsea.jlibrary.photo.gallery;

/**
 * PhotoViewAttacher中保存图片监听 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
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
