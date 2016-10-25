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

    public long getExpiredTime() {
        return builder.expiredTimeMillis;
    }

    public int getSaveFileMode() {
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
        private int saveFileMode = -1;
        //本地文件过期时间(单位：毫秒)默认为2天
        private long expiredTimeMillis = 1000 * 60 * 60 * 24 * 2;
        //http请求选项
        private HttpOption httpOption;

        public Builder(Context context) {
            this.mContext = context;
        }

        /** 设置同时采用多少个线程来下载该文件,默认为2 */
        public Builder setThreadPool(int threadPool) {
            this.threadPool = threadPool;
            return this;
        }

        /** 设置当请求失败时，再重试数次，默认为2 */
        public Builder setReTryCount(int reTryCount) {
            this.reTryCount = reTryCount;
            return this;
        }

        /** 设置文件保存地址，默认为{@link CacheDirUtils#getSdDataDir(Context)} */
        public Builder setSaveFilePath(String saveFilePath) {
            this.saveFilePath = saveFilePath;
            return this;
        }

        /** 设置文件名称，若不设置 则会尝试通过下载地址来获取文件名 */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * 设置文件保存方式，有三种方式：{@link SaveFileMode#NONACTION},{@link SaveFileMode#OVERRIDE}
         * ,{@link SaveFileMode#RENAME}，默认为:OVERRIDE
         */
        public Builder setSaveFileMode(int saveFileMode) {
            this.saveFileMode = saveFileMode;
            return this;
        }

        /** 设置下载文件的过期时间（单位：毫秒），默认为2天 */
        public Builder setExpiredTime(long expiredTimeMillis) {
            this.expiredTimeMillis = expiredTimeMillis;
            return this;
        }

        /** 设置Http请求选项 */
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

            if (saveFileMode < 0) {
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
                    ", expiredTimeMillis=" + expiredTimeMillis +
                    ", httpOption=" + httpOption.toString() +
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
