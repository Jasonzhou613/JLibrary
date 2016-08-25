package com.ttsea.jlibrary.photo.select;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.base.BaseFragmentActivity;
import com.ttsea.jlibrary.common.JImageLoader;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.common.JToast;
import com.ttsea.jlibrary.photo.crop.CropConstants;
import com.ttsea.jlibrary.photo.gallery.GalleryConstants;
import com.ttsea.jlibrary.photo.gallery.PhotoView;
import com.ttsea.jlibrary.photo.gallery.ViewPagerFixed;
import com.ttsea.jlibrary.utils.DisplayUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends BaseActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {
    private final String TAG = "Select.ImagePreviewActivity";

    private View llyTitleBar;
    private TextView tvTitleBarName;
    private Button btnLeft;
    private Button btnRight;

    private View llyCheck;
    private ImageView ivCheck;

    private ViewPagerFixed viwePager;
    private MyPageAdapter adapter;

    /** 获取前一个activity传过来的图片list */
    private List<ImageItem> selectedList;
    /** 获取前一个activity传过来的position */
    private int currentPosition;
    /** 能选择的最大数目 */
    private int maxSize;

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_preview_main);// 切屏到主界面

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        selectedList = (List<ImageItem>) bundle.getSerializable(ImageSelector.KEY_SELECTED_LIST);
        currentPosition = bundle.getInt(ImageSelector.KEY_SELECTED_POSITION, 0);
        maxSize = bundle.getInt(ImageSelector.KEY_MAX_SIZE, 0);

        llyTitleBar = findViewById(R.id.llyTitleBar);
        tvTitleBarName = (TextView) findViewById(R.id.tvTitleBarName);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);

        llyCheck = findViewById(R.id.llyCheck);
        ivCheck = (ImageView) findViewById(R.id.ivCheck);
        viwePager = (ViewPagerFixed) findViewById(R.id.viwePager);

        llyTitleBar.setBackgroundColor(getColorById(R.color.photo_title_bar_bg));
        btnRight.setBackgroundResource(R.drawable.photo_select_ok_btn_selector);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        llyCheck.setOnClickListener(this);
        viwePager.setOnPageChangeListener(this);

        if (selectedList == null || selectedList.size() == 0) {
            toastMessage(R.string.image_no_picture);
            this.finish();
            return;
        }
        if (currentPosition < 0) {
            toastMessage(R.string.photo_position_can_not_less_than_zero);
            this.finish();
            return;
        }

        adapter = new MyPageAdapter(selectedList);
        viwePager.setAdapter(adapter);
        viwePager.setPageMargin(10);
        viwePager.setCurrentItem(currentPosition);

        ViewGroup.LayoutParams params = btnRight.getLayoutParams();
        int marginLR = DisplayUtils.dip2px(mActivity, 8);
        int marginTB = (marginLR * 2) / 3;
        if (params != null) {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (params instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) params).setMargins(0, 0, marginLR, 0);
            }
            btnRight.setLayoutParams(params);
            btnRight.setPadding(marginLR, marginTB, marginLR, marginTB);
        }

        refreshTvIndex();
    }

    private void setLoadingViewVisibility(View childView, int visibility) {
        View parent;
        if (childView == null) {
            return;
        }
        ViewParent viewParent = childView.getParent();
        if (viewParent instanceof FrameLayout) {
            parent = (FrameLayout) viewParent;
        } else {
            parent = childView.getRootView();
        }

        View v = parent.findViewById(R.id.pbProgress);
        if (v != null) {
            v.setVisibility(visibility);
        }
    }

    private void onOkBtnClicked(int resultCode) {
        //去除未选择的
        int position = 0;
        while (position < selectedList.size()) {
            ImageItem item = selectedList.get(position);
            if (!item.isSelected()) {
                selectedList.remove(item);
                position = 0;
                continue;
            }
            position++;
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, (Serializable) selectedList);
        intent.putExtras(bundle);
        setResult(resultCode, intent);

        finish();
    }

    public void refreshTvIndex() {
        if (selectedList.size() <= 1) {
            tvTitleBarName.setText("");
        } else {
            tvTitleBarName.setText((currentPosition + 1) + "/" + selectedList.size());
        }
        ImageItem item = selectedList.get(currentPosition);
        if (item != null) {
            if (item.isSelected()) {
                ivCheck.setImageResource(R.drawable.imageselector_select_checked);
            } else {
                ivCheck.setImageResource(R.drawable.imageselector_select_uncheck);
            }
        }

        int selectCount = getSelectCount();
        String txt = (getStringById(R.string.finish)) + "(" + selectCount + "/" + maxSize + ")";
        btnRight.setEnabled(true);

        if (selectCount < 1) {
            txt = (getStringById(R.string.finish));
            btnRight.setEnabled(false);
        }
        btnRight.setText(txt);
    }

    private int getSelectCount() {
        int count = 0;
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).isSelected()) {
                count++;
            }
        }
        return count;
    }

    private void selectOrUnselectImage(int position) {
        ImageItem item = selectedList.get(position);
        if (getSelectCount() >= maxSize && !item.isSelected()) {
            JToast.makeTextCenter(mActivity, getStringById(R.string.image_msg_amount_limit));
            return;
        }
        item.setSelected(!item.isSelected());
        refreshTvIndex();
    }

    private void displayImage(ImageItem item, ImageView imageView) {
        JImageLoader.ImageLoadingListener listener = new JImageLoader.ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                setLoadingViewVisibility(view, View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, String failReason) {
                setLoadingViewVisibility(view, View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                setLoadingViewVisibility(view, View.GONE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                setLoadingViewVisibility(view, View.GONE);
            }
        };

        JLog.d(TAG, "childrenCount:" + viwePager.getChildCount());

        if (item.isNetWorkImage()) {
            JImageLoader.getInstance().displayImageForGallery(mActivity, item.getPath(), imageView, listener);
        } else {
            JImageLoader.getInstance().displayImageForGallery(mActivity, "file://" + item.getPath(), imageView, listener);
        }
    }

    /** 监听返回按钮 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onOkBtnClicked(Activity.RESULT_CANCELED);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnLeft) {
            onOkBtnClicked(Activity.RESULT_CANCELED);
        } else if (id == R.id.btnRight) {
            onOkBtnClicked(Activity.RESULT_OK);
        } else if (id == R.id.llyCheck) {
            selectOrUnselectImage(currentPosition);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        currentPosition = arg0;
        refreshTvIndex();
    }

    private class MyPageAdapter extends PagerAdapter {
        private List<ImageItem> items;

        public MyPageAdapter(List<ImageItem> listViews) {
            this.items = listViews;
        }

        public int getCount() {
            return items.size();
        }

        public void destroyItem(View container, int position, Object object) {
            JLog.d(TAG, "destroyItem, position:" + position);
            if (container instanceof ViewPagerFixed && object instanceof View) {
                ((ViewPagerFixed) container).removeView(((View) object));
            }
        }

        public Object instantiateItem(ViewGroup container, int position) {
            JLog.d(TAG, "instantiateItem, position:" + position);

            LayoutInflater inflater = LayoutInflater.from(mActivity);

            View itemView = inflater.inflate(R.layout.photo_gallery_item, null);
            PhotoView pvImage = (PhotoView) itemView.findViewById(R.id.pvImage);
            pvImage.setBackgroundColor(0xff000000);

            ImageItem item = selectedList.get(position);

            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            itemView.setTag(item.getPath());
            displayImage(item, pvImage);

            container.addView(itemView, 0);

            return itemView;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
