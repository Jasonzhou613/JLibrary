package com.ttsea.jlibrary.photo.select;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.JImageLoader;

import java.util.ArrayList;
import java.util.List;

class FolderAdapter extends BaseAdapter {
    private final String TAG = "Select.FolderAdapter";

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Folder> folderList;

    private int lastSelected = 0;

    public FolderAdapter(Context context) {
        this.mContext = context;

        init();
    }

    private void init() {
        mLayoutInflater = LayoutInflater.from(mContext);
        folderList = new ArrayList<>();
    }

    public void setData(List<Folder> folders) {
        folderList.clear();
        if (folders != null) {
            folderList.addAll(folders);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return folderList.size() + 1;
    }

    @Override
    public Folder getItem(int position) {
        if (position == 0)
            return null;
        return folderList.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.jimageselector_item_folder, parent, false);

            holder.ivCover = (ImageView) convertView.findViewById(R.id.ivCover);
            holder.tvFolderName = (TextView) convertView.findViewById(R.id.tvFolderName);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tvCount);
            holder.ivIndicator = (ImageView) convertView.findViewById(R.id.ivIndicator);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            holder.tvFolderName.setText(R.string.image_all_folder);
            holder.tvCount.setText("" + getTotalImageSize() + (mContext.getResources().getText(R.string.image_sheet)));

            if (folderList.size() > 0) {
                Folder folder = folderList.get(0);
                JImageLoader.getInstance().displayImageAsBitmap(mContext, "file://" + folder.getCover().getPath(), holder.ivCover);
            }
        } else {

            Folder folder = getItem(position);
            holder.tvFolderName.setText(folder.getName());
            holder.tvCount.setText("" + folder.getImages().size() + (mContext.getResources().getText(R.string.image_sheet)));

            JImageLoader.getInstance().displayImageAsBitmap(mContext, "file://" + folder.getCover().getPath(), holder.ivCover);
        }

        if (lastSelected == position) {
            holder.ivIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.ivIndicator.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    public void setSelectIndex(int position) {
        if (lastSelected == position)
            return;
        lastSelected = position;
        notifyDataSetChanged();
    }

    private int getTotalImageSize() {
        int result = 0;
        if (folderList != null && folderList.size() > 0) {
            for (Folder folder : folderList) {
                result += folder.getImages().size();
            }
        }
        return result;
    }

    private static class ViewHolder {
        ImageView ivCover;
        TextView tvFolderName;
        TextView tvCount;
        ImageView ivIndicator;
    }
}