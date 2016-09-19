package com.ttsea.jlibrary.jasynchttp.server.download;

/**
 * Created by Jason on 2015/12/31.
 */
interface TaskHandler {

    /** 是否支持暂停 */
    boolean isSupportPause();

    void setSupportPause(boolean supportPause);

    /** 是否支持唤醒 */
    boolean isSupportResume();

    void setSupportResume(boolean supportResume);

    /** 是否支持取消 */
    boolean isSupportCancel();

    void setSupportCancel(boolean supportCancel);

    /** 是否已暂停 */
    boolean isPaused();

    /** 是否已取消 */
    boolean isCancelled();

    /**
     * 暂停
     *
     * @param reason 暂停原因
     */
    void pause(int reason);

    /** 唤醒 */
    void resume();

    /**
     * 取消
     *
     * @param reason 取消原因
     */
    void cancel(int reason);

    /** 重新下载 */
    void reDownload();
}
