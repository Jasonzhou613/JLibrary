package com.ttsea.jlibrary.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.base.JBaseAdapter;
import com.ttsea.jlibrary.common.JImageLoader;
import com.ttsea.jlibrary.component.widget.roundImage.RoundedImageView;
import com.ttsea.jlibrary.photo.select.ImageConfig;
import com.ttsea.jlibrary.photo.select.ImageItem;
import com.ttsea.jlibrary.photo.select.ImageSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * 圆角图片 <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/10/9 16:15 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/10/9 16:15
 */
public class RoundImageActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "RoundImageActivity";

    private ListView lvListView;
    private Button btnSelect;

    private List<ImageItem> selectedList;
    private ImageAdapter mAdapter;

    private final int REQUEST_CODE_SELECT_PIC = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_image_main);

        initView();
        initData();
    }

    private void initView() {
        lvListView = (ListView) findViewById(R.id.lvListView);
        btnSelect = (Button) findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(this);
    }

    private void initData() {
        selectedList = new ArrayList<>();
        mAdapter = new ImageAdapter(mActivity, selectedList);

        lvListView.setAdapter(mAdapter);
    }

    private void onSelectImageBack(List<ImageItem> list) {
        if (list == null || list.size() < 1) {
            return;
        }

        if (list != null) {
            selectedList.clear();
        }
        for (int i = 0; i < 9; i++) {
            selectedList.add(list.get(0));
        }

        mAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_PIC) {//选择图片回来
            if (resultCode == Activity.RESULT_CANCELED) {
                toastMessage("你取消了选择图片");
            } else if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    toastMessage("选择图片出错");
                    return;
                }

                List<ImageItem> list = (List<ImageItem>) data.getExtras()
                        .getSerializable(ImageSelector.KEY_SELECTED_LIST);
                onSelectImageBack(list);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSelect:
                ImageConfig config = new ImageConfig.Builder(mActivity)
                        .setMutiSelect(false)
                        .setRequestCode(REQUEST_CODE_SELECT_PIC)
                        .setCrop(false)
                        .build();
                ImageSelector.open(mActivity, config);
                break;

            default:
                break;
        }
    }

    private class ImageAdapter extends JBaseAdapter<ImageItem> {
        public ImageAdapter(Context context, List<ImageItem> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.round_image_item, parent, false);

            RoundedImageView ivImage = (RoundedImageView) convertView.findViewById(R.id.ivImage);
            TextView tvDes = (TextView) convertView.findViewById(R.id.tvDes);

            if (position == 1) {
                ivImage.setCornerRadius(0, 20, 20, 0);

            } else if (position == 2) {
                ivImage.mutateBackground(false);
                ivImage.setBackgroundColor(getColorById(R.color.green));

            } else if (position == 3) {
                ivImage.setOval(true);

            } else if (position == 4) {
                ivImage.setTileModeX(Shader.TileMode.REPEAT);

            } else if (position == 5) {
                ivImage.setBorderWidth(20f);
                ivImage.setBorderColor(getColorById(R.color.gold));

            } else if (position == 6) {
                ivImage.setCornerRadius(300);
                ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } else if (position == 7) {
                ivImage.setScaleType(ImageView.ScaleType.FIT_END);

            } else if (position == 8) {
                ivImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }

            JImageLoader.getInstance().displayImage(mActivity, "file://" + mList.get(position).getPath(), ivImage);
            String info = "radius:" + ivImage.getCornerRadius() + "\n"
                    + "scaleType:" + ivImage.getScaleType() + "\n"
                    + "borderWidth:" + ivImage.getBorderWidth() + "\n"
                    + "tileMode_X:" + ivImage.getTileModeX() + "\n"
                    + "tileMode_Y:" + ivImage.getTileModeY() + "\n"
                    + "isOval:" + ivImage.isOval() + "\n"
                    + "mutatesBackground:" + ivImage.mutatesBackground() + "\n";
            tvDes.setText(info);

            return convertView;
        }
    }
}
