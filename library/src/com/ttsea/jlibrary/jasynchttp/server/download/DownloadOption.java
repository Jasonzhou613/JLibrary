package com.ttsea.jlibrary.jasynchttp.server.download;

import android.content.Context;

import com.ttsea.jlibrary.utils.CacheDirUtils;


/**
 * 下载选项，使用建造者模式 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/5/19 10:16 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/19 10:16
 */
public class DownloadOption {
    private Builder builder;

    public DownloadOption(Builder builder) {
        this.builder = builder;
    }

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public int getThreadPool() {
        return builder.threadPool;
    }

    public int getReTryCount() {
        return builder.reTryCount;
    }

    public String getSaveFilePath() {
        return builder.saveFilePath;
    }

    public String getFileName() {
        return builder.fileName;
    }

    public SaveFileMode getSaveFileMode() {
        return builder.saveFileMode;
    }

    public HttpOption getHttpOption() {
        return builder.httpOption;
    }

    public static class Builder {
        private Context mContext;

        private final static int DEFAULT_THREAD_POOL = 2;
        private final static int DEFAULT_RETRY_COUNT = 2;

        //同时采用多少个线程来下载该文件
        private int threadPool;
        //当请求失败时，重试数次
        private int reTryCount;
        //文件保存地址
        private String saveFilePath;
        //文件名称
        private String fileName;
        //文件保存方式
        private SaveFileMode saveFileMode;
        //http请求选项
        private HttpOption httpOption;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setThreadPool(int threadPool) {
            this.threadPool = threadPool;
            return this;
        }

        public Builder setReTryCount(int reTryCount) {
            this.reTryCount = reTryCount;
            return this;
        }

        public Builder setSaveFilePath(String saveFilePath) {
            this.saveFilePath = saveFilePath;
            return this;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Builder setSaveFileMode(SaveFileMode saveFileMode) {
            this.saveFileMode = saveFileMode;
            return this;
        }

        public Builder setHttpOption(HttpOption httpOption) {
            this.httpOption = httpOption;
            return this;
        }

        public DownloadOption build() {

            if (threadPool < 1) {
                threadPool = DEFAULT_THREAD_POOL;
            }

            if (reTryCount < 1) {
                reTryCount = DEFAULT_RETRY_COUNT;
            }

            if (com.ttsea.jlibrary.utils.Utils.isEmpty(saveFilePath)) {
                saveFilePath = CacheDirUtils.getSdDataDir(mContext);
            }

            if (saveFileMode == null) {
                saveFileMode = SaveFileMode.OVERRIDE;
            }

            if (httpOption == null) {
                httpOption = new HttpOption();
            }

            return new DownloadOption(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "threadPool=" + threadPool +
                    ", reTryCount=" + reTryCount +
                    ", saveFilePath='" + saveFilePath + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", saveFileMode=" + saveFileMode +
                    ", httpOption=" + httpOption +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DownloadOption{" +
                "builder=" + builder.toString() +
                '}';
    }
}
