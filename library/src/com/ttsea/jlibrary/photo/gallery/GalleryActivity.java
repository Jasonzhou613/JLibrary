package com.ttsea.jlibrary.photo.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.ttsea.jlibrary.base.JBaseActivity;
import com.ttsea.jlibrary.common.JImageLoader;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.component.dialog.TransparentDialog;
import com.ttsea.jlibrary.photo.select.ImageItem;
import com.ttsea.jlibrary.utils.CacheDirUtils;
import com.ttsea.jlibrary.utils.DateUtils;
import com.ttsea.jlibrary.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 浏览被选择了的照片<br/>
 * 可以传入的参数有：<br/>
 * selected_list: 被选择了的照片列表<br/>
 * selected_position: 从哪个位置开始查看，从0开始，不能小于0，默认为0<br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2015.09.10 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2015.10.23
 */
public class GalleryActivity extends JBaseActivity implements OnClickListener,
        OnPageChangeListener, ImageSaveListener {
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

    private TransparentDialog saveImageDialog;
    /** 记录是否正在保存图片 */
    private boolean isSaveImageing = false;

    private List<View> views;
    /** 获取前一个activity传过来的图片list */
    private List<ImageItem> selectedList;
    /** 获取前一个activity传过来的position */
    private int currentPosition;
    /** 是否可保存图片, 默认为false */
    private boolean canSave = false;
    /** 是否可旋转图片, 默认为true */
    private boolean canRotate = true;
    /** 若图片设置为可保存，这里可以设置保存的地址 */
    private String savePath;

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jphoto_gallery_main);// 切屏到主界面

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        selectedList = (List<ImageItem>) bundle.getSerializable(GalleryConstants.KEY_SELECTED_LIST);
        currentPosition = bundle.getInt(GalleryConstants.KEY_SELECTED_POSITION, 0);
        canSave = bundle.getBoolean(GalleryConstants.KEY_CAN_SAVE, false);
        canRotate = bundle.getBoolean(GalleryConstants.KEY_CAN_ROTATE, true);
        savePath = bundle.getString(GalleryConstants.KEY_SAVE_PATH);

        llyTitleBar = findViewById(R.id.llyTitleBar);
        tvTitleBarName = (TextView) findViewById(R.id.tvTitleBarName);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);

        viwePager = (ViewPagerFixed) findViewById(R.id.viwePager);
        llyBottomView = findViewById(R.id.llyBottomView);
        btnUnClockwiseRotation = (Button) findViewById(R.id.btnUnClockwiseRotation);
        btnClockwiseRotation = (Button) findViewById(R.id.btnClockwiseRotation);

        btnRight.setBackgroundResource(R.drawable.jphoto_btn_download_selector);
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
        //如果保存地址未设置，这里给个默认值
        if (Utils.isEmpty(savePath)) {
            savePath = CacheDirUtils.getSdDataDir(mActivity);
        }

        File dir = new File(savePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        views = new ArrayList<View>();
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        for (int i = 0; i < selectedList.size(); i++) {
            View itemView = inflater.inflate(R.layout.jphoto_gallery_item, null);
            views.add(itemView);
        }

        btnRight.setVisibility(canSave ? View.VISIBLE : View.INVISIBLE);
        llyBottomView.setVisibility(canRotate ? View.VISIBLE : View.INVISIBLE);

        adapter = new MyPageAdapter(views, selectedList);
        viwePager.setAdapter(adapter);
        viwePager.setPageMargin(10);
        viwePager.setCurrentItem(currentPosition);

        refreshTvIndex();
    }

    protected void setLoadingViewVisibility(View childView, int visibility) {
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

    protected void onOkBtnClicked() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GalleryConstants.KEY_SELECTED_LIST, (Serializable) selectedList);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    protected void onDeleteBtnClicked() {
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

    protected void refreshTvIndex() {
        if (selectedList.size() <= 1) {
            tvTitleBarName.setVisibility(View.INVISIBLE);
        } else {
            tvTitleBarName.setVisibility(View.VISIBLE);
        }
        tvTitleBarName.setText((currentPosition + 1) + "/" + selectedList.size());
    }

    protected void displayImage(ImageItem item, ImageView imageView) {
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

    protected void rotateImage(float degree) {
        View itemView = views.get(currentPosition);
        View pvImage = itemView.findViewById(R.id.pvImage);
        if (pvImage instanceof PhotoView) {
            ((PhotoView) pvImage).rotate(degree);
        }
    }

    protected void saveImage() {
        if (isSaveImageing) {
            showToast(R.string.image_save_imageing);
            return;
        }
        String fileName = DateUtils.getCurrentTime("yyy-MM-dd_HH_mm_ss") + ".jpg";
        View itemView = views.get(currentPosition);
        View pvImage = itemView.findViewById(R.id.pvImage);
        if (pvImage instanceof PhotoView) {
            ((PhotoView) pvImage).setImageSaveListener(this);
            ((PhotoView) pvImage).saveImage(savePath, fileName);
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
        if (id == R.id.btnLeft) {//返回
            onOkBtnClicked();
        } else if (id == R.id.btnRight) {//保存图片
            saveImage();
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

    @Override
    public void onStartSave() {
        isSaveImageing = true;
        if (saveImageDialog == null) {
            saveImageDialog = new TransparentDialog(mActivity);
            saveImageDialog.setCanceledOnTouchOutside(false);
        }
        if (saveImageDialog.isShowing()) {
            return;
        }
        saveImageDialog.show(getStringById(R.string.image_save_imageing));
    }

    @Override
    public void onSaveComplete(String path) {
        isSaveImageing = false;
        saveImageDialog.dismiss();
        toastMessage("图片已保存到" + path);
    }

    @Override
    public void onSaveFailed(String reason) {
        isSaveImageing = false;
        saveImageDialog.dismiss();
    }

    private class MyPageAdapter extends PagerAdapter {
        private List<View> viewItems;
        private List<ImageItem> items;

        public MyPageAdapter(List<View> views, List<ImageItem> listViews) {
            this.viewItems = views;
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

            View itemView = viewItems.get(position);
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
