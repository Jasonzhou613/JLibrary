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

        /** 设置是否为多选，true为多选，false为单选 */
        public Builder setMutiSelect(boolean mutiSelect) {
            this.mutiSelect = mutiSelect;
            return this;
        }

        /** 是否显示拍照选项 */
        public Builder setShowCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        /** 设置最大选择数目，mutiSelect为true时生效 */
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

        /** 设置requestCode，以便onActivityResult接收 */
        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder setPathList(ArrayList<String> pathList) {
            this.pathList = pathList;
            return this;
        }

        /** 设置选择完图片或者拍照后是否需要剪切图片,true为需要，false为不需要，只有当mutiSelect为false时生效 */
        public Builder setCrop(boolean crop) {
            this.crop = crop;
            return this;
        }

        public Builder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        /** 设置剪切图片后的输出路径，父路径 */
        public Builder setOutPutPath(String outPutPath) {
            this.outPutPath = outPutPath;
            return this;
        }

        /** 设置剪切图片后保存图片的路径，父路径 */
        public Builder setImageSuffix(String imageSuffix) {
            this.imageSuffix = imageSuffix;
            return this;
        }

        /** 设置x比例 */
        public Builder setAspectX(int aspectX) {
            this.aspectX = aspectX;
            return this;
        }

        /** 设置y比例 */
        public Builder setAspectY(int aspectY) {
            this.aspectY = aspectY;
            return this;
        }

        /** 设置x输出长度 */
        public Builder setOutputX(int outputX) {
            this.outputX = outputX;
            return this;
        }

        /** 设置y输出长度 */
        public Builder setOutputY(int outputY) {
            this.outputY = outputY;
            return this;
        }

        /** 设置是否返回data */
        public Builder setReturnData(boolean returnData) {
            this.returnData = returnData;
            return this;
        }

        /** 设置剪切时是否保持长宽比例 */
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
