package com.ttsea.jlibrary.common.bitmap;

import android.graphics.Bitmap;

/**
 * 图片压缩选项
 */
public class BitmapOption {
    private BitmapOption.Builder builder;

    private BitmapOption(BitmapOption.Builder builder) {
        this.builder = builder;
    }

    /**
     * see {@link BitmapOption.Builder#maxHeight}
     */
    public int getMaxHeight() {
        return builder.getMaxHeight();
    }

    /**
     * see {@link BitmapOption.Builder#maxWidth}
     */
    public int getMaxWidth() {
        return builder.getMaxWidth();
    }


    /**
     * see {@link BitmapOption.Builder#maxSizeInKB}
     */
    public long getMaxSizeInKB() {
        return builder.getMaxSizeInKB();
    }

    /**
     * see {@link BitmapOption.Builder#config}
     */
    public Bitmap.Config getConfig() {
        return builder.getConfig();
    }

    /**
     * see {@link BitmapOption.Builder#lockRatio}
     */
    public boolean isLockRatio() {
        return builder.isLockRatio();
    }

    public static class Builder {
        /** 压缩后最大的宽度，<=0的时候表示不压缩宽度，默认为：0 */
        private int maxHeight;
        /** 压缩后最大的高度，<=0的时候表示不压缩高度，默认为：0 */
        private int maxWidth;
        /** 压缩后最大的大小，<=0的时候表示不压缩大小，默认为：0 */
        private long maxSizeInKB;

        /** 压缩格式，默认为{@link Bitmap.Config#ARGB_8888} */
        private Bitmap.Config config = Bitmap.Config.ARGB_8888;
        /** 压缩长宽的时候，是否锁定比例，默认：true */
        private boolean lockRatio = true;

        /**
         * see {@link #maxHeight}
         */
        public int getMaxHeight() {
            return maxHeight;
        }

        /**
         * see {@link #maxHeight}
         *
         * @param maxHeight
         */
        public BitmapOption.Builder setMaxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        /**
         * see {@link #maxWidth}
         */
        public int getMaxWidth() {
            return maxWidth;
        }

        /**
         * see {@link #maxWidth}
         *
         * @param maxWidth
         */
        public BitmapOption.Builder setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        /**
         * see {@link #maxSizeInKB}
         */
        public long getMaxSizeInKB() {
            return maxSizeInKB;
        }

        /**
         * see {@link #maxSizeInKB}
         *
         * @param maxSizeInKB
         */
        public BitmapOption.Builder setMaxSizeInKB(long maxSizeInKB) {
            this.maxSizeInKB = maxSizeInKB;
            return this;
        }

        /**
         * see {@link #config}
         */
        public Bitmap.Config getConfig() {
            return config;
        }

        /**
         * see {@link #config}
         *
         * @param config
         */
        public BitmapOption.Builder setConfig(Bitmap.Config config) {
            this.config = config;
            return this;
        }

        /**
         * see {@link #lockRatio}
         */
        public boolean isLockRatio() {
            return lockRatio;
        }

        /**
         * see {@link #lockRatio}
         *
         * @param lockRatio
         */
        public BitmapOption.Builder setLockRatio(boolean lockRatio) {
            this.lockRatio = lockRatio;
            return this;
        }

        public BitmapOption build() {
            return new BitmapOption(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "maxHeight=" + maxHeight +
                    ", maxWidth=" + maxWidth +
                    ", maxSizeInKB=" + maxSizeInKB +
                    ", config=" + config +
                    ", lockRatio=" + lockRatio +
                    '}';
        }
    }
}
