package com.ttsea.jlibrary.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.base.JBaseAdapter;
import com.ttsea.jlibrary.common.JImageLoader;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.common.JToast;
import com.ttsea.jlibrary.component.pageflow.PageIndicator;
import com.ttsea.jlibrary.component.pageflow.PageView;

import java.util.ArrayList;
import java.util.List;

/**
 * //To do <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/7/12 15:40 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/7/12 15:40
 */
public class PageViewActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "PageViewActivity";

    private RelativeLayout llyParentView;
    private PageView pageView;
    private PageIndicator indicator;

    private List<String> mList;
    private PageViewAdapter mAdapter;

    private final String[] testImages = new String[]{
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/859a27c6a61224fdbc140bf7b6f99f4d",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/fc90c4c9bb54dae3d63642cf20cbbc12",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/8c748752d963790074bfae4302d0a84f",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/7f39e67fb78adcbaad955b4466f74fe5",
            "file:///storage/sdcard0/test-image/ratio/IMG_0572.JPG",
            "file:///storage/sdcard0/test-image/big-pic/中国政区2500.jpg",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/677ef48b95d71957495a745bc6e64263",
            "http://hws002.b0.upaiyun.com/team/2162187/20160820/fc90c4c9bb54dae3d63642cf20cbbc12"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_view_main);

        initView();
        initData();
    }

    private void initView() {
        llyParentView = (RelativeLayout) findViewById(R.id.llyParentView);
        pageView = (PageView) findViewById(R.id.pageView);
        indicator = (PageIndicator) findViewById(R.id.pvIndicator);
    }

    private void initData() {
        mList = new ArrayList<>();

        for (int i = 0; i < testImages.length; i++) {
            mList.add(testImages[i]);
        }
        pageView.setViewGroup(llyParentView);

        pageView.setIndicator(indicator);
        pageView.setOnViewSwitchListener(new PageView.OnViewSwitchListener() {
            @Override
            public void onSwitched(View view, int position) {
//                JToast.makeTextCenter(mActivity, "ViewSwitch: " + (position+1));
                JLog.d(TAG, "viewSwitch: " + (position + 1));
            }
        });
        mAdapter = new PageViewAdapter(mActivity, mList);
        pageView.setAdapter(mAdapter);
        pageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PageViewAdapter adapter = (PageViewAdapter) parent.getAdapter();
                String url = adapter.getItem(position);
                JLog.d("jason", "position:" + (position + 1) + ", url:" + url);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pageView != null) {
            pageView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (pageView != null) {
            pageView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {

            case R.id.btnSelect:
                break;

            default:
                break;
        }
    }

    private class PageViewAdapter extends JBaseAdapter<String> {

        PageViewAdapter(Context context, List<String> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null || (!(convertView.getTag() instanceof ViewHolder))) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.page_view_item, parent, false);
                holder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
                holder.tvDes = (TextView) convertView.findViewById(R.id.tvDes);
                convertView.setTag(String.valueOf(position));
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvDes.setText(String.valueOf(position + 1));
            JImageLoader.getInstance().displayImage(mActivity, mList.get(position), holder.ivImage);

            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView ivImage;
        TextView tvDes;
    }
}
