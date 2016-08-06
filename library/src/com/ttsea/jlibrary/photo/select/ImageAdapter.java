package com.ttsea.jlibrary.photo.select;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.ImageLoader;

import java.util.ArrayList;
import java.util.List;

class ImageAdapter extends BaseAdapter {
    private final String TAG = "Select.ImageAdapter";

    private Context context;
    private LayoutInflater mInflater;
    private List<ImageItem> imageList;
    private List<ImageItem> selectedImageList;

    private final int TYPE_CAMERA = 0;
    private final int TYPE_NORMAL = 1;

    private boolean showCamera = true;
    private boolean showSelectIndicator = true;
    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;

    public ImageAdapter(Context context, List<ImageItem> imageList) {
        this.context = context;
        this.imageList = imageList;

        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(context);
        mItemLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
        selectedImageList = new ArrayList<ImageItem>();
    }

    public void setDefaultSelected(ArrayList<String> resultList) {
        for (String filePath : resultList) {
            ImageItem image = getImageByPath(filePath);
            if (image != null) {
                selectedImageList.add(image);
            }
        }
        if (selectedImageList.size() > 0) {
            notifyDataSetChanged();
        }
    }

    private ImageItem getImageByPath(String filePath) {
        if (imageList != null && imageList.size() > 0) {
            for (ImageItem image : imageList) {
                if (image.getPath().equalsIgnoreCase(filePath)) {
                    return image;
                }
            }
        }
        return null;
    }

    public void setItemSize(int columnWidth) {
        if (mItemSize == columnWidth) {
            return;
        }

        mItemSize = columnWidth;
        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return showCamera ? imageList.size() + 1 : imageList.size();
    }

    @Override
    public ImageItem getItem(int position) {
        if (showCamera) {
            if (position == 0) {
                return null;
            }
            return imageList.get(position - 1);
        } else {
            return imageList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);

        if (type == TYPE_CAMERA) {
            convertView = mInflater.inflate(R.layout.imageselector_item_camera, parent, false);
            convertView.setTag(null);

        } else if (type == TYPE_NORMAL) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.imageselector_item_image, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
                if (holder == null) {
                    convertView = mInflater.inflate(R.layout.imageselector_item_image, parent, false);
                    holder = new ViewHolder(convertView);
                }
            }

            if (showSelectIndicator) {
                holder.ivCheck.setVisibility(View.VISIBLE);
                if (selectedImageList.contains(getItem(position))) {
                    holder.ivCheck.setImageResource(R.drawable.imageselector_select_checked);
                    holder.photoMask.setVisibility(View.VISIBLE);
                } else {
                    holder.ivCheck.setImageResource(R.drawable.imageselector_select_uncheck);
                    holder.photoMask.setVisibility(View.GONE);
                }
            } else {
                holder.ivCheck.setVisibility(View.GONE);
                holder.ivCheck.setVisibility(View.GONE);
            }

            if (mItemSize > 0) {
                ImageLoader.getInstance().displayImage(context, "file://" + getItem(position).getPath(), holder.ivImage);
            }
        }

        GridView.LayoutParams layoutParams = (GridView.LayoutParams) convertView.getLayoutParams();
        if (layoutParams.height != mItemSize) {
            convertView.setLayoutParams(mItemLayoutParams);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return TYPE_CAMERA;
        }
        return TYPE_NORMAL;
    }

    public void setShowSelectIndicator(boolean showSelectIndicator) {
        this.showSelectIndicator = showSelectIndicator;
        notifyDataSetChanged();
    }

    public void setShowCamera(boolean showCamera) {
        if (this.showCamera == showCamera) {
            return;
        }

        this.showCamera = showCamera;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void selectOrRemoveImage(ImageItem image) {
        if (selectedImageList.contains(image)) {
            selectedImageList.remove(image);
        } else {
            selectedImageList.add(image);
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView ivImage;
        ImageView ivCheck;
        View photoMask;

        ViewHolder(View itemView) {
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            ivCheck = (ImageView) itemView.findViewById(R.id.ivCheck);
            photoMask = itemView.findViewById(R.id.photoMask);
            itemView.setTag(this);
        }
    }
}