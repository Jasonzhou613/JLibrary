package com.ttsea.jlibrary.jasynchttp.server.download;

import android.os.Process;

import com.ttsea.jlibrary.common.JLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载线程，该线程负责下载内容 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/1/4 9:58 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/5 10:05
 */
class DownloadThread extends Thread {
    private final String TAG = "DownloadThread";

    private HttpUrlStack httpUrlStack;
    private DownloadFileInfo downloadInfo;
    private Downloader downloader;

    private String url;//下载地址
    private String threadId;//下载线程的id
    private int threadPriority = Process.THREAD_PRIORITY_BACKGROUND;
    //记录请求失败时，当前重试的次数
    private int currentRetryCount = 0;

    /** 是否完成下载 */
    private boolean isComplete = false;
    private boolean isQuite = false;

    public DownloadThread(Downloader downloader, DownloadFileInfo downloadInfo) {
        this.downloader = downloader;
        this.downloadInfo = downloadInfo;

        this.threadId = downloadInfo.getThread_id();
        this.url = downloader.getUrl();

        init();
    }

    public String getUrl() {
        return url;
    }

    public DownloadFileInfo getDownloadInfo() {
        return downloadInfo;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public boolean isComplete() {
        return isComplete;
    }

    private void init() {
        httpUrlStack = new HttpUrlStack(downloader.getDownloadOption().getHttpOption(), downloadInfo);
    }

    public void quite() {
        isQuite = true;
    }

    public void run() {
        if (downloader.isCancelled() || downloader.isPaused()
                || isQuite) {
            JLog.d(TAG, "downloader has cancelled or paused or quited, download status:"
                    + downloader.getStatus() + ", id：" + downloader.getId());
            return;
        }
        JLog.d(TAG, "threadId:" + threadId + ", start download..."
                + ", downloadInfo:" + downloadInfo.toString()
                + ", threadPriority:" + threadPriority);
        Process.setThreadPriority(threadPriority);

        HttpURLConnection conn = null;

        int maxRetryCount = downloader.getDownloadOption().getReTryCount() + 1;
        while (currentRetryCount < maxRetryCount) {
            currentRetryCount++;
            if (currentRetryCount > 1) {
                JLog.d(TAG, "threadId:" + threadId + ", retry... currentRetryCout:" + (currentRetryCount - 1));
            }

            try {
                perfrom(conn);
                break;

            } catch (FileNotFoundException e) {
                JLog.e(TAG, "threadId:" + threadId + ", FileNotFoundException e:" + e.toString());
                isComplete = false;
                if (currentRetryCount < maxRetryCount) {
                    continue;
                }
                downloader.cancel(Downloader.ERROR_HTTP_FILE_NOT_FOUND, e.toString());

            } catch (IOException e) {
                JLog.e(TAG, "threadId:" + threadId + ", IOException e:" + e.toString());
                isComplete = false;
                if (currentRetryCount < maxRetryCount) {
                    continue;
                }
                downloader.cancel(Downloader.ERROR_HTTP_DATA_ERROR, e.toString());

            } catch (Exception e) {
                JLog.e(TAG, "threadId:" + threadId + ", Exception e:" + e.toString());
                isComplete = false;
                if (currentRetryCount < maxRetryCount) {
                    continue;
                }
                downloader.cancel(Downloader.ERROR_UNKNOWN, e.toString());

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    private void perfrom(HttpURLConnection conn) throws Exception {
        URL url = new URL(getUrl());
        conn = httpUrlStack.openConnection(url);
        int responseCode = conn.getResponseCode();
        //url被重定向
        if (!url.getHost().equals(conn.getURL().getHost())) {
            // we were redirected!
            JLog.e(TAG, "we were redirected, redirected url:" + conn.getURL());
            downloader.cancel(Downloader.ERROR_TOO_MANY_REDIRECTS);
            return;
        }

        if (responseCode < 200 || responseCode > 299) {
            handleErrorResponseCode(responseCode);
            return;
        }
        JLog.d(TAG, "threadId:" + threadId + ", responseCode:" + responseCode);

        String fileAbsolutelyPath = downloadInfo.getLocal_file_path()
                + File.separator + downloadInfo.getLocal_filename();
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileAbsolutelyPath, "rwd");
        File file = new File(fileAbsolutelyPath);

        long offset = downloadInfo.getStart_bytes() + downloadInfo.getBytes_so_far();
        randomAccessFile.seek(offset);

        InputStream is = conn.getInputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        while ((length = is.read(buffer)) != -1
                && !isQuite
                && !downloader.isCancelled()
                && !downloader.isPaused()) {
            //下载过程中，文件被删除了后需要退出并取消下载
            if (!file.exists()) {
                isQuite = true;
                downloader.cancel(Downloader.ERROR_HTTP_FILE_NOT_FOUND, "FILE_NOT_FOUND");
                continue;
            }
            randomAccessFile.write(buffer, 0, length);
            long byteAlReady = downloadInfo.getBytes_so_far();
            downloadInfo.setBytes_so_far((byteAlReady + length));
        }

        if (downloadInfo.getBytes_so_far() >= (downloadInfo.getEnd_bytes() - downloadInfo.getStart_bytes())) {
            isComplete = true;
        } else {
            isComplete = false;
        }

        JLog.d(TAG, "byte so far:" + downloadInfo.getBytes_so_far()
                + ", need download:" + (downloadInfo.getEnd_bytes() - downloadInfo.getStart_bytes()));
        JLog.d(TAG, "thread:" + threadId + ", download completed:" + isComplete);
    }


    /** 处理错误的 responeCode */
    private void handleErrorResponseCode(int responseCode) {
        String msg = "";
        switch (responseCode) {
            case 404:
                msg = "file not found";
                downloader.cancel(Downloader.ERROR_HTTP_FILE_NOT_FOUND, "responseCode:" + responseCode);
                break;

            default:
                msg = "ERROR_UNHANDLED_HTTP_CODE";
                downloader.cancel(Downloader.ERROR_UNHANDLED_HTTP_CODE, "responseCode:" + responseCode);
                break;
        }
        JLog.e(TAG, "cancel download, responseCode:" + responseCode + ", msg:" + msg);
    }
}