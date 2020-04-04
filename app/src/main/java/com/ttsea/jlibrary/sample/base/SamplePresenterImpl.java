package com.ttsea.jlibrary.sample.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ttsea.jlibrary.base.BasePresenterImpl;
import com.ttsea.jlibrary.common.customHttpClient.Http;
import com.ttsea.jlibrary.debug.JLog;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * //描述
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2020/4/4 14:59 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class SamplePresenterImpl extends BasePresenterImpl {
    public SamplePresenterImpl(Context context) {
        super(context);
    }

    /**
     * 获取http请求结果，这里使用同步请求，需要放入子线程处理<br>
     * OkHttpClient默认使用 {@link BasePresenterImpl#getDefaultOkHttpClient()}<br>
     * 默认使用：POST方法
     *
     * @param url    url，完整路径（包含http开头）
     * @param params 参数
     * @return
     * @throws Exception
     */
    @NonNull
    public String getResponseSync(@NonNull String url, @Nullable Map<String, String> params) throws Exception {
        return getResponseSync(null, url, params);
    }

    /**
     * 获取http请求结果，这里使用同步请求，需要放入子线程处理<br>
     * 默认使用：POST方法
     *
     * @param httpClient OkHttpClient，为null的时候默认使用
     *                   {@link BasePresenterImpl#getDefaultOkHttpClient()}
     * @param url        url，完整路径（包含http开头）
     * @param params     参数
     * @return
     * @throws Exception
     */
    @NonNull
    public String getResponseSync(@Nullable OkHttpClient httpClient, @NonNull String url,
                                  @Nullable Map<String, String> params) throws Exception {
        return getResponseSync(httpClient, Http.Method.POST, url, params);
    }

    /**
     * 获取http请求结果，这里使用同步请求，需要放入子线程处理
     *
     * @param httpClient OkHttpClient，为null的时候默认使用
     *                   {@link BasePresenterImpl#getDefaultOkHttpClient()}
     * @param url        url，完整路径（包含http开头）
     * @param method     请求方法（GET/POST...）
     * @param params     参数
     * @return
     * @throws Exception
     */
    @NonNull
    public String getResponseSync(@Nullable OkHttpClient httpClient, @Nullable String method, @NonNull String url,
                                  @Nullable Map<String, String> params) throws Exception {

        final long requestStartTimeMillis = System.currentTimeMillis();
        JLog.d("requestStart:" + requestStartTimeMillis);

        if (httpClient == null) {
            httpClient = getDefaultOkHttpClient();
        }

        if (params == null) {
            params = new LinkedHashMap<>();
        }

        Map<String, String> headers = new LinkedHashMap<>();

        Request request = newRequest(url, method, headers, params, getRequestTag(), false, 3);
        Response response = httpClient.newCall(request).execute();

        String jsonData = processResponse(response);

        long requestEndTimeMillis = System.currentTimeMillis();
        JLog.d("requestEnd:" + requestEndTimeMillis + ", spend:" + (requestEndTimeMillis - requestStartTimeMillis));

        return jsonData;
    }
}
