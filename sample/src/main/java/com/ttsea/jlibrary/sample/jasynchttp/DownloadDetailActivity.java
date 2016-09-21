package com.ttsea.jlibrary.sample.jasynchttp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.component.dialog.MyAlertDialog;
import com.ttsea.jlibrary.interfaces.OnItemViewClickListener;
import com.ttsea.jlibrary.jasynchttp.server.download.DownloadManager;
import com.ttsea.jlibrary.jasynchttp.server.download.Downloader;
import com.ttsea.jlibrary.sample.R;

import java.util.List;

/**
 * 下载详情 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/5/17 16:34 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/17 16:34
 */
public class DownloadDetailActivity extends BaseActivity implements OnItemViewClickListener,
        View.OnClickListener {
    private final String TAG = "DownloadDetailActivity";
    private List<Downloader> downloaderList;
    private ListView lvDownload;
    private DownloadAdapter adapter;
    private TextView tvNoDownloader;
    private Button btnPauseAll;
    private Button btnCancelAll;

    private boolean needDeleteFile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_detail_main);

        lvDownload = (ListView) findViewById(R.id.lvDownloadList);
        tvNoDownloader = (TextView) findViewById(R.id.tvNoDownloader);
        btnPauseAll = (Button) findViewById(R.id.btnPauseAll);
        btnCancelAll = (Button) findViewById(R.id.btnCancelAll);

        downloaderList = DownloadManager.getInstance(mActivity).getDownloaderList();
        adapter = new DownloadAdapter(mActivity, downloaderList);
        lvDownload.setAdapter(adapter);
        adapter.setOnItemViewClickListener(this);

        btnPauseAll.setOnClickListener(this);
        btnCancelAll.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (downloaderList == null || downloaderList.size() < 1) {
            lvDownload.setVisibility(View.GONE);
            tvNoDownloader.setVisibility(View.VISIBLE);
        } else {
            lvDownload.setVisibility(View.VISIBLE);
            tvNoDownloader.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancelAll:
                showAlertDialog("是否取消所有下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadManager.getInstance(mActivity).removeAllDownloader(needDeleteFile);
                        adapter.notifyDataSetChanged();
                    }
                });
                break;

            case R.id.btnPauseAll:
                DownloadManager.getInstance(mActivity).pauseAllDownloader(Downloader.PAUSED_HUMAN);
                break;

            default:
                break;
        }

    }

    @Override
    public void onItemViewClick(View v, int position) {
        if (position < 0 || position >= downloaderList.size()) {
            return;
        }

        final Downloader downloader = downloaderList.get(position);
        switch (v.getId()) {
            case R.id.btnDownload:
                if (downloader.getStatus() == Downloader.STATUS_PAUSED
                        || downloader.getStatus() == Downloader.STATUS_FAILED) {
                    downloader.resume();

                } else if (downloader.getStatus() == Downloader.STATUS_RUNNING
                        || downloader.getStatus() == Downloader.STATUS_PENDING) {
                    downloader.pause(Downloader.PAUSED_HUMAN);
                }
                break;

            case R.id.btnCancel:
                showAlertDialog("是否取消该下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadManager.getInstance(mActivity).removeDownloader(downloader, needDeleteFile);
                        adapter.notifyDataSetChanged();
                    }
                });
                break;

            default:
                break;
        }
    }

    private void showAlertDialog(String msg, DialogInterface.OnClickListener positiveListener) {
        needDeleteFile = false;
        View view = LayoutInflater.from(mActivity).inflate(R.layout.download_detail_alter_view, null);
        CheckBox cbDeleteFile = (CheckBox) view.findViewById(R.id.cbDeleteFile);
        cbDeleteFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                needDeleteFile = isChecked;
            }
        });
        cbDeleteFile.setChecked(false);

        MyAlertDialog.Builder builder = new MyAlertDialog.Builder(mActivity);
        builder.setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("确定", positiveListener)
                .setNegativeButton("取消", null)
                .setContentView(view)
                .setCancelable(true);

        MyAlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                needDeleteFile = false;
            }
        });

        dialog.show();
    }
}
