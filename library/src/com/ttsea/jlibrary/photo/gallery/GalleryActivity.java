package com.ttsea.jlibrary.photo.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.common.JImageLoader;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.photo.select.ImageItem;

import java.io.Serializable;
import java.util.List;

/**
 * 浏览被选择了的照片<br/>
 * 可以传入的参数有：<br/>
 * selected_list: 被选择了的照片列表<br/>
 * selected_position: 从哪个位置开始查看，从0开始，不能小于0，默认为0<br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2015.09.10 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2015.10.23
 */
public class GalleryActivity extends BaseActivity implements OnClickListener,
        OnPageChangeListener {
    private final String TAG = "Gallery.GalleryActivity";

    private View llyTitleBar;
    private TextView tvTitleBarName;
    private Button btnLeft;
    private Button btnRight;

    private View llyBottomView;
    private Button btnUnClockwiseRotation;
    private Button btnClockwiseRotation;

    private ViewPagerFixed viwePager;
    private MyPageAdapter adapter;

    /** 获取前一个activity传过来的图片list */
    private List<ImageItem> selectedList;
    /** 获取前一个activity传过来的position */
    private int currentPosition;
    /** 是否可保存图片, 默认为false */
    private boolean canSave = false;
    /** 是否可旋转图片, 默认为true */
    private boolean canRotate = true;

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_gallery_main);// 切屏到主界面

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        selectedList = (List<ImageItem>) bundle.getSerializable(GalleryConstants.KEY_SELECTED_LIST);
        currentPosition = bundle.getInt(GalleryConstants.KEY_SELECTED_POSITION, 0);
        canSave = bundle.getBoolean(GalleryConstants.KEY_CAN_SAVE, false);
        canRotate = bundle.getBoolean(GalleryConstants.KEY_CAN_ROTATE, true);

        llyTitleBar = findViewById(R.id.llyTitleBar);
        tvTitleBarName = (TextView) findViewById(R.id.tvTitleBarName);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);

        viwePager = (ViewPagerFixed) findViewById(R.id.viwePager);
        llyBottomView = findViewById(R.id.llyBottomView);
        btnUnClockwiseRotation = (Button) findViewById(R.id.btnUnClockwiseRotation);
        btnClockwiseRotation = (Button) findViewById(R.id.btnClockwiseRotation);

        btnRight.setBackgroundResource(R.drawable.photo_btn_download_selector);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnUnClockwiseRotation.setOnClickListener(this);
        btnClockwiseRotation.setOnClickListener(this);
        viwePager.setOnPageChangeListener(this);

        if (selectedList == null || selectedList.size() == 0) {
            toastMessage(R.string.photo_not_select_pic);
            this.finish();
            return;
        }
        if (currentPosition < 0) {
            toastMessage(R.string.photo_position_can_not_less_than_zero);
            this.finish();
            return;
        }

        btnRight.setVisibility(canSave ? View.VISIBLE : View.INVISIBLE);
        llyBottomView.setVisibility(canRotate ? View.VISIBLE : View.INVISIBLE);

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
            tvTitleBarName.setVisibility(View.INVISIBLE);
        } else {
            tvTitleBarName.setVisibility(View.VISIBLE);
        }
        tvTitleBarName.setText((currentPosition + 1) + "/" + selectedList.size());
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

    private void rotateImage(int degree) {
        showToast("针旋转图片:" + degree);
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
        if (id == R.id.btnLeft) {//返回
            onOkBtnClicked();
        } else if (id == R.id.btnRight) {//保存图片
            showToast("保存图片");
        } else if (id == R.id.btnClockwiseRotation) {//顺时针旋转图片
            rotateImage(90);
        } else if (id == R.id.btnUnClockwiseRotation) {//逆时针旋转图片
            rotateImage(-90);
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

            itemView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
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
