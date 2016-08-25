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
    private final String TAG = "Gallery.ImagePreviewActivity";

    private View llyTitleBar;
    private TextView tvTitleBarName;
    private Button btnLeft;
    private Button btnRight;

    private View llyBottomView;
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

        llyBottomView = findViewById(R.id.llyBottomView);
        llyCheck = findViewById(R.id.llyCheck);
        ivCheck = (ImageView) findViewById(R.id.ivCheck);
        viwePager = (ViewPagerFixed) findViewById(R.id.viwePager);

        llyTitleBar.setBackgroundColor(getColorById(R.color.photo_title_bar_bg));
        llyBottomView.setBackgroundColor(getColorById(R.color.translucentE0));

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

    private void onOkBtnClicked() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GalleryConstants.KEY_SELECTED_LIST, (Serializable) selectedList);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    private void onDeleteBtnClicked() {
        selectedList.remove(currentPosition);

        if (currentPosition >= selectedList.size() && selectedList.size() != 0) {
            currentPosition = selectedList.size() - 1;
        } else if (currentPosition != 0) {
            currentPosition--;
        }
        JLog.d(TAG, "removed currentPosition:" + currentPosition);
        adapter.notifyDataSetChanged();
        refreshTvIndex();

        if (selectedList.size() == 0) {
            onOkBtnClicked();
        }
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
            onOkBtnClicked();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnLeft || id == R.id.btnRight) {
            onOkBtnClicked();
        } else if (id == R.id.btnDelete) {
            onDeleteBtnClicked();
        } else if (id == R.id.btnSavePic) {
            toastMessage("下载图片");
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
