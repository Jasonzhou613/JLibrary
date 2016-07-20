package com.ttsea.jlibrary.photo.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.common.ImageLoader;
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
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2015.09.10 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2015.10.23
 */
public class GalleryActivity extends BaseActivity implements OnClickListener,
        OnPageChangeListener {
    private final String TAG = "GalleryActivity";

    private TextView tvTitleBarName;
    private Button btnLeft;
    private Button btnRight;
    private Button btnOK;

    private ViewPagerFixed viwePager;
    private ArrayList<View> photoViews;
    private MyPageAdapter adapter;

    // 获取前一个activity传过来的图片list
    private List<ImageItem> selectedList;
    // 获取前一个activity传过来的position
    private int currentPosition;

    public static final String KEY_SELECTED_LIST = "selected_list";
    public static final String KEY_SELECTED_POSITION = "selected_position";

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_gallery_main);// 切屏到主界面

        Intent intent = getIntent();

        tvTitleBarName = (TextView) findViewById(R.id.tvTitleBarName);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        btnOK = (Button) findViewById(R.id.btnOK);
        viwePager = (ViewPagerFixed) findViewById(R.id.viwePager);

        btnLeft.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        viwePager.setOnPageChangeListener(this);

        Bundle bundle = intent.getExtras();
        selectedList = (List<ImageItem>) bundle
                .getSerializable(KEY_SELECTED_LIST);
        currentPosition = bundle.getInt(KEY_SELECTED_POSITION, 0);

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

        initPhotoViews();
        adapter = new MyPageAdapter(photoViews);
        viwePager.setAdapter(adapter);
        viwePager.setPageMargin(10);
        viwePager.setCurrentItem(currentPosition);

        refreshOkBtn();
    }

    private void initPhotoViews() {
        refreshPhotoViews();
    }

    private void refreshPhotoViews() {
        if (photoViews == null) {
            photoViews = new ArrayList<View>();
        }
        photoViews.clear();
        for (int i = 0; i < selectedList.size(); i++) {
            ImageItem item = selectedList.get(i);
            PhotoView imgView = new PhotoView(this);
            imgView.setBackgroundColor(0xff000000);
            if (item.isNetWorkImage()) {
                ImageLoader.getInstance().displayImage(mActivity, item.getPath(), imgView);
            } else {
                new LoadBitmapTask(imgView).execute(selectedList.get(i).getPath());
            }
            imgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            photoViews.add(imgView);
        }
    }

    public void refreshOkBtn() {
        if (selectedList.size() == 0) {
            btnOK.setText("完成(" + 0 + "/" + selectedList.size() + ")");
        } else {
            btnOK.setText("完成(" + (currentPosition + 1) + "/"
                    + selectedList.size() + ")");
        }

        if (selectedList.size() > 0) {
            btnOK.setEnabled(true);
            btnOK.setTextColor(Color.WHITE);
        } else {
            btnOK.setEnabled(false);
            btnOK.setTextColor(Color.parseColor("#E1E0DE"));
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
        if (id == R.id.btnLeft || id == R.id.btnOK) {
            onOkBtnClicked();
        } else if (id == R.id.btnRight) {
            onDeleteBtnClicked();
        } else {
        }
    }

    private void onOkBtnClicked() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_SELECTED_LIST, (Serializable) selectedList);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    private void onDeleteBtnClicked() {
        selectedList.remove(currentPosition);
        refreshPhotoViews();
        if (currentPosition >= selectedList.size()) {
            currentPosition = selectedList.size() - 1;
        } else if (currentPosition != 0) {
            currentPosition--;
        }
        if (photoViews.size() == 0) {
            onOkBtnClicked();
        } else {
            // adapter.notifyDataSetChanged();
            adapter = new MyPageAdapter(photoViews);
            viwePager.setAdapter(adapter);
            viwePager.setCurrentItem(currentPosition, true);
        }
        refreshOkBtn();
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
        refreshOkBtn();
    }

    private class MyPageAdapter extends PagerAdapter {
        private ArrayList<View> listViews;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
        }

        public int getCount() {
            return listViews.size();
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            // ((ViewPagerFixed) arg0)
            // .removeView(listViews.get(arg1 % getCount()));
        }

        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPagerFixed) arg0).addView(
                        listViews.get(arg1 % getCount()), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % getCount());
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    private class LoadBitmapTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView iv;

        public LoadBitmapTask(ImageView iv) {
            this.iv = iv;
        }

        @Override
        protected void onPreExecute() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.color.gray);
            iv.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.revisionImageSize(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            iv.setImageBitmap(result);
        }
    }
}
