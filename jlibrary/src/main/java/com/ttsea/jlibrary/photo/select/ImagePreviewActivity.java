package com.ttsea.jlibrary.photo.select;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.ttsea.jlibrary.base.JBaseActivity;
import com.ttsea.jlibrary.common.imageloader.JImageLoader;
import com.ttsea.jlibrary.common.utils.DisplayUtils;
import com.ttsea.jlibrary.common.utils.JToast;
import com.ttsea.jlibrary.debug.JLog;
import com.ttsea.jlibrary.photo.gallery.PhotoView;
import com.ttsea.jlibrary.photo.gallery.ViewPagerFixed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends JBaseActivity implements View.OnClickListener,
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

    /** 使用静态常量来传输list，避免list太大导致传输失败 */
    private static List<ImageItem> imageList;
    /** 获取前一个activity传过来的position */
    private int currentPosition;
    /** 能选择的最大数目 */
    private int maxSize;

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jphoto_preview_main);// 切屏到主界面

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //imageList = (List<ImageItem>) bundle.getSerializable(ImageSelector.KEY_SELECTED_LIST);
        imageList = getImageList();
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
        btnRight.setBackgroundResource(R.drawable.jphoto_select_ok_btn_selector);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        llyCheck.setOnClickListener(this);
        viwePager.setOnPageChangeListener(this);

        if (imageList == null || imageList.size() == 0) {
            toastMessage(R.string.image_no_picture);
            this.finish();
            return;
        }
        if (currentPosition < 0) {
            toastMessage(R.string.photo_position_can_not_less_than_zero);
            this.finish();
            return;
        }

        adapter = new MyPageAdapter(imageList);
        viwePager.setAdapter(adapter);
        viwePager.setPageMargin(10);
        viwePager.setCurrentItem(currentPosition);

        ViewGroup.LayoutParams params = btnRight.getLayoutParams();
        int marginLR = DisplayUtils.dip2px(mActivity, 8);
        int marginTB = (marginLR * 2) / 3;
        if (params != null) {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = DisplayUtils.dip2px(mActivity, 30);
            if (params instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) params).setMargins(0, 0, marginLR, 0);
            }
            btnRight.setLayoutParams(params);
            btnRight.setPadding(marginLR, marginTB, marginLR, marginTB);
        }

        refreshTvIndex();
    }

    @Override
    protected void onDestroy() {
        destroyImageList();
        super.onDestroy();
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
        //筛选出被选择的图片
        List<ImageItem> list = new ArrayList<ImageItem>();
        for (int i = 0; i < imageList.size(); i++) {
            ImageItem item = imageList.get(i);
            if (item.isSelected()) {
                list.add(item);
            }
        }

        //点击的是完成
        if (list.size() == 0 && resultCode == Activity.RESULT_OK) {
            imageList.get(currentPosition).setSelected(true);
            list.add(imageList.get(currentPosition));
            refreshTvIndex();
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImageSelector.KEY_SELECTED_LIST, (Serializable) list);
        intent.putExtras(bundle);
        setResult(resultCode, intent);

        finish();
    }

    public void refreshTvIndex() {
        if (imageList.size() <= 1) {
            tvTitleBarName.setText("");
        } else {
            tvTitleBarName.setText((currentPosition + 1) + "/" + imageList.size());
        }
        ImageItem item = imageList.get(currentPosition);
        if (item != null) {
            if (item.isSelected()) {
                ivCheck.setImageResource(R.drawable.imageselector_select_checked);
            } else {
                ivCheck.setImageResource(R.drawable.imageselector_select_uncheck);
            }
        }

        int selectCount = getSelectCount();
        String txt = (getStringById(R.string.finish)) + "(" + selectCount + "/" + maxSize + ")";

        if (selectCount < 1) {
            txt = (getStringById(R.string.finish));
        }
        btnRight.setText(txt);
    }

    private int getSelectCount() {
        int count = 0;
        for (int i = 0; i < imageList.size(); i++) {
            if (imageList.get(i).isSelected()) {
                count++;
            }
        }
        return count;
    }

    private void selectOrUnselectImage(int position) {
        ImageItem item = imageList.get(position);
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
                JLog.e(TAG, "onLoadingFailed, reason:" + failReason);
                setLoadingViewVisibility(view, View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Drawable drawable) {
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

    public static void destroyImageList() {
        if (imageList != null)
            imageList.clear();
        imageList = null;
    }

    /** 使用静态常量来传输list，避免list太大导致传输失败 */
    public static void setImageList(List<ImageItem> items) {
        if (imageList == null) {
            imageList = new ArrayList<>();
        }
        imageList.clear();
        if (items != null) {
            imageList.addAll(items);
        }
    }

    public static List<ImageItem> getImageList() {
        if (imageList == null) {
            imageList = new ArrayList<>();
        }
        return imageList;
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

            View itemView = inflater.inflate(R.layout.jphoto_gallery_item, null);
            PhotoView pvImage = (PhotoView) itemView.findViewById(R.id.pvImage);
            pvImage.setBackgroundColor(0xff000000);

            ImageItem item = imageList.get(position);

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
