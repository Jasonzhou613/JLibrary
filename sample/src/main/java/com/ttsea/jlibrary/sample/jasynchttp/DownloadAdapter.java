package com.ttsea.jlibrary.sample.jasynchttp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.ttsea.jlibrary.interfaces.OnItemViewClickListener;
import com.ttsea.jlibrary.jasynchttp.server.download.Downloader;
import com.ttsea.jlibrary.jasynchttp.server.download.OnDownloadListener;
import com.ttsea.jlibrary.sample.R;
import com.ttsea.jlibrary.utils.DigitUtils;
import com.ttsea.jlibrary.utils.Utils;

import java.util.List;

/**
 * 下载列表适配器 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/1/5 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/15 10:05
 */
public class DownloadAdapter extends BaseAdapter {
    private final String TAG = "DownloadAdapter";
    private Context mContext;
    private List<Downloader> downloaders;
    private LayoutInflater inflater;

    private OnItemViewClickListener onItemViewClickListener;

    public DownloadAdapter(Context context, List<Downloader> downloaders) {
        this.mContext = context;
        this.downloaders = downloaders;

        inflater = LayoutInflater.from(mContext);
    }

    public void setOnItemViewClickListener(OnItemViewClickListener l) {
        this.onItemViewClickListener = l;
    }

    @Override
    public int getCount() {
        return downloaders == null ? 0 : downloaders.size();
    }

    @Override
    public Object getItem(int position) {
        return downloaders == null ? null : downloaders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.download_detail_item, null);
            holder = new ViewHolder();
            holder.pb = (ProgressBar) convertView.findViewById(R.id.pb);
            holder.tvFileName = (TextView) convertView.findViewById(R.id.tvFileName);
            holder.tvInfo = (TextView) convertView.findViewById(R.id.tvInfo);
            holder.btnDownload = (Button) convertView.findViewById(R.id.btnDownload);
            holder.btnCancel = (Button) convertView.findViewById(R.id.btnCancel);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Downloader loader = downloaders.get(position);

        setOnViewClickListener(holder.btnDownload, position);
        setOnViewClickListener(holder.btnCancel, position);

        initViewData(loader, holder);

        loader.setOnDownloadListener(new OnDownloadListener() {
            @Override
            public void onPreDownload(Downloader downloader) {
                refreshView();
            }

            @Override
            public void onDownloadLinking(Downloader downloader) {
                refreshView();
            }

            @Override
            public void onDownloadStart(Downloader downloader) {
                refreshView();
            }

            @Override
            public void onDownloading(Downloader downloader, long totalSizeByte, long byteSoFar, long speed) {
                refreshView();
            }

            @Override
            public void onDownloadPause(Downloader downloader, int reason) {
                refreshView();
            }

            @Override
            public void onDownloadCancel(Downloader downloader, int reason) {
                refreshView();
            }

            @Override
            public void onComplete(Downloader downloader) {
                refreshView();
            }
        });

        return convertView;
    }

    public void refreshView() {
        notifyDataSetChanged();
    }

    private void setOnViewClickListener(final View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemViewClickListener != null) {
                    onItemViewClickListener.onItemViewClick(view, position);
                }
            }
        });
    }

    private void initViewData(Downloader loader, ViewHolder holder) {
        long totalSize = loader.getTotalSizeByte();
        long byteSoFar = loader.getByteSoFar();
        int status = loader.getStatus();

        String fileName = loader.getDownloadOption().getFileName();
        if (Utils.isEmpty(fileName)) {
            fileName = "未知";
        }
        holder.tvFileName.setText("文件名:" + fileName);

        String txt = "";
        String info = "";
        if (totalSize > 0) {
            info = getSizeWithUnit(byteSoFar) + "/" + getSizeWithUnit(totalSize);
            holder.pb.setMax((int) (totalSize / 1024));
            holder.pb.setProgress((int) (byteSoFar / 1024));
            holder.pb.setVisibility(View.VISIBLE);
        } else {
            holder.pb.setMax(1);
            holder.pb.setProgress(0);
            holder.pb.setVisibility(View.VISIBLE);
        }

        switch (status) {
            case Downloader.STATUS_PENDING:
                txt = "等待下载";
                info = info + "，" + txt;
                break;

            case Downloader.STATUS_LINKING:
                txt = "连接中...";
                info = info + "，" + txt;
                break;

            case Downloader.STATUS_RUNNING:
                txt = "暂停下载";
                info = info + "，速度：" + getSpeedWithUnit(loader.getSpeed());
                break;

            case Downloader.STATUS_PAUSED:
                txt = "已暂停";
                info = info + "，" + txt;
                break;

            case Downloader.STATUS_SUCCESSFUL:
                txt = "已完成";
                info = info + "，已完成";
                break;

            case Downloader.STATUS_FAILED:
                txt = "重新下载";
                info = info + "，下载失败";
                break;

            default:
                txt = "未知";
                info = info + "，" + txt;
                break;
        }

        holder.tvInfo.setText(info);
        holder.btnDownload.setText(txt);
    }

    private String getSizeWithUnit(long sizebyte) {
        float size = sizebyte;
        float radices = 1024.0f;
        if (sizebyte < 1024) {
            return size + "B";
        }

        size = DigitUtils.getFloat((sizebyte / radices), 2);
        if (size < 1024) {
            return size + "KB";
        }

        size = DigitUtils.getFloat((sizebyte / radices / radices), 2);
        if (size < 1024) {
            return size + "MB";
        }

        size = DigitUtils.getFloat((sizebyte / radices / radices / radices), 2);
        return size + "G";
    }

    private String getSpeedWithUnit(long speed) {
        float size = speed;
        float radices = 1024.0f;
        if (speed < 1024) {
            return size + "b/s";
        }

        size = DigitUtils.getFloat((speed / radices), 2);
        if (size < 1024) {
            return size + "kb/s";
        }

        size = DigitUtils.getFloat((speed / radices / radices), 2);
        return size + "M/s";
    }

    private static class ViewHolder {
        ProgressBar pb;
        TextView tvFileName;
        TextView tvInfo;
        Button btnDownload;
        Button btnCancel;
    }
}
