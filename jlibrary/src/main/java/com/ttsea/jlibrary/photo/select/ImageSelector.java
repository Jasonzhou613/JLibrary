package com.ttsea.jlibrary.photo.select;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ImageSelector {
    public static final int TAKE_PHOTO_BY_GALLERY = 0x110;

    public static final String KEY_SELECTED_LIST = "selected_list";
    public static final String KEY_SELECTED_POSITION = "selected_position";
    public static final String KEY_MAX_SIZE = "max_size";


    /**
     * 开始选择图片
     *
     * @param activity     Activity
     * @param selectConfig 选择图片相关配置
     * @param cropConfig   剪切图片配置，如果是多选，则cropConfig可以不用带或者可以为null
     */
    public static void open(Activity activity, SelectConfig selectConfig, CropConfig cropConfig) {
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectConfig", selectConfig);
        bundle.putSerializable("cropConfig", cropConfig);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, selectConfig.getRequestCode());
    }

    /**
     * 开始选择图片
     *
     * @param fragment     Fragment
     * @param selectConfig 选择图片相关配置
     * @param cropConfig   剪切图片配置，如果是多选，则cropConfig可以不用带或者可以为null
     */
    public static void open(Fragment fragment, SelectConfig selectConfig, CropConfig cropConfig) {
        Intent intent = new Intent(fragment.getActivity(), ImageSelectorActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectConfig", selectConfig);
        bundle.putSerializable("cropConfig", cropConfig);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent, selectConfig.getRequestCode());
    }
}
