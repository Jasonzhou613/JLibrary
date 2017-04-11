package com.ttsea.jlibrary.photo.select;

import com.ttsea.jlibrary.photo.crop.CropView;

import java.io.Serializable;

/**
 * 剪切图片配置 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 17:37 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class CropConfig implements Serializable {
    private Builder builder;

    private CropConfig(Builder builder) {
        this.builder = builder;
    }

    public Builder getBuilder() {
        return builder;
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
        //剪切图片常量
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

        /** 设置剪切图片后的输出路径 */
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

        public CropConfig build() {
            if (imageSuffix == null) {
                imageSuffix = ".jpg";
            }
            if (aspectX == 0 && aspectY == 0) {
                aspectX = aspectY = 1;
            }

            if (outputX == 0 && outputY == 0) {
                outputX = outputY = 250;
            }

            return new CropConfig(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "outPutPath='" + outPutPath + '\'' +
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
