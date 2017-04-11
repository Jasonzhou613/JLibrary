package com.ttsea.jlibrary.photo.select;

import com.ttsea.jlibrary.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 选择图片配置 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 17:37 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class SelectConfig implements Serializable {
    private Builder builder;

    private SelectConfig(Builder builder) {
        this.builder = builder;
    }

    public Builder getBuilder() {
        return builder;
    }

    public boolean isMultiSelect() {
        return builder.multiSelect;
    }

    public boolean isCrop() {
        return builder.crop;
    }

    public boolean isShowCamera() {
        return builder.showCamera;
    }

    public int getMaxSize() {
        return builder.maxSize;
    }

    public String getOutPutPath() {
        return builder.outPutPath;
    }

    public String getImageSuffix() {
        return builder.imageSuffix;
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

    public ArrayList<ImageItem> getPathList() {
        return builder.pathList;
    }

    @Override
    public String toString() {
        return "ImageConfig{" +
                "builder=" + builder.toString() +
                '}';
    }

    public static class Builder implements Serializable {
        private boolean multiSelect = true;
        private boolean showCamera = true;
        private int maxSize = 9;
        //是否剪切图片，只有在单选情况下才生效
        private boolean crop = false;
        //拍照后，照片的存储路径
        private String outPutPath;
        //拍照后，保存照片的后缀名
        private String imageSuffix;

        private int titleBgColorRes = -1;
        private int titleNameTextColorRes = -1;
        private int titleOKTextColorRes = -1;
        private int requestCode = ImageSelector.TAKE_PHOTO_BY_GALLERY;

        private ArrayList<ImageItem> pathList;

        /** 设置是否为多选，true为多选，false为单选 */
        public Builder setMultiSelect(boolean multiSelect) {
            this.multiSelect = multiSelect;
            return this;
        }

        /** 是否显示拍照选项 */
        public Builder setShowCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        /** 设置最大选择数目，multiSelect为true时生效 */
        public Builder setMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        /** 设置选择完图片或者拍照后是否需要剪切图片,true为需要，false为不需要，只有当multiSelect为false时生效 */
        public Builder setCrop(boolean crop) {
            this.crop = crop;
            return this;
        }

        /**
         * 设置拍照后的输出路径，建议不要保存到app的私有路径，因为调用系统相机拍照后保存到app的私有路径中，
         * 会有权限问题，从而导致保存不成功
         */
        public Builder setOutPutPath(String outPutPath) {
            this.outPutPath = outPutPath;
            return this;
        }

        /** 设置拍照保存图片的后缀名，默认为.jpg */
        public Builder setImageSuffix(String imageSuffix) {
            this.imageSuffix = imageSuffix;
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

        public Builder setPathList(ArrayList<ImageItem> pathList) {
            if (this.pathList == null) {
                this.pathList = new ArrayList<ImageItem>();
            }
            this.pathList.clear();

            if (pathList != null) {
                this.pathList.addAll(pathList);
            }
            return this;
        }

        public SelectConfig build() {
            if (this.pathList == null) {
                this.pathList = new ArrayList<ImageItem>();
            }

            if (imageSuffix == null) {
                imageSuffix = ".jpg";
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

            return new SelectConfig(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "multiSelect=" + multiSelect +
                    ", showCamera=" + showCamera +
                    ", maxSize=" + maxSize +
                    ", crop=" + crop +
                    ", outPutPath='" + outPutPath + '\'' +
                    ", imageSuffix='" + imageSuffix + '\'' +
                    ", titleBgColorRes=" + titleBgColorRes +
                    ", titleNameTextColorRes=" + titleNameTextColorRes +
                    ", titleOKTextColorRes=" + titleOKTextColorRes +
                    ", requestCode=" + requestCode +
                    ", pathList=" + pathList +
                    '}';
        }
    }
}
