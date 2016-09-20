package com.ttsea.jlibrary.jasynchttp.server.download;

import android.content.Context;

import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.jasynchttp.db.DownloadOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载管理类 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/1/5 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/5 10:05
 */
public class DownloadManager {
    private static String TAG = "DownloadManager";
    private Context appContext;
    private List<Downloader> downloaderList;

    private static DownloadManager downloadManager;

    private final int DEFAULT_MAX_DOWNLOADING_COUNT = 2;

    /** 同时最大下载数 */
    private int maxDownloadingCount = DEFAULT_MAX_DOWNLOADING_COUNT;

    /** 设置同时最大下载数，建议1-3 */
    public void setMaxDownloadingCount(int maxDownloadingCount) {
        if (maxDownloadingCount < 1) {
            throw new IllegalArgumentException("maxDownloadingCount must greater than 0.");
        }
        this.maxDownloadingCount = maxDownloadingCount;
    }

    public int getMaxDownloadingCount() {
        return maxDownloadingCount;
    }

    public static DownloadManager getInstance(Context appContext) {
        if (downloadManager == null) {
            downloadManager = new DownloadManager(appContext);
        }
        return downloadManager;
    }

    private DownloadManager(Context appContext) {
        this.appContext = appContext;

        init();
    }

    private void init() {
        List<Downloader> downloaders = DownloadOperation.getDownloaders(appContext);
        downloaderList = new ArrayList<Downloader>();
        downloaderList.clear();
        downloaderList.addAll(downloaders);
    }

    /**
     * 增加一个新的下载任务
     *
     * @param url 下载地址
     */
    public void addNewDownloader(String url) {
        addNewDownloader(url, null, null);
    }

    public void addNewDownloader(String url, DownloadOption option) {
        addNewDownloader(url, option, null);
    }

    public void addNewDownloader(String url, OnDownloadListener l) {
        addNewDownloader(url, null, l);
    }

    /**
     * 增加一个新的下载任务，如果该下载已经存在，则无须在添加，直接启动
     *
     * @param url    下载地址
     * @param option Downloader选项
     */
    public void addNewDownloader(String url, DownloadOption option, OnDownloadListener l) {
        Downloader downloader = new Downloader(appContext, url, option, l);

        if (!isDownloaderExist(downloader)) {
            addDownloader(downloader);
        }

        fitDownloadList();
    }

    private void addDownloader(Downloader downloader) {
        if (downloaderList == null) {
            downloaderList = new ArrayList<Downloader>();
        }
        JLog.d(TAG, "addDownloader in to downloaderList");
        downloaderList.add(downloader);
    }

    /**
     * 根据下载地址移除指定下载器
     *
     * @param url            下载地址
     * @param needDeleteFile 是否删除文件
     */
    public void removeDownloader(String url, boolean needDeleteFile) {
        int position = 0;
        // 从下载列表中移除
        while (position < downloaderList.size()) {
            Downloader loader = downloaderList.get(position);
            if (loader.getUrl().equals(url)) {
                //首先取消该下载，再从下载列表中移除
                loader.cancel(Downloader.ERROR_BLOCKED, "delete by human");
                if (needDeleteFile) {
                    loader.deleteFile();
                }
                downloaderList.remove(loader);
                position = 0;
                JLog.d(TAG, "downloader removed from downloaderList");
            }
            position++;
        }
        //从下载记录中移除
        long count = DownloadOperation.deleteRecord(appContext, url);
        if (count > 0) {
            JLog.d(TAG, "downloader record has removed from DB");
        }
    }

    /**
     * 移除指定下载器
     *
     * @param downloader     下载器
     * @param needDeleteFile 是否删除文件
     */
    public void removeDownloader(Downloader downloader, boolean needDeleteFile) {
        removeDownloader(downloader.getUrl(), needDeleteFile);
    }

    /**
     * 根据下载地址移除指定下载器，但不删除已经下载好的文件
     *
     * @param url 下载地址
     */
    public void removeDownloader(String url) {
        removeDownloader(url, false);
    }

    /**
     * 移除指定下载器，但不删除已经下载好的文件
     *
     * @param downloader 下载器
     */
    public void removeDownloader(Downloader downloader) {
        removeDownloader(downloader.getUrl());
    }

    /**
     * 移除所有下载器
     *
     * @param needDeleteFile 是否删除文件
     */
    public void removeAllDownloader(boolean needDeleteFile) {
        while (downloaderList.size() > 0) {
            Downloader loader = downloaderList.get(0);
            DownloadManager.getInstance(appContext).removeDownloader(loader, needDeleteFile);
        }
    }

    /** 移除所有下载器，但不删除已经下载好的文件 */
    public void removeAllDownloader() {
        while (downloaderList.size() > 0) {
            Downloader loader = downloaderList.get(0);
            DownloadManager.getInstance(appContext).removeDownloader(loader, false);
        }
    }

    /** 判断该下载是否已经存在列表中 */
    public boolean isDownloaderExist(Downloader downloader) {
        return isDownloaderExist(downloader.getUrl());
    }

    public boolean isDownloaderExist(String url) {
        if (downloaderList == null || url == null) {
            return false;
        }
        for (int i = 0; i < downloaderList.size(); i++) {
            Downloader d = downloaderList.get(i);
            if (url.equals(d.getUrl())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 暂停某个下载任务，如果url为null则会暂停所有的下载任务
     *
     * @param url    下载任务的tag
     * @param reason 暂停原因
     */
    public void pauseDownloader(String url, int reason) {
        Downloader loader = getDownloader(url);
        if (loader == null) {
            JLog.d(TAG, "pause downloader failed, downloader is null, url:" + url);
            return;
        }
        loader.pause(reason);
    }

    /**
     * 重新启动某个下载任务，如果url为null则会重启所有的下载任务
     *
     * @param url 下载任务的tag
     */
    public void resumeDownloader(String url) {
        Downloader loader = getDownloader(url);
        if (loader == null) {
            JLog.d(TAG, "resume downloader failed, downloader is null, url:" + url);
            return;
        }
        loader.resume();
    }

    /**
     * 取消某个下载任务，如果url为null则会取消所有的下载任务
     *
     * @param url    下载任务的tag
     * @param reason 取消原因
     */
    public void cancelDownloader(String url, int reason) {
        Downloader loader = getDownloader(url);
        if (loader == null) {
            JLog.d(TAG, "cacel downloader failed, downloader is null, url:" + url);
            return;
        }
        loader.cancel(reason);
    }

    /** 将所有正在运行的下载器暂停 */
    public void pauseAllDownloader(int reason) {
        if (downloaderList != null) {
            for (int i = 0; i < downloaderList.size(); i++) {
                Downloader loader = downloaderList.get(i);
                loader.pause(reason);
            }
        }
    }

    public void resumeAllDownloader() {
        if (downloaderList != null) {
            for (int i = 0; i < downloaderList.size(); i++) {
                Downloader loader = downloaderList.get(i);
                loader.resume();
            }
        }
    }

    public void cancelAllDownloader(int reason) {
        if (downloaderList != null) {
            for (int i = 0; i < downloaderList.size(); i++) {
                Downloader loader = downloaderList.get(i);
                loader.cancel(reason);
            }
        }
    }

    /**
     * 获取对应url的下载者，如果url为空则返回全部
     *
     * @return List of Downloader
     */
    public List<Downloader> getDownloaderList() {
        return downloaderList;
    }

    /**
     * 获取对应url的下载者，如果url为空则返回空
     *
     * @param url 下载地址
     * @return Downloader
     */
    public Downloader getDownloader(String url) {
        if (url == null) {
            return null;
        }
        for (int i = 0; i < downloaderList.size(); i++) {
            Downloader downloader = downloaderList.get(i);
            String dTag = downloader.getUrl();
            if (url.equals(dTag)) {
                return downloader;
            }
        }
        return null;
    }

    /**
     * 自动适配下载列表<br/>
     * 当一个下载完后，将状态为STATUS_PENDING进行自动下载，<br/>
     * 并且控制同时下载的数量不超过maxDownloadingCount
     */
    public void fitDownloadList() {
        if (maxDownloadingCount <= getDownloadingCount()) {
            return;
        }
        for (int i = 0; i < downloaderList.size(); i++) {
            Downloader loader = downloaderList.get(i);
            if (loader.getStatus() == Downloader.STATUS_PENDING) {
                loader.setStatus(Downloader.STATUS_LINKING);
                loader.start();
            }
            if (maxDownloadingCount <= getDownloadingCount()) {
                return;
            }
        }
    }

    /** 获取正在下载数量 */
    public int getDownloadingCount() {
        int count = 0;
        for (int i = 0; i < downloaderList.size(); i++) {
            Downloader loader = downloaderList.get(i);
            if (loader.getStatus() == Downloader.STATUS_RUNNING
                    || loader.getStatus() == Downloader.STATUS_LINKING) {
                count++;
            }
        }
        return count;
    }
}