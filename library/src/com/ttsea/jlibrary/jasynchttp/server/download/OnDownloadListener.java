package com.ttsea.jlibrary.jasynchttp.server.download;

/**
 * 下载监听，该监听是通过Handler来传递的，所以是运行在主线程中，可以用来更改UI的状态 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/5/9 13:53 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/9 13:53
 */
public interface OnDownloadListener {

    /**
     * 准备下载前，用来设置各个view的状态
     *
     * @param downloader 下载器
     */
    void onPreDownload(Downloader downloader);

    /**
     * 连接中...
     *
     * @param downloader 下载器
     */
    void onDownloadLinking(Downloader downloader);

    /**
     * 开始下载
     *
     * @param downloader 下载器
     */
    void onDownloadStart(Downloader downloader);

    /**
     * 正在下载中
     *
     * @param downloader    下载器
     * @param totalSizeByte 文件大小(byte)
     * @param byteSoFar     已经下载的大小(byte)
     * @param speed         下载速度(b/s)
     */
    void onDownloading(Downloader downloader, long totalSizeByte, long byteSoFar, long speed);

    /**
     * 该下载被暂停
     *
     * @param downloader 下载器
     * @param reason     被暂停原因
     */
    void onDownloadPause(Downloader downloader, int reason);

    /**
     * 该下载被取消
     *
     * @param downloader 下载器
     * @param reason     被取消原因
     */
    void onDownloadCancel(Downloader downloader, int reason);

    /** 下载失败 */
    //void onDownloadFailed();

    /**
     * 下载完成
     *
     * @param downloader 下载器
     */
    void onComplete(Downloader downloader);
}
