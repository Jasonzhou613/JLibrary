package com.ttsea.jlibrary.photo.select;

import android.content.Context;

import com.ttsea.jlibrary.utils.CacheDirUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageConfig implements Serializable {
    private Builder builder;

    private ImageConfig(Builder builder) {
        this.builder = builder;
    }

    public Builder getBuilder() {
        return builder;
    }

    public boolean isMutiSelect() {
        return builder.mutiSelect;
    }

    public boolean isShowCamera() {
        return builder.showCamera;
    }

    public int getMaxSize() {
        return builder.maxSize;
    }

    public int getTitleBgColor() {
        return builder.titleBgColor;
    }

    public int getTitleTextColor() {
        return builder.titleNameTextColor;
    }

    public int getTitleSubmitTextColor() {
        return builder.titleOKTextColor;
    }

    public int getSteepToolBarColor() {
        return builder.steepToolBarColor;
    }

    public int getRequestCode() {
        return builder.requestCode;
    }

    public ArrayList<String> getPathList() {
        return builder.pathList;
    }

    public boolean isCrop() {
        return builder.crop;
    }

    public String getImagePath() {
        return builder.imagePath;
    }

    public String getOutPutPath() {
        return builder.outPutPath;
    }

    public String getImageSuffix() {
        return builder.imageSuffix;
    }

    public int getAspectX() {
        return builder.aspectX;
    }

    public int getAspectY() {
        return builder.aspectY;
    }

    public int getOutputX() {
        return builder.outputX;
    }

    public int getOutputY() {
        return builder.outputY;
    }

    public boolean isReturnData() {
        return builder.returnData;
    }

    public boolean isFixedAspectRatio() {
        return builder.fixedAspectRatio;
    }

    @Override
    public String toString() {
        return "ImageConfig{" +
                "builder=" + builder.toString() +
                '}';
    }

    public static class Builder implements Serializable {
        private Context mContext;

        private boolean mutiSelect = true;
        private boolean showCamera = true;
        private int maxSize = 9;

        private int titleBgColor = 0xFF000000;
        private int titleNameTextColor = 0xFFFFFFFF;
        private int titleOKTextColor = 0xFFFFFFFF;
        private int steepToolBarColor = 0xFF000000;

        private int requestCode = ImageSelector.TAKE_PHOTO_BY_GALLERY;

        private ArrayList<String> pathList;

        //剪切图片常量
        private boolean crop = false;
        private String imagePath;
        private String outPutPath;
        private String imageSuffix;
        private int aspectX;
        private int aspectY;
        private int outputX;
        private int outputY;
        private boolean returnData = false;
        private boolean fixedAspectRatio = true;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setMutiSelect(boolean mutiSelect) {
            this.mutiSelect = mutiSelect;
            return this;
        }

        public Builder setShowCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        public Builder setMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder setTitleBgColor(int titleBgColor) {
            this.titleBgColor = titleBgColor;
            return this;
        }

        public Builder setTitleNameTextColor(int titleNameTextColor) {
            this.titleNameTextColor = titleNameTextColor;
            return this;
        }

        public Builder setTitleOKTextColor(int titleOKTextColor) {
            this.titleOKTextColor = titleOKTextColor;
            return this;
        }

        public Builder setSteepToolBarColor(int steepToolBarColor) {
            this.steepToolBarColor = steepToolBarColor;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder setPathList(ArrayList<String> pathList) {
            this.pathList = pathList;
            return this;
        }

        public Builder setCrop(boolean crop) {
            this.crop = crop;
            return this;
        }

        public Builder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder setOutPutPath(String outPutPath) {
            this.outPutPath = outPutPath;
            return this;
        }

        public Builder setImageSuffix(String imageSuffix) {
            this.imageSuffix = imageSuffix;
            return this;
        }

        public Builder setAspectX(int aspectX) {
            this.aspectX = aspectX;
            return this;
        }

        public Builder setAspectY(int aspectY) {
            this.aspectY = aspectY;
            return this;
        }

        public Builder setOutputX(int outputX) {
            this.outputX = outputX;
            return this;
        }

        public Builder setOutputY(int outputY) {
            this.outputY = outputY;
            return this;
        }

        public Builder setReturnData(boolean returnData) {
            this.returnData = returnData;
            return this;
        }

        public Builder setFixedAspectRatio(boolean fixedAspectRatio) {
            this.fixedAspectRatio = fixedAspectRatio;
            return this;
        }

        public ImageConfig build() {
            if (this.pathList == null) {
                this.pathList = new ArrayList<String>();
            }
            if (outPutPath == null) {
                outPutPath = CacheDirUtils.getTempDir(mContext);
            }
            return new ImageConfig(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "mContext=" + mContext +
                    ", mutiSelect=" + mutiSelect +
                    ", showCamera=" + showCamera +
                    ", maxSize=" + maxSize +
                    ", titleBgColor=" + titleBgColor +
                    ", titleNameTextColor=" + titleNameTextColor +
                    ", titleOKTextColor=" + titleOKTextColor +
                    ", steepToolBarColor=" + steepToolBarColor +
                    ", requestCode=" + requestCode +
                    ", pathList=" + pathList +
                    ", crop=" + crop +
                    ", imagePath='" + imagePath + '\'' +
                    ", outPutPath='" + outPutPath + '\'' +
                    ", imageSuffix='" + imageSuffix + '\'' +
                    ", aspectX=" + aspectX +
                    ", aspectY=" + aspectY +
                    ", outputX=" + outputX +
                    ", outputY=" + outputY +
                    ", returnData=" + returnData +
                    ", fixedAspectRatio=" + fixedAspectRatio +
                    '}';
        }
    }
}
