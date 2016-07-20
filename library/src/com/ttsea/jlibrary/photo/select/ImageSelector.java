package com.ttsea.jlibrary.photo.select;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class ImageSelector {
    public static final int TAKE_PHOTO_BY_GALLERY = 0x110;

    public static final String KEY_SELECTED_LIST = "selected_list";

    private static ImageConfig mImageConfig;

    public static ImageConfig getImageConfig() {
        return mImageConfig;
    }

    public static void open(Activity activity, ImageConfig config) {
        if (config == null) {
            return;
        }
        mImageConfig = config;

        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        activity.startActivityForResult(intent, mImageConfig.getRequestCode());
    }

    public static void open(Fragment fragment, ImageConfig config) {
        if (config == null || fragment.getActivity() == null) {
            return;
        }
        mImageConfig = config;

        Intent intent = new Intent(fragment.getActivity(), ImageSelectorActivity.class);
        fragment.startActivityForResult(intent, mImageConfig.getRequestCode());
    }
}
