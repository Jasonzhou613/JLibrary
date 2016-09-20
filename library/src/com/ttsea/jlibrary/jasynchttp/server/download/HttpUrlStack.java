package com.ttsea.jlibrary.jasynchttp.server.download;


import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.jasynchttp.server.http.Http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * 下载http
 * Created by Jason on 2016/1/4.
 */
class HttpUrlStack {
    private final String TAG = "HttpUrlStack";

    private int connectionTimeOut;
    private long ifModifiedSince = 0;
    private boolean useCache = false;
    private boolean doInput = true;
    private boolean doOutput = false;
    private Map<String, String> additionalProperty = null;
    private String requestMethod = Http.Method.GET;
    private String threadId;

    private SSLSocketFactory mSslSocketFactory;
    private HttpOption httpOption;
    private DownloadFileInfo downloadInfo;

    public HttpUrlStack(HttpOption option, DownloadFileInfo downloadInfo) {
        this.httpOption = option;
        this.downloadInfo = downloadInfo;

        this.requestMethod = option.getRequestMethod();
        this.connectionTimeOut = option.getConnectionTimeOut();
        this.mSslSocketFactory = option.getSslSocketFactory();
        this.doInput = option.isDoInput();
        this.doOutput = option.isDoOutput();
        this.additionalProperty = option.getAdditionalProperty();

        this.threadId = downloadInfo.getThread_id();
    }

    public HttpOption getHttpOption() {
        return httpOption;
    }

    public DownloadFileInfo getDownloadInfo() {
        return downloadInfo;
    }

    public HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //设置本次连接是否自动处理重定向。
        //设置成true，系统自动处理重定向；设置成false，则需要自己从http reply中分析新的url
        conn.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());

        conn.setConnectTimeout(connectionTimeOut);
        conn.setRequestMethod(requestMethod);
        conn.setReadTimeout(connectionTimeOut);
        conn.setUseCaches(useCache);
        conn.setDoInput(doInput);
        conn.setDoOutput(doOutput);

        conn.setRequestProperty(Http.RequestHeadField.Accept,
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty(Http.RequestHeadField.Accept_Language, "zh-CN");
        conn.setRequestProperty(Http.RequestHeadField.Referer, downloadInfo.getUrl());
        conn.setRequestProperty(Http.RequestHeadField.Charset, "UTF-8");
        conn.setRequestProperty(Http.RequestHeadField.User_Agent, "android");
        conn.setRequestProperty(Http.RequestHeadField.Connection, "Keep-Alive");

        if (downloadInfo.getTotal_size_bytes() > 0) {
            long start = downloadInfo.getStart_bytes() + downloadInfo.getBytes_so_far();
            long end = downloadInfo.getEnd_bytes();
            // 设置获取实体数据的范围
            conn.setRequestProperty(Http.RequestHeadField.Range, "bytes=" + start + "-" + end);
        }
        if (ifModifiedSince > 0) {
            conn.setIfModifiedSince(ifModifiedSince);
        }

        if (additionalProperty != null) {
            for (String key : additionalProperty.keySet()) {
                conn.addRequestProperty(key, additionalProperty.get(key));
                JLog.d(TAG, "threadId:" + threadId + ", openConnection, additionalProperty, key:" + key + ", value:" + additionalProperty.get(key));
            }
        }

        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if ("https".equals(url.getProtocol()) && mSslSocketFactory != null) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(mSslSocketFactory);
        }

        //printConnection(conn);

        return conn;
    }

    private void printConnection(HttpURLConnection c) {
        if (!JLog.isDebugMode()) {
            return;
        }

        try {
            boolean AllowUserInteraction = c.getAllowUserInteraction();
            Object content = c.getContent();
            String ContentEncoding = c.getContentEncoding();
            String ContentType = c.getContentType();
            int ConnectTimeout = c.getConnectTimeout();
            long ContentLength = c.getContentLength();
            long Date = c.getDate();
            long Expiration = c.getExpiration();
            long IfModifiedSince = c.getIfModifiedSince();
            long LastModified = c.getLastModified();
            long ReadTimeout = c.getReadTimeout();
            //Cannot access request header fields after connection is set
            //Map<String, List<String>> RequestProperties = c.getRequestProperties();
            Map<String, List<String>> HeaderFields = c.getHeaderFields();
            URL url = c.getURL();

            JLog.d(TAG, "printConnettion, threadId:" + threadId +
                    ", \nAllowUserInteraction:" + AllowUserInteraction +
                    ", \nContent:" + content +
                    ", \nContentEncoding:" + ContentEncoding +
                    ", \nContentType:" + ContentType +
                    ", \nConnectTimeout:" + ConnectTimeout +
                    ", \nContentLength:" + ContentLength +
                    ", \nDate:" + Date +
                    ", \nExpiration:" + Expiration +
                    ", \nIfModifiedSince:" + IfModifiedSince +
                    ", \nLastModified:" + LastModified +
                    ", \nReadTimeout:" + ReadTimeout +
                    //", \nRequestProperties:" + RequestProperties +
                    ", \nHeaderFields:" + HeaderFields +
                    ", \nurl:" + url
            );

        } catch (IOException e) {
            JLog.e(TAG, "threadId:" + threadId + ", IOException e：" + e.toString());

        } catch (Exception e) {
            JLog.e(TAG, "threadId:" + threadId + ", Exception e：" + e.toString());
        }
    }
}
