package com.ttsea.jlibrary.photo.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
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
import com.ttsea.jlibrary.common.ImageLoader;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.photo.select.ImageItem;
import com.ttsea.jlibrary.utils.BitmapUtils;

import java.io.Serializable;
import java.util.ArrayList;
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

    private TextView tvTitleBarName;
    private Button btnLeft;
    private Button btnRight;
    private TextView tvIndex;
    private Button btnSavePic;
    private Button btnDelete;

    private ViewPagerFixed viwePager;
    private ArrayList<View> photoViews;
    private MyPageAdapter adapter;

    /** 获取前一个activity传过来的图片list */
    private List<ImageItem> selectedList;
    /** 获取前一个activity传过来的position */
    private int currentPosition;
    /** 是否可保存图片, 默认为false */
    private boolean canSave = false;
    /** 是否可删除图片, 默认为false */
    private boolean canDel = false;

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
        canDel = bundle.getBoolean(GalleryConstants.KEY_CAN_DEL, false);

        tvTitleBarName = (TextView) findViewById(R.id.tvTitleBarName);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        viwePager = (ViewPagerFixed) findViewById(R.id.viwePager);
        tvIndex = (TextView) findViewById(R.id.tvIndex);
        btnSavePic = (Button) findViewById(R.id.btnSavePic);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnSavePic.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
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

        btnSavePic.setVisibility(canSave ? View.VISIBLE : View.INVISIBLE);
        btnDelete.setVisibility(canDel ? View.VISIBLE : View.INVISIBLE);

        initPhotoViews();
        adapter = new MyPageAdapter(photoViews);
        viwePager.setAdapter(adapter);
        viwePager.setPageMargin(10);
        viwePager.setCurrentItem(currentPosition);

        refreshTvIndex();
    }

    private void initPhotoViews() {
        refreshPhotoViews();
    }

    private void setLoadingViewVisibility(View childView, int visibility) {
        View parent;
        ViewParent viewParent = childView.getParent();
        if (viewParent == null) {
            return;
        }
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

    private void refreshPhotoViews() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        if (photoViews == null) {
            photoViews = new ArrayList<View>();
        }
        photoViews.clear();

        ImageLoader.ImageLoadingListener listener = new ImageLoader.ImageLoadingListener() {
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


        for (int i = 0; i < selectedList.size(); i++) {
            View itemView = inflater.inflate(R.layout.photo_gallery_item, null);

            PhotoView pvImage = (PhotoView) itemView.findViewById(R.id.pvImage);
            pvImage.setBackgroundColor(0xff000000);

            ImageItem item = selectedList.get(i);

            if (item.isNetWorkImage()) {
                ImageLoader.getInstance().displayImageForGallery(mActivity, item.getPath(), pvImage, listener);
            } else {
                ImageLoader.getInstance().displayImageForGallery(mActivity, "file://" + item.getPath(), pvImage, listener);
            }
            itemView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            photoViews.add(itemView);
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
        int removePosition = currentPosition;
        selectedList.remove(currentPosition);
        photoViews.remove(currentPosition);

        if (currentPosition >= photoViews.size() && photoViews.size() != 0) {
            currentPosition = photoViews.size() - 1;
        } else if (currentPosition != 0) {
            currentPosition--;
        }
        JLog.d(TAG, "removed position:" + removePosition + ", currentPosition:" + currentPosition);
        adapter.notifyDataSetChanged();

        if (photoViews.size() == 0) {
            onOkBtnClicked();
        }
        refreshTvIndex();
    }

    public void refreshTvIndex() {
        if (selectedList.size() <= 1) {
            tvIndex.setVisibility(View.INVISIBLE);
        } else {
            tvIndex.setVisibility(View.VISIBLE);
        }
        tvIndex.setText((currentPosition + 1) + "/" + selectedList.size());
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
        private ArrayList<View> listViews;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
        }

        public int getCount() {
            return listViews.size();
        }

        public void destroyItem(View container, int position, Object object) {
            JLog.d(TAG, "destroyItem, position:" + (position % getCount()));

            try {
                ((ViewPagerFixed) container).removeView(listViews.get(position % getCount()));
            } catch (Exception e) {
                JLog.e(TAG, "Exception e:" + e.toString());
            }
        }

        public Object instantiateItem(ViewGroup container, int position) {
            try {
                container.addView(listViews.get(position % getCount()), 0);
            } catch (Exception e) {
                JLog.e(TAG, "Exception e:" + e.toString());
            }
            JLog.d(TAG, "instantiateItem, position:" + (position % getCount()));
            return listViews.get(position % getCount());
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
