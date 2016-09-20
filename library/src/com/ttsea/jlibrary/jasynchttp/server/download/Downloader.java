package com.ttsea.jlibrary.jasynchttp.server.download;

import android.content.Context;
import android.os.Message;

import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.common.SdStatus;
import com.ttsea.jlibrary.jasynchttp.db.DownloadOperation;
import com.ttsea.jlibrary.jasynchttp.server.http.Http;
import com.ttsea.jlibrary.utils.CacheDirUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 下载器，包含多个下载线程<br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/1/6 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/5 10:05
 */
public class Downloader implements TaskHandler {
    private final String TAG = "Downloader";

    // Downloader status，数据库字段COLUMN_STATUS的值,其值范围：0x010-0x029
    public final static int STATUS_PENDING = 0x010;//等待下载
    public final static int STATUS_LINKING = 0x011;//连接中
    public final static int STATUS_RUNNING = 0x012;//正在下载
    public final static int STATUS_PAUSED = 0x013;//暂停
    public final static int STATUS_SUCCESSFUL = 0x014;//下载成功
    public final static int STATUS_FAILED = 0x015;//下载失败

    //Downloader错误缘由，数据库字段COLUMN_REASON的值,其值范围：0x030-0x049
    public final static int ERROR_UNKNOWN = 0x030;
    public final static int ERROR_HTTP_TIMEOUT = 0x031;
    public final static int ERROR_HTTP_FILE_NOT_FOUND = 0x032;
    public final static int ERROR_HTTP_DATA_ERROR = 0x033;
    public final static int ERROR_UNHANDLED_HTTP_CODE = 0x034;
    public final static int ERROR_FILE_ALREADY_EXISTS = 0x035;
    public final static int ERROR_TOO_MANY_REDIRECTS = 0x036;
    public final static int ERROR_INSUFFICIENT_SPACE = 0x037;
    public final static int ERROR_BLOCKED = 0x038;

    //Downloader暂停理由，数据库字段COLUMN_REASON的值,其值范围：0x050-0x069
    public final static int PAUSED_WAITING_TO_RETRY = 0x050;
    public final static int PAUSED_WAITING_FOR_NETWORK = 0x051;
    public final static int PAUSED_QUEUED_FOR_WIFI = 0x052;
    public final static int PAUSED_HUMAN = 0x053;
    public final static int PAUSED_UNKNOWN = 0x054;

    private int id = -1;//下载器的标识
    private Context mContext;
    private String url;//下载地址
    private DownloadOption downloadOption;

    //该下载器状态，默认为等待下载
    private int status = Downloader.STATUS_PENDING;
    private long contentLength = 0;
    private long totalByteSoFar = 0;
    private long speed = 0;

    //记录下载开始的时间戳
    private long startTimestamp;
    //记录请求失败时，当前重试的次数
    private int currentRetryCount = 0;

    private boolean isSupportPause = true;
    private boolean isSupportResume = true;
    private boolean isSupportCancel = true;
    private boolean isPaused = false;
    private boolean isCancelled = false;

    /** 更新UI时间间隔 */
    private final int DEFAULT_UPDATE_UI_PERIOD = 1000 * 1;
    private final int ON_PRE_DOWNLOAD = 0x100;
    private final int ON_DOWNLOAD_LINKING = 0x101;
    private final int ON_DOWNLOAD_START = 0x102;
    private final int ON_DOWNLOADING = 0x103;
    private final int ON_DOWNLOAD_PAUSE = 0x104;
    private final int ON_DOWNLOAD_CANCEL = 0x105;
    private final int ON_DOWNLOAD_COMPLETED = 0x106;

    private List<DownloadThread> threads;
    private HttpUrlStack httpUrlStack;
    private OnDownloadListener onDownloadListener;
    private DownloaderHandler mHandler;

    private Runnable downloadingRunnable = new Runnable() {
        @Override
        public void run() {
            switch (getStatus()) {
                case Downloader.STATUS_RUNNING:
                    mHandler.sendEmptyMessage(ON_DOWNLOADING);
                    break;

                case Downloader.STATUS_SUCCESSFUL:
                    mHandler.sendEmptyMessage(ON_DOWNLOAD_COMPLETED);
                    break;

                default:
                    break;
            }
        }
    };

    public Downloader(Context context, String url) {
        this(context, url, null);
    }

    public Downloader(Context context, String url, DownloadOption option) {
        this(context, url, option, null);
    }

    public Downloader(Context context, String url, DownloadOption option, OnDownloadListener l) {
        this.mContext = context;
        this.url = url;

        if (option == null) {
            option = new DownloadOption.Builder(mContext).build();
        }
        this.downloadOption = option;
        //如果本地保存地址为空，则设置一个默认值
        if (com.ttsea.jlibrary.utils.Utils.isEmpty(downloadOption.getSaveFilePath())) {
            downloadOption.getBuilder().setSaveFilePath(CacheDirUtils.getSdDataDir(mContext));
        }

        setOnDownloadListener(l);
        init();

        List<DownloadFileInfo> infos = DownloadOperation.getDownloadInfos(context, url);
        if (infos != null && infos.size() > 0) {
            initFromRecord(infos);
        } else {
            initNewRecord();
        }

        JLog.d(TAG, downloadOption.toString());
    }

    public String getUrl() {
        return url;
    }

    public DownloadOption getDownloadOption() {
        return downloadOption;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /** 获取所需下载的大小(byte) */
    public long getTotalSizeByte() {
        return contentLength;
    }

    /** 获取总共已经下载了多少(byte) */
    public long getByteSoFar() {
        return totalByteSoFar;
    }

    /** 获取下载速度 (byte/s) */
    public long getSpeed() {
        return speed;
    }

    /** 下载器的标识,该标识结合了url和DownloaderOption中的additionalProperty */
    public int getId() {
        if (id != -1) {
            return id;
        }
        String paramsStr = "";

        if (downloadOption.getHttpOption() != null
                && downloadOption.getHttpOption().getAdditionalProperty() != null) {
            Map<String, String> params = downloadOption.getHttpOption().getAdditionalProperty();
            //需要对params排序，否则虽然params中包含的值是一样的，但是顺序不一样也会影响获取不到缓存
            Map<String, String> sortedParams = Utils.sortByComparator(params);
            if (sortedParams != null) {
                for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                    paramsStr = paramsStr + "key=" + entry.getKey() + "-value="
                            + entry.getValue() + "--";
                }
            }
        }

        // 如果参数长度大于100，则取前面100个作为key，防止param太长而导致内存溢出
        if (paramsStr.length() > 100) {
            paramsStr = paramsStr.substring(0, 99);
        }

        String url = getUrl();
        if (url != null && url.length() > 100) {
            url = url.substring(0, 99);
        }
        String p = url + paramsStr;
        id = p.hashCode();
        return id;
    }

    /** 初始化 */
    private void init() {
        threads = new ArrayList<DownloadThread>();
        mHandler = new DownloaderHandler();
    }

    /** 从记录中进行初始化 */
    private void initFromRecord(List<DownloadFileInfo> infos) {
        DownloadOption.Builder builder = downloadOption.getBuilder();
        builder.setThreadPool(infos.size());

        threads.clear();
        for (int i = 0; i < infos.size(); i++) {
            DownloadFileInfo info = infos.get(i);
            DownloadThread task = new DownloadThread(this, info);
            threads.add(task);

            contentLength = info.getTotal_size_bytes();
            totalByteSoFar = totalByteSoFar + info.getBytes_so_far();
            builder.setSaveFilePath(info.getLocal_file_path());
            builder.setFileName(info.getLocal_filename());
            setStatus(info.getStatus());
        }
    }

    /** 初始化新的记录 */
    private void initNewRecord() {
        for (int i = 0; i < downloadOption.getThreadPool(); i++) {
            DownloadFileInfo info = getNewDownloadInfo(getUrl(), String.valueOf(i));

            DownloadThread task = new DownloadThread(this, info);
            threads.add(task);
            //将对应的下载线程信息存入数据库
            DownloadOperation.insertOrUpdate(mContext, task.getDownloadInfo());
        }
    }

    /** 启动下载，如果下载不是新建的，则唤醒 */
    public void start() {
        if (contentLength <= 0) {
            startNewDownloadTask();

        } else {
            JLog.d(TAG, "downloader already exists, will resume");
            resume();
        }
    }

    private void startNewDownloadTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startNewDownloaderRunnable();
            }
        }).start();
    }

    private void retry() {
        currentRetryCount++;
        startNewDownloadTask();
    }

    private void startNewDownloaderRunnable() {
        if (currentRetryCount > 0) {
            JLog.d(TAG, "retry... currentRetryCout:" + currentRetryCount);
        } else {
            JLog.d(TAG, "start a new download task...");
        }

        //如果存在有记录，则先删除
        DownloadOperation.deleteRecord(mContext, getUrl());
        //设置下载器状态
        setStatus(Downloader.STATUS_LINKING);
        mHandler.sendEmptyMessage(ON_DOWNLOAD_LINKING);
        isPaused = false;
        isCancelled = false;

        DownloadFileInfo downloadInfo = getNewDownloadInfo(url, "-1");
        httpUrlStack = new HttpUrlStack(downloadOption.getHttpOption(), downloadInfo);
        HttpURLConnection conn = null;

        try {
            URL url = new URL(getUrl());
            conn = httpUrlStack.openConnection(url);
            int responseCode = conn.getResponseCode();
            //url被重定向
            if (!url.getHost().equals(conn.getURL().getHost())) {
                // we were redirected!
                JLog.e(TAG, "we were redirected, redirected url:" + conn.getURL());
                cancel(Downloader.ERROR_TOO_MANY_REDIRECTS);
                return;
            }

            if (responseCode < 200 || responseCode > 299) {
                handleErrorResponseCode(responseCode);
                return;
            }

            //如果用户未设置保存的文件名，则自动从url中获取该文件名
            if (com.ttsea.jlibrary.utils.Utils.isEmpty(downloadOption.getFileName())) {
                String fileName = getFileName(conn);
                downloadOption.getBuilder().setFileName(fileName);
                JLog.d(TAG, "get file name from connection, fileName:" + fileName);
            }

            //根据文件的保存方式，来设置文件
            JLog.d(TAG, "saveFileMode: " + downloadOption.getSaveFileMode());
            String filePath = downloadOption.getSaveFilePath();
            String fileName = downloadOption.getFileName();
            File file = new File(filePath, fileName);
            boolean isFileExists = file.exists();

            //文件已经存在，并且保存方式为 NONACTION，则取消该下载
            if (isFileExists && downloadOption.getSaveFileMode() == SaveFileMode.NONACTION) {
                JLog.e(TAG, "file already exists, downloader will cancel");
                cancel(Downloader.ERROR_FILE_ALREADY_EXISTS, "file already exists, file:" + file.getAbsolutePath());
                return;

                //文件已经存在，并且保存方式为 OVERRIDE，则覆盖该文件
            } else if (isFileExists && downloadOption.getSaveFileMode() == SaveFileMode.OVERRIDE) {
                JLog.d(TAG, "file already exists and it will be override");
                file.delete();

                //文件已经存在，并且保存方式为 RENAME，则重名该文件
            } else if (isFileExists && downloadOption.getSaveFileMode() == SaveFileMode.RENAME) {
                JLog.d(TAG, "file already exists and it will be renamed");
                int fileTag = 1;
                //重命名文件，直到该文件不存在
                while (file.exists() && fileTag < Integer.MAX_VALUE) {
                    String suffix = "";
                    String tempName = "";
                    int index = fileName.lastIndexOf(".");

                    if (index > -1) {
                        suffix = fileName.substring(index);
                        tempName = fileName.substring(0, index);
                    }
                    tempName = tempName + "（" + String.valueOf(fileTag) + "）" + suffix;
                    file = new File(downloadOption.getSaveFilePath(), tempName);
                    fileTag++;
                    if (!file.exists()) {
                        fileName = tempName;
                    }
                }
                //设置文件的新名字
                downloadOption.getBuilder().setFileName(fileName);
                JLog.d(TAG, "renamed file name:" + downloadOption.getFileName());

                //文件已经存在，默认处理方式:覆盖
            } else if (isFileExists) {
                file.delete();
            }

            int threadPool = downloadOption.getThreadPool();
            contentLength = conn.getContentLength();
            //内容长度均分时，可能不能整除，所以先求余，再将余数放在最后的线程里下载
            long remainder = contentLength % threadPool;
            long average = (contentLength - remainder) / threadPool;

            //判断文件所需空间“MB”
            long needSpace = (contentLength / 1024 / 1024) + 10;
            if (!SdStatus.isABlockEnough((needSpace))) {
                String msg = "Insufficient space, need space:" + needSpace
                        + "MB, SD free space:" + SdStatus.getAvailableBlock() + "MB";
                JLog.e(TAG, "cancel download, " + msg);
                cancel(Downloader.ERROR_INSUFFICIENT_SPACE, msg);
                return;
            }

            JLog.d(TAG, "contentLength:" + contentLength + ", threadPool:" + threadPool
                    + ", average:" + average + ", remainder:" + remainder + ", isCuttedCorrect:"
                    + (contentLength == ((threadPool * average) + remainder)));
            JLog.d(TAG, "filePath:" + downloadOption.getSaveFilePath() + ", fileName:" + downloadOption.getFileName());

            threads.clear();
            for (int i = 0; i < downloadOption.getThreadPool(); i++) {
                DownloadFileInfo info = getNewDownloadInfo(getUrl(), String.valueOf(i));

                long bytes_so_far = 0;//已经下载了的长度
                long start_bytes = i * average;//最初开始位置
                long end_bytes = start_bytes + average;//结束位置
                if (i == (threadPool - 1)) {//最后一项需要加上内容长度的余数：remainder
                    end_bytes = end_bytes + remainder;
                }
                info.setTotal_size_bytes(contentLength);
                info.setBytes_so_far(bytes_so_far);
                info.setStart_bytes(start_bytes);
                info.setEnd_bytes(end_bytes);
                info.setEtag(conn.getHeaderField(Http.ResponseHeadField.ETag));
                info.setMedia_type(conn.getHeaderField(Http.ResponseHeadField.Content_Type));
                info.setTitle(info.getLocal_filename());
                info.setDescription(info.getLocal_filename());
                info.setStatus(getStatus());

                DownloadThread task = new DownloadThread(this, info);
                threads.add(task);
                //将对应的下载线程信息存入数据库
                DownloadOperation.insertOrUpdate(mContext, task.getDownloadInfo());
            }
            startTheads();

        } catch (SocketTimeoutException e) {//超时
            JLog.e(TAG, "SocketTimeoutException e:" + e.toString());
            if (currentRetryCount < downloadOption.getReTryCount()) {
                retry();
                return;
            }
            cancel(Downloader.ERROR_HTTP_TIMEOUT, e.getMessage());

        } catch (IOException e) {
            JLog.e(TAG, "IOException e:" + e.toString());
            if (currentRetryCount < downloadOption.getReTryCount()) {
                retry();
                return;
            }
            cancel(Downloader.ERROR_HTTP_DATA_ERROR, e.getMessage());

        } catch (Exception e) {
            JLog.e(TAG, "Exception e:" + e.toString());
            if (currentRetryCount < downloadOption.getReTryCount()) {
                retry();
                return;
            }
            cancel(Downloader.ERROR_UNKNOWN, e.getMessage());

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /** 启动所有线程 */
    private void startTheads() {
        startTimestamp = System.currentTimeMillis();
        setStatus(Downloader.STATUS_RUNNING);
        saveDownloaderStatus(getStatus(), Downloader.STATUS_RUNNING);
        mHandler.sendEmptyMessage(ON_DOWNLOAD_START);
        //启动下载
        JLog.d(TAG, "start download file, startTimestamp:" + startTimestamp);
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).start();
        }
        mHandler.postDelayed(downloadingRunnable, 0);
    }

    /**
     * 构建一个新的下载信息
     *
     * @param dUrl
     * @param threadId
     * @return
     */
    private DownloadFileInfo getNewDownloadInfo(String dUrl, String threadId) {
        String title = "";
        String description = "";
        String add_timestamp = String.valueOf(System.currentTimeMillis());
        String last_modified_timestamp = String.valueOf(System.currentTimeMillis());
        String local_filename = downloadOption.getFileName();
        String local_file_path = downloadOption.getSaveFilePath();
        String media_type = "";
        int reason = Downloader.PAUSED_UNKNOWN;
        int status = getStatus();
        long total_size_bytes = 0;
        long bytes_so_far = 0;
        long start_bytes = 0;
        long end_bytes = 0;

        DownloadFileInfo info = new DownloadFileInfo();
        info.setThread_id(threadId);
        info.setUrl(dUrl);
        info.setTitle(title);
        info.setDescription(description);
        info.setAdd_timestamp(add_timestamp);
        info.setLast_modified_timestamp(last_modified_timestamp);
        info.setLocal_filename(local_filename);
        info.setLocal_file_path(local_file_path);
        info.setMedia_type(media_type);
        info.setReason(reason);
        info.setStatus(status);
        info.setTotal_size_bytes(total_size_bytes);
        info.setBytes_so_far(bytes_so_far);
        info.setStart_bytes(start_bytes);
        info.setEnd_bytes(end_bytes);

        return info;
    }

    private String getFileName(HttpURLConnection conn) {
        String filename = url.substring(url.lastIndexOf("/") + 1);

        return filename;
    }

    public void setOnDownloadListener(OnDownloadListener l) {
        this.onDownloadListener = l;
    }

    @Override
    public boolean isSupportPause() {
        return isSupportPause;
    }

    @Override
    public void setSupportPause(boolean supportPause) {
        isSupportPause = supportPause;
    }

    @Override
    public boolean isSupportResume() {
        return isSupportResume;
    }

    @Override
    public void setSupportResume(boolean supportResume) {
        isSupportResume = supportResume;
    }

    @Override
    public boolean isSupportCancel() {
        return isSupportCancel;
    }

    @Override
    public void setSupportCancel(boolean supportCancel) {
        isSupportCancel = supportCancel;
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    public boolean isCompleted() {
        for (int i = 0; i < threads.size(); i++) {
            if (!threads.get(i).isComplete()) {
                return false;
            }
        }
        return true;
    }

    /** 删除下载的文件 */
    public boolean deleteFile() {
        String fileName = downloadOption.getFileName();
        String filePath = downloadOption.getSaveFilePath();
        JLog.d(TAG, "deleteFile, filePath:" + filePath + ", fileName:" + fileName);
        try {
            File file = new File(filePath, fileName);
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            JLog.e(TAG, "Exception e:" + e.toString());
        }

        return false;
    }

    @Override
    public void pause(int reason) {
        if (!isSupportPause()) {
            JLog.d(TAG, "pause, this downloader not support pause, return");
            return;
        }
        if (isPaused()) {
            JLog.d(TAG, "pause, this downloader already paused, return");
            return;
        }

        JLog.d(TAG, "pause, reason:" + getStatusStr(reason));
        for (int i = 0; i < threads.size(); i++) {
            DownloadThread thread = threads.get(i);
            thread.quite();
        }

        mHandler.removeCallbacks(downloadingRunnable);
        isPaused = true;
        setStatus(Downloader.STATUS_PAUSED);
        //保存下载信息
        saveDownloaderStatus(getStatus(), reason);

        Message msg = new Message();
        msg.what = ON_DOWNLOAD_PAUSE;
        msg.arg1 = reason;
        mHandler.sendMessage(msg);
        JLog.d(TAG, "downloader has paused, downloaderId:" + getId()
                + ", downloader status:" + getStatusStr(reason));
    }

    @Override
    public void resume() {
        if (getStatus() == STATUS_RUNNING) {
            JLog.d(TAG, "resume, this downloader is already running");
            return;
        }

        if (!isSupportResume()) {
            JLog.d(TAG, "resume, this downloader not support resume, will reDownload");
            reDownload();
            return;
        }

        JLog.d(TAG, "resume...");
        List<DownloadFileInfo> infos = DownloadOperation.getDownloadInfos(mContext, url);
        //如果获取本地下载信息失败，则重新下载
        if (infos == null || infos.size() < 1) {
            JLog.d(TAG, "get download info failed, will reDownload");
            reDownload();
            return;
        }

        String fileName = infos.get(0).getLocal_filename();
        String filePath = infos.get(0).getLocal_file_path();
        if (com.ttsea.jlibrary.utils.Utils.isEmpty(fileName)
                || com.ttsea.jlibrary.utils.Utils.isEmpty(filePath)
                || infos.get(0).getTotal_size_bytes() <= 0) {
            JLog.d(TAG, "download info error, will reDownload");
            reDownload();
            return;
        }
        // 判断下载文件是否还存在，如果不存在 则重新下载
        File file = new File(filePath, fileName);
        if (!file.exists()) {
            JLog.d(TAG, "download file not exists, will reDownload");
            reDownload();
            return;
        }

        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).quite();
        }
        threads.clear();

        for (int i = 0; i < infos.size(); i++) {
            DownloadFileInfo info = infos.get(i);
            info.setStatus(getStatus());
            DownloadThread task = new DownloadThread(this, info);
            threads.add(task);
            //将对应的下载线程信息存入数据库
            DownloadOperation.insertOrUpdate(mContext, task.getDownloadInfo());
        }

        setStatus(Downloader.STATUS_PENDING);
        mHandler.sendEmptyMessage(ON_PRE_DOWNLOAD);
        isPaused = false;
        isCancelled = false;

        startTheads();

        JLog.d(TAG, "downloader has resumed, downloaderId:" + getId()
                + ", downloader status:" + getStatusStr(getStatus()));
    }

    @Override
    public void cancel(int reason) {
        cancel(reason, "");
    }

    public void cancel(int reason, String msg) {
        currentRetryCount = 0;
        if (!isSupportCancel()) {
            JLog.d(TAG, "cancel, this downloader not support cancel, return");
            return;
        }
        if (isCancelled()) {
            JLog.d(TAG, "cancel, this downloader already cancelled, return");
            return;
        }

        mHandler.removeCallbacks(downloadingRunnable);
        isCancelled = true;
        setStatus(Downloader.STATUS_FAILED);
        //删除所有下载信息
        DownloadOperation.deleteRecord(mContext, getUrl());
        //删除下载好的文件
        String path = downloadOption.getSaveFilePath();
        String fileName = downloadOption.getFileName();
        if (!com.ttsea.jlibrary.utils.Utils.isEmpty(path)
                && !com.ttsea.jlibrary.utils.Utils.isEmpty(fileName)) {
            File file = new File(path, fileName);
            if (file.exists()) {
                file.delete();
            }
        }

        Message message = new Message();
        message.what = ON_DOWNLOAD_CANCEL;
        message.arg1 = reason;
        mHandler.sendMessage(message);

        JLog.d(TAG, "downloader has canceled, downloaderId:" + getId()
                + ", url:" + getUrl()
                + ", downloader status:" + getStatusStr(getStatus())
                + ", reason:" + getStatusStr(reason) + ", msg:" + msg);
    }

    @Override
    public void reDownload() {
        contentLength = 0;
        isCancelled = false;
        isPaused = false;
        setStatus(Downloader.STATUS_PENDING);
        mHandler.sendEmptyMessage(ON_PRE_DOWNLOAD);
        startNewDownloadTask();
    }

    /** 保存该下载器的信息和状态 */
    private void saveDownloaderStatus(int status, int reason) {
        if (threads == null) {
            return;
        }
        for (int i = 0; i < threads.size(); i++) {
            DownloadThread thread = threads.get(i);
            DownloadFileInfo info = thread.getDownloadInfo();
            info.setReason(reason);
            info.setStatus(status);
            info.setLast_modified_timestamp(String.valueOf(System.currentTimeMillis()));
            DownloadOperation.insertOrUpdate(mContext, thread.getDownloadInfo());
        }
    }

    /** 处理错误的 responeCode */
    private void handleErrorResponseCode(int responseCode) {
        String msg = "";
        switch (responseCode) {
            case 404:
                msg = getStatusStr(Downloader.ERROR_HTTP_FILE_NOT_FOUND);
                cancel(Downloader.ERROR_HTTP_FILE_NOT_FOUND, "responseCode:" + responseCode);
                break;

            default:
                msg = getStatusStr(Downloader.ERROR_UNHANDLED_HTTP_CODE);
                cancel(Downloader.ERROR_UNHANDLED_HTTP_CODE, "responseCode:" + responseCode);
                break;
        }
        JLog.e(TAG, "cancel download, responseCode:" + responseCode + ", msg:" + msg);
    }


    public String getStatusStr(int reason) {
        String resonStr;
        switch (reason) {
            case Downloader.PAUSED_WAITING_TO_RETRY:
                resonStr = "PAUSED_WAITING_TO_RETRY";
                break;
            case Downloader.PAUSED_WAITING_FOR_NETWORK:
                resonStr = "PAUSED_WAITING_FOR_NETWORK";
                break;
            case Downloader.PAUSED_QUEUED_FOR_WIFI:
                resonStr = "PAUSED_QUEUED_FOR_WIFI";
                break;
            case Downloader.PAUSED_HUMAN:
                resonStr = "PAUSED_HUMAN";
                break;
            case Downloader.PAUSED_UNKNOWN:
                resonStr = "PAUSED_UNKNOWN";
                break;

            case Downloader.ERROR_UNKNOWN:
                resonStr = "ERROR_UNKNOWN";
                break;
            case Downloader.ERROR_HTTP_TIMEOUT:
                resonStr = "ERROR_HTTP_TIMEOUT";
                break;
            case Downloader.ERROR_HTTP_FILE_NOT_FOUND:
                resonStr = "ERROR_HTTP_FILE_NOT_FOUND";
                break;
            case Downloader.ERROR_HTTP_DATA_ERROR:
                resonStr = "ERROR_HTTP_DATA_ERROR";
                break;
            case Downloader.ERROR_UNHANDLED_HTTP_CODE:
                resonStr = "ERROR_UNHANDLED_HTTP_CODE";
                break;
            case Downloader.ERROR_FILE_ALREADY_EXISTS:
                resonStr = "ERROR_FILE_ALREADY_EXISTS";
                break;
            case Downloader.ERROR_TOO_MANY_REDIRECTS:
                resonStr = "ERROR_TOO_MANY_REDIRECTS";
                break;
            case Downloader.ERROR_INSUFFICIENT_SPACE:
                resonStr = "ERROR_INSUFFICIENT_SPACE";
                break;
            case Downloader.ERROR_BLOCKED:
                resonStr = "ERROR_BLOCKED";
                break;

            case Downloader.STATUS_PENDING:
                resonStr = "STATUS_PENDING";
                break;
            case Downloader.STATUS_LINKING:
                resonStr = "STATUS_LINKING";
                break;
            case Downloader.STATUS_RUNNING:
                resonStr = "STATUS_RUNNING";
                break;
            case Downloader.STATUS_PAUSED:
                resonStr = "STATUS_PAUSED";
                break;
            case Downloader.STATUS_SUCCESSFUL:
                resonStr = "STATUS_SUCCESSFUL";
                break;
            case Downloader.STATUS_FAILED:
                resonStr = "STATUS_FAILED";
                break;

            default:
                resonStr = "unknown";
                break;
        }

        return resonStr;
    }

    private class DownloaderHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case ON_PRE_DOWNLOAD://准备下载前
                    if (onDownloadListener != null) {
                        onDownloadListener.onPreDownload(Downloader.this);
                    }
                    break;

                case ON_DOWNLOAD_LINKING://连接中...
                    if (onDownloadListener != null) {
                        onDownloadListener.onDownloadLinking(Downloader.this);
                    }
                    break;

                case ON_DOWNLOAD_START://开始下载
                    if (onDownloadListener != null) {
                        onDownloadListener.onDownloadStart(Downloader.this);
                    }
                    break;

                case ON_DOWNLOAD_PAUSE://暂停下载
                    if (onDownloadListener != null) {
                        onDownloadListener.onDownloadPause(Downloader.this, msg.arg1);
                    }
                    break;

                case ON_DOWNLOAD_CANCEL://取消下载
                    if (onDownloadListener != null) {
                        onDownloadListener.onDownloadCancel(Downloader.this, msg.arg1);
                    }
                    break;

                case ON_DOWNLOADING://下载中...
                    JLog.d(TAG, "on downloading...");
                    long byteSoFar = 0;
                    for (int i = 0; i < threads.size(); i++) {
                        byteSoFar = byteSoFar + threads.get(i).getDownloadInfo().getBytes_so_far();
                    }
                    speed = byteSoFar - totalByteSoFar;
                    totalByteSoFar = byteSoFar;
                    if (speed < 0) {
                        speed = 0;
                    }
                    //当全下载完后，需要设置Downloader的状态并保存其状态
                    if (contentLength <= totalByteSoFar) {
                        setStatus(STATUS_SUCCESSFUL);
                        saveDownloaderStatus(getStatus(), STATUS_SUCCESSFUL);
                    }
                    if (onDownloadListener != null) {
                        onDownloadListener.onDownloading(Downloader.this, contentLength, totalByteSoFar, speed);
                    }

                    mHandler.postDelayed(downloadingRunnable, DEFAULT_UPDATE_UI_PERIOD);
                    break;

                case ON_DOWNLOAD_COMPLETED://下载完成
                    long endTimestamp = System.currentTimeMillis();
                    JLog.d(TAG, "startTimestamp:" + startTimestamp + ", endTimestamp:" + endTimestamp
                            + ", spendTime:" + (endTimestamp - startTimestamp));
                    if (onDownloadListener != null) {
                        onDownloadListener.onComplete(Downloader.this);
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
