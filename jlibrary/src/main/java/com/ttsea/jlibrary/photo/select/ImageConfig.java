package com.ttsea.jlibrary.photo.select;

import android.content.Context;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.photo.crop.CropView;
import com.ttsea.jlibrary.utils.CacheDirUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public int getTitleBgColorRes() {
        return builder.titleBgColorRes;
    }

    public int getTitleTextColorRes() {
        return builder.titleNameTextColorRes;
    }

    public int getTitleSubmitTextColorRes() {
        return builder.titleOKTextColorRes;
    }

    public int getRequestCode() {
        return builder.requestCode;
    }

    public List<ImageItem> getPathList() {
        return builder.pathList;
    }

    public boolean isCrop() {
        return builder.crop;
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

    public int getCropModel() {
        return builder.cropModel;
    }

    public boolean isReturnData() {
        return builder.returnData;
    }

    public boolean isFixedAspectRatio() {
        return builder.fixedAspectRatio;
    }

    public boolean isCanMoveFrame() {
        return builder.canMoveFrame;
    }

    public boolean isCanDragFrameConner() {
        return builder.canDragFrameConner;
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

        private int titleBgColorRes = -1;
        private int titleNameTextColorRes = -1;
        private int titleOKTextColorRes = -1;

        private int requestCode = ImageSelector.TAKE_PHOTO_BY_GALLERY;

        private List<ImageItem> pathList;

        //剪切图片常量
        private boolean crop = false;
        private String outPutPath;
        private String imageSuffix;
        private int aspectX;
        private int aspectY;
        private int outputX;
        private int outputY;
        private int cropModel = CropView.CROP_MODE_RECTANGLE;
        private boolean returnData = false;
        private boolean fixedAspectRatio = true;
        private boolean canMoveFrame = false;
        private boolean canDragFrameConner = false;

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

        public Builder setTitleBgColorRes(int titleBgColorRes) {
            this.titleBgColorRes = titleBgColorRes;
            return this;
        }

        public Builder setTitleNameTextColorRes(int titleNameTextColorRes) {
            this.titleNameTextColorRes = titleNameTextColorRes;
            return this;
        }

        public Builder setTitleOKTextColorRes(int titleOKTextColorRes) {
            this.titleOKTextColorRes = titleOKTextColorRes;
            return this;
        }

        /** 设置requestCode，以便onActivityResult接收 */
        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder setPathList(List<ImageItem> pathList) {
            if (this.pathList == null) {
                this.pathList = new ArrayList<ImageItem>();
            }
            this.pathList.clear();
            if (pathList != null) {
                this.pathList.addAll(pathList);
            }
            return this;
        }

        /** 设置选择完图片或者拍照后是否需要剪切图片,true为需要，false为不需要，只有当mutiSelect为false时生效 */
        public Builder setCrop(boolean crop) {
            this.crop = crop;
            return this;
        }

        /** 设置剪切图片后的输出路径，父路径 */
        public Builder setOutPutPath(String outPutPath) {
            this.outPutPath = outPutPath;
            return this;
        }

        /** 设置剪切图片后保存和拍照保存后的后缀名，默认为.jpg */
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

        /**
         * 设置剪切模式，{@link com.ttsea.jlibrary.photo.crop.CropView#CROP_MODE_RECTANGLE}
         */
        public Builder setCropModel(int cropModel) {
            this.cropModel = cropModel;
            return this;
        }

        /** 设置是否返回data */
        public Builder setReturnData(boolean returnData) {
            this.returnData = returnData;
            return this;
        }

        /** 设置剪切时是否保持长宽比例，默认为false */
        public Builder setFixedAspectRatio(boolean fixedAspectRatio) {
            this.fixedAspectRatio = fixedAspectRatio;
            return this;
        }

        /** 按住剪切框中间，是否可以拖动整个剪切框, 默认为false */
        public Builder setCanMoveFrame(boolean canMoveFrame) {
            this.canMoveFrame = canMoveFrame;
            return this;
        }

        /** 按住剪切框四个角，是否可以拖动剪切框的四个角 */
        public Builder setCanDragFrameConner(boolean canDragFrameConner) {
            this.canDragFrameConner = canDragFrameConner;
            return this;
        }

        public ImageConfig build() {
            if (this.pathList == null) {
                this.pathList = new ArrayList<ImageItem>();
            }
            if (outPutPath == null) {
                outPutPath = CacheDirUtils.getTempDir(mContext);
            }
            if (titleBgColorRes == -1) {
                titleBgColorRes = R.color.photo_title_bar_bg;
            }
            if (titleNameTextColorRes == -1) {
                titleNameTextColorRes = R.color.white;
            }
            if (titleOKTextColorRes == -1) {
                titleOKTextColorRes = R.color.white;
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
                    ", titleBgColorRes=" + titleBgColorRes +
                    ", titleNameTextColorRes=" + titleNameTextColorRes +
                    ", titleOKTextColorRes=" + titleOKTextColorRes +
                    ", requestCode=" + requestCode +
                    ", pathList=" + pathList +
                    ", crop=" + crop +
                    ", outPutPath='" + outPutPath + '\'' +
                    ", imageSuffix='" + imageSuffix + '\'' +
                    ", aspectX=" + aspectX +
                    ", aspectY=" + aspectY +
                    ", outputX=" + outputX +
                    ", outputY=" + outputY +
                    ", cropModel=" + cropModel +
                    ", returnData=" + returnData +
                    ", fixedAspectRatio=" + fixedAspectRatio +
                    ", canMoveFrame=" + canMoveFrame +
                    ", canDragFrameConner=" + canDragFrameConner +
                    '}';
        }
    }
}
