package com.ttsea.jlibrary.sample.jasynchttp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ttsea.jlibrary.base.BaseActivity;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.jasynchttp.server.download.DownloadManager;
import com.ttsea.jlibrary.jasynchttp.server.download.DownloadOption;
import com.ttsea.jlibrary.jasynchttp.server.download.Downloader;
import com.ttsea.jlibrary.jasynchttp.server.download.OnDownloadListener;
import com.ttsea.jlibrary.jasynchttp.server.download.SaveFileMode;
import com.ttsea.jlibrary.sample.R;
import com.ttsea.jlibrary.utils.DigitUtils;
import com.ttsea.jlibrary.utils.Utils;

/**
 * //To do <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/9/20 11:07 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/9/20 11:07
 */
public class DownloadActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "DownloadActivity";
    private EditText etPath;
    private Button btnAddDownload;
    private Button btnDownloadDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_main);

        etPath = (EditText) findViewById(R.id.etPath);
        btnAddDownload = (Button) findViewById(R.id.btnAddDownload);
        btnDownloadDetail = (Button) findViewById(R.id.btnDownloadDetail);

        btnAddDownload.setOnClickListener(this);
        btnDownloadDetail.setOnClickListener(this);

        etPath.setText(TestDownloadUrl.TEMP_URL);
        if (etPath.getText() != null) {
            String txt = etPath.getText().toString();
            etPath.setSelection(txt.length());
        }

        JLog.copyDB2SD(mActivity, "jasync.db");
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnAddDownload:
                addDownload(etPath.getText().toString());
                //addDownloads();
                break;

            case R.id.btnDownloadDetail:
                intent = new Intent(mActivity, DownloadDetailActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void addDownloads() {
        String[] urls = new String[]{
                TestDownloadUrl.TEMP_URL,
                TestDownloadUrl.DOWNLOAD_URL_0_0_824,
                TestDownloadUrl.DOWNLOAD_URL_20_3,
                TestDownloadUrl.DOWNLOAD_URL_4_85,
                TestDownloadUrl.DOWNLOAD_URL_3_77,
                // TestDownloadUrl.DOWNLOAD_URL_127_17
                TestDownloadUrl.DOWNLOAD_URL_20_3
        };

        for (int i = 0; i < urls.length; i++) {
            DownloadManager.getInstance(getApplicationContext()).addNewDownloader(urls[i]);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //以下单独测试Downloader
    private ViewHolder holder;
    private Downloader downloader;

    private void addDownload(String downloaderUrl) {
        final View downloadView = findViewById(R.id.downloadView);
        downloadView.setVisibility(View.VISIBLE);

        holder = new ViewHolder();
        holder.pb = (ProgressBar) downloadView.findViewById(R.id.pb);
        holder.tvFileName = (TextView) downloadView.findViewById(R.id.tvFileName);
        holder.tvInfo = (TextView) downloadView.findViewById(R.id.tvInfo);
        holder.btnDownload = (Button) downloadView.findViewById(R.id.btnDownload);
        holder.btnCancel = (Button) downloadView.findViewById(R.id.btnCancel);


        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = downloader.getStatus();

                if (status == Downloader.STATUS_PAUSED
                        || status == Downloader.STATUS_SUCCESSFUL
                        || status == Downloader.STATUS_FAILED) {
                    downloader.start();

                } else if (status == Downloader.STATUS_PENDING
                        || status == Downloader.STATUS_LINKING
                        || status == Downloader.STATUS_RUNNING) {
                    downloader.pause(Downloader.PAUSED_HUMAN);
                }

            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloader.cancel(Downloader.ERROR_BLOCKED, "delete by human");
                downloadView.setVisibility(View.GONE);
            }
        });

        if (downloader != null) {
            downloader.cancel(Downloader.ERROR_BLOCKED);
        }

        DownloadOption option = new DownloadOption.Builder(mActivity).build();
        option.getBuilder()
                .setSaveFileMode(SaveFileMode.OVERRIDE)
                .setExpiredTime(1000 * 60 * 2);
        downloader = new Downloader(mActivity, downloaderUrl, option);

        downloader.setOnDownloadListener(
                new OnDownloadListener() {
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
                        downloader.deleteRecord();
                    }
                }
        );
    }

    private void refreshView() {
        initViewData(downloader, holder);
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
