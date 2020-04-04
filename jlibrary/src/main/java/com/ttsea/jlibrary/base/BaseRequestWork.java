package com.ttsea.jlibrary.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.customHttpClient.Http;
import com.ttsea.jlibrary.common.customHttpClient.HttpClient;
import com.ttsea.jlibrary.common.utils.AppInformationUtils;
import com.ttsea.jlibrary.common.utils.NetWorkUtils;
import com.ttsea.jlibrary.common.utils.Utils;
import com.ttsea.jlibrary.debug.JLog;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * // to do <br>
 * <p>
 * <b>date:</b> 2017/2/17 18:27 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
abstract class BaseRequestWork {
    private final String TAG = "BaseRequestWork";

    private final int defaultMaxAgeSeconds = 0;
    private final int defaultMaxStaleSeconds = 3;

    protected Context mContext;
    private Handler callbackHandler;

    /** 是否是调试模式，true:会打印log，false:不会打印log */
    private boolean debug = true;

    public BaseRequestWork(Context context) {
        this.mContext = context;
        this.callbackHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 设置是否是调试模式
     *
     * @param debug true:会打印log，false:不会打印log，Log.e不受控制
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * 添加一个异步请求，并执行该请求，请求结束后将会回调{@link #handleNetWorkData(String, int)} 和
     * {@link #handleErrorResponse(String, int)}<br>
     * okHttpClient为{@link HttpClient#getHttpClient(Context)}<br>
     * headers为("version", "app版本号")和httconfig.xml文件中headers所配置的值<br>
     * requestTag默认为{@link #getRequestTag()}<br>
     * maxAgeSeconds默认为{@link #defaultMaxAgeSeconds}<br>
     * maxStaleSeconds默认为{@link #defaultMaxStaleSeconds}<br>
     * shouldCache为false<br>
     * method为{@link Http.Method#POST}<br>
     *
     * @param url         url，请求地址，不能为空
     * @param params      请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *                    {@link Http.Method#GET}，则参数会拼接到url后面
     * @param requestCode 自定义请求code
     */
    public void addRequest(@NonNull String url, @Nullable Map<String, String> params, int requestCode) {
        addRequest(url, Http.Method.POST, params, requestCode);
    }

    /**
     * 添加一个异步请求，并执行该请求，请求结束后将会回调{@link #handleNetWorkData(String, int)} 和
     * {@link #handleErrorResponse(String, int)}<br>
     * okHttpClient为{@link HttpClient#getHttpClient(Context)}<br>
     * headers为("version", "app版本号")和httconfig.xml文件中headers所配置的值<br>
     * requestTag默认为{@link #getRequestTag()}<br>
     * maxAgeSeconds默认为{@link #defaultMaxAgeSeconds}<br>
     * maxStaleSeconds默认为{@link #defaultMaxStaleSeconds}<br>
     * shouldCache为false<br>
     *
     * @param url         url，请求地址，不能为空
     * @param method      请求方法,默认为{@link Http.Method#POST}, see {@link Http.Method}
     * @param params      请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *                    {@link Http.Method#GET}，则参数会拼接到url后面
     * @param requestCode 自定义请求code
     */
    public void addRequest(@NonNull String url, @Nullable String method, Map<String, String> params, int requestCode) {
        addRequest(url, method, params, false, requestCode);
    }

    /**
     * 添加一个异步请求，并执行该请求，请求结束后将会回调{@link #handleNetWorkData(String, int)} 和
     * {@link #handleErrorResponse(String, int)}<br>
     * okHttpClient为{@link HttpClient#getHttpClient(Context)}<br>
     * headers为("version", "app版本号")和httconfig.xml文件中headers所配置的值<br>
     * requestTag默认为{@link #getRequestTag()}<br>
     * maxAgeSeconds默认为{@link #defaultMaxAgeSeconds}<br>
     * maxStaleSeconds默认为{@link #defaultMaxStaleSeconds}<br>
     *
     * @param url         url，请求地址，不能为空
     * @param method      请求方法,默认为{@link Http.Method#POST}, see {@link Http.Method}
     * @param params      请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *                    {@link Http.Method#GET}，则参数会拼接到url后面
     * @param shouldCache 是否需要缓存，该项只对请求方法为{@link Http.Method#GET}
     *                    时生效，因为OkHttp3只会缓存使用GET方法的请求<br>
     * @param requestCode 自定义请求code
     */
    public void addRequest(@NonNull String url, String method, @Nullable Map<String, String> params,
                           boolean shouldCache, int requestCode) {
        addRequest(null, url, method, null, params, getRequestTag(),
                shouldCache, defaultMaxStaleSeconds, requestCode);
    }

    /**
     * 添加一个异步请求，并执行该请求，请求结束后将会回调{@link #handleNetWorkData(String, int)} 和
     * {@link #handleErrorResponse(String, int)}<br>
     * maxAgeSeconds为{@link #defaultMaxAgeSeconds}
     *
     * @param okHttpClient    当okHttpClient为null时，采用默认的okHttpClient, see
     *                        {@link HttpClient#getHttpClient(Context)}
     * @param url             url，请求地址，不能为空
     * @param method          请求方法,默认为{@link Http.Method#POST}, see {@link Http.Method}
     * @param headers         请求头，默认会有("version", "app版本号")和httconfig.xml文件中headers所配置的值
     * @param params          请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *                        {@link Http.Method#GET}，则参数会拼接到url后面
     * @param requestTag      请求标识，可根据这个tag来取消请求
     * @param shouldCache     是否需要缓存，该项只对请求方法为{@link Http.Method#GET}
     *                        时生效，因为OkHttp3只会缓存使用GET方法的请求<br>
     * @param maxStaleSeconds CacheControl:
     *                        max-age参数，该参数只会在shouldCache为true并且网络可用的时候才生效，建议设置为3<br>
     * @param requestCode     自定义请求code
     */
    public void addRequest(@Nullable OkHttpClient okHttpClient, @NonNull String url,
                           @Nullable String method, @Nullable Map<String, String> headers,
                           @Nullable Map<String, String> params, @Nullable String requestTag,
                           boolean shouldCache, int maxStaleSeconds, final int requestCode) {
        addRequest(okHttpClient, url, method, headers, params, requestTag,
                shouldCache, defaultMaxAgeSeconds, maxStaleSeconds, requestCode);
    }

    /**
     * 添加一个异步请求，并执行该请求，请求结束后将会回调{@link #handleNetWorkData(String, int)} 和
     * {@link #handleErrorResponse(String, int)}<br>
     *
     * @param okHttpClient    当okHttpClient为null时，采用默认的okHttpClient, see
     *                        {@link HttpClient#getHttpClient(Context)}
     * @param url             url，请求地址，不能为空
     * @param method          请求方法,默认为{@link Http.Method#POST}, see {@link Http.Method}
     * @param headers         请求头，默认会有("version", "app版本号")和httconfig.xml文件中headers所配置的值
     * @param params          请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *                        {@link Http.Method#GET}，则参数会拼接到url后面
     * @param requestTag      请求标识，可根据这个tag来取消请求
     * @param shouldCache     是否需要缓存，该项只对请求方法为{@link Http.Method#GET}
     *                        时生效，因为OkHttp3只会缓存使用GET方法的请求<br>
     * @param maxAgeSeconds   CacheControl: max-age参数，该参数只会在shouldCache为true并且网络可用的时候才生效<br>
     *                        表示不愿接受年龄大于指定秒数(maxAgeSeconds)的响应（一般设置为0）
     * @param maxStaleSeconds CacheControl:
     *                        max-age参数，该参数只会在shouldCache为true并且网络可用的时候才生效，建议设置为3<br>
     * @param requestCode     自定义请求code
     */
    public void addRequest(@Nullable OkHttpClient okHttpClient, @NonNull String url,
                           @Nullable String method, @Nullable Map<String, String> headers,
                           @Nullable Map<String, String> params, @Nullable String requestTag,
                           boolean shouldCache, int maxAgeSeconds, int maxStaleSeconds, final int requestCode) {

        final long requestStartTimeMillis = System.currentTimeMillis();
        if (debug) {
            JLog.d("==========>requestCode:" + requestCode + ", start:" + requestStartTimeMillis);
        }

        // 这里需要注意callback是运行在OKHttp线程中的，
        // 所以这里我们使用callbackHandler将其设置在主线程中进行回调
        okhttp3.Callback callback = new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                long requestEndTimeMillis = System.currentTimeMillis();
                if (debug) {
                    JLog.d("==========>requestCode:" + requestCode + ", end:" + requestEndTimeMillis
                            + ", spend:" + (requestEndTimeMillis - requestStartTimeMillis));
                }

                if (call.isCanceled()) {
                    if (debug) {
                        JLog.d(TAG, "request is canceled, request:" + call.request().toString());
                    }
                    return;
                }
                e.printStackTrace();

                String errorMsg = e.getMessage();
                JLog.e(TAG, "Exception e:" + errorMsg);
                handleErrorResponseToMainThread(errorMsg, requestCode);
            }

            @Override
            public void onResponse(Call call, Response response) {
                long requestEndTimeMillis = System.currentTimeMillis();
                if (debug) {
                    JLog.d("==========>requestCode:" + requestCode + ", end:" + requestEndTimeMillis
                            + ", spend:" + (requestEndTimeMillis - requestStartTimeMillis));
                }

                if (call.isCanceled()) {
                    if (debug) {
                        JLog.d(TAG, "request is canceled, request:" + call.request().toString());
                    }
                    return;
                }

                try {
                    String jasonData = processResponse(response);
                    handleNetWorkDataToMainThread(jasonData, requestCode);

                } catch (Exception e) {
                    String errorMsg = e.getMessage();
                    if (debug) {
                        JLog.e("Exception e:" + errorMsg);
                    }
                    handleErrorResponseToMainThread(errorMsg, requestCode);
                }
            }
        };

        addRequest(okHttpClient, url, method, headers, params, requestTag,
                shouldCache, maxAgeSeconds, maxStaleSeconds, callback);
    }

    /**
     * 添加一个异步请求，并执行该请求，请求结束后将会回调{@link #handleNetWorkData(String, int)} 和
     * {@link #handleErrorResponse(String, int)}<br>
     *
     * @param okHttpClient    当okHttpClient为null时，采用默认的okHttpClient, see
     *                        {@link HttpClient#getHttpClient(Context)}
     * @param url             url，请求地址，不能为空
     * @param method          请求方法,默认为{@link Http.Method#POST}, see {@link Http.Method}
     * @param headers         请求头，默认会有("version", "app版本号")和httconfig.xml文件中headers所配置的值
     * @param params          请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *                        {@link Http.Method#GET}，则参数会拼接到url后面
     * @param requestTag      请求标识，可根据这个tag来取消请求
     * @param shouldCache     是否需要缓存，该项只对请求方法为{@link Http.Method#GET}
     *                        时生效，因为OkHttp3只会缓存使用GET方法的请求<br>
     * @param maxAgeSeconds   CacheControl: max-age参数，该参数只会在shouldCache为true并且网络可用的时候才生效<br>
     *                        表示不愿接受年龄大于指定秒数(maxAgeSeconds)的响应（一般设置为0）
     * @param maxStaleSeconds CacheControl:
     *                        max-age参数，该参数只会在shouldCache为true并且网络可用的时候才生效，建议设置为3<br>
     * @param callback        请求回调<br>
     */
    public void addRequest(@Nullable OkHttpClient okHttpClient, @NonNull String url,
                           @Nullable String method, @Nullable Map<String, String> headers,
                           @Nullable Map<String, String> params, @Nullable String requestTag,
                           boolean shouldCache, int maxAgeSeconds, int maxStaleSeconds,
                           @NonNull okhttp3.Callback callback) {

        Request request = newRequest(url, method, headers, params, requestTag,
                shouldCache, maxAgeSeconds, maxStaleSeconds);

        if (okHttpClient == null) {
            okHttpClient = getDefaultOkHttpClient();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    // ---------------------------------
    // -- 以下是 新建一个okhttp3.Request
    // ---------------------------------

    /**
     * 只新建一个okhttp3.Request，这个请求不会被执行<br>
     * headers为("version", "app版本号")和httconfig.xml文件中headers所配置的值<br>
     * requestTag默认为{@link #getRequestTag()}<br>
     * maxAgeSeconds默认为{@link #defaultMaxAgeSeconds}<br>
     * maxStaleSeconds默认为{@link #defaultMaxStaleSeconds}<br>
     * shouldCache为false<br>
     * method为{@link Http.Method#POST}<br>
     *
     * @param url    url，请求地址，不能为空
     * @param params 请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *               {@link Http.Method#GET}，则参数会拼接到url后面
     */
    public Request newRequest(@NonNull String url, @Nullable Map<String, String> params) {
        return newRequest(url, Http.Method.POST, params);
    }

    /**
     * 只新建一个okhttp3.Request，这个请求不会被执行<br>
     * headers为("version", "app版本号")和httconfig.xml文件中headers所配置的值<br>
     * requestTag默认为{@link #getRequestTag()}<br>
     * maxAgeSeconds默认为{@link #defaultMaxAgeSeconds}<br>
     * maxStaleSeconds默认为{@link #defaultMaxStaleSeconds}<br>
     * shouldCache为false<br>
     *
     * @param url    url，请求地址，不能为空
     * @param method 请求方法,默认为{@link Http.Method#POST}, see {@link Http.Method}
     * @param params 请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *               {@link Http.Method#GET}，则参数会拼接到url后面
     */
    public Request newRequest(@NonNull String url, @Nullable String method, @Nullable Map<String, String> params) {
        return newRequest(url, method, params, false);
    }

    /**
     * 只新建一个okhttp3.Request，这个请求不会被执行<br>
     * headers为("version", "app版本号")和httconfig.xml文件中headers所配置的值<br>
     * requestTag默认为{@link #getRequestTag()}<br>
     * maxAgeSeconds默认为{@link #defaultMaxAgeSeconds}<br>
     * maxStaleSeconds默认为{@link #defaultMaxStaleSeconds}<br>
     *
     * @param url         url，请求地址，不能为空
     * @param method      请求方法,默认为{@link Http.Method#POST}, see {@link Http.Method}
     * @param params      请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *                    {@link Http.Method#GET}，则参数会拼接到url后面
     * @param shouldCache 是否需要缓存，该项只对请求方法为{@link Http.Method#GET}
     *                    时生效，因为OkHttp3只会缓存使用GET方法的请求<br>
     */
    public Request newRequest(@NonNull String url, @Nullable String method, @Nullable Map<String, String> params,
                              boolean shouldCache) {
        return newRequest(url, method, null, params, getRequestTag(), shouldCache, defaultMaxStaleSeconds);
    }

    /**
     * 只新建一个okhttp3.Request，这个请求不会被执行<br>
     * maxAgeSeconds为{@link #defaultMaxAgeSeconds}
     *
     * @param url             url，请求地址，不能为空
     * @param method          请求方法,默认为{@link Http.Method#POST}, see {@link Http.Method}
     * @param headers         请求头，默认会有("version", "app版本号")和httconfig.xml文件中headers所配置的值
     * @param params          请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *                        {@link Http.Method#GET}，则参数会拼接到url后面
     * @param requestTag      请求标识，可根据这个tag来取消请求
     * @param shouldCache     是否需要缓存，该项只对请求方法为{@link Http.Method#GET}
     *                        时生效，因为OkHttp3只会缓存使用GET方法的请求<br>
     * @param maxStaleSeconds CacheControl:
     *                        max-age参数，该参数只会在shouldCache为true并且网络可用的时候才生效，建议设置为3<br>
     */
    public Request newRequest(@NonNull String url, @Nullable String method, @Nullable Map<String, String> headers,
                              @Nullable Map<String, String> params, @Nullable String requestTag,
                              boolean shouldCache, int maxStaleSeconds) {
        return newRequest(url, method, headers, params, requestTag,
                shouldCache, defaultMaxAgeSeconds, maxStaleSeconds);
    }

    /**
     * 只新建一个okhttp3.Request，这个请求不会被执行<br>
     *
     * @param url             url，请求地址，不能为空
     * @param method          请求方法,默认为{@link Http.Method#POST}, see {@link Http.Method}
     * @param headers         请求头，默认会有("version", "app版本号")和httconfig.xml文件中headers所配置的值
     * @param params          请求体，默认会有httconfig.xml文件中formBody所配置的值，如果请求方法为
     *                        {@link Http.Method#GET}，则参数会拼接到url后面
     * @param requestTag      请求标识，可根据这个tag来取消请求
     * @param shouldCache     是否需要缓存，该项只对请求方法为{@link Http.Method#GET}
     *                        时生效，因为OkHttp3只会缓存使用GET方法的请求<br>
     * @param maxAgeSeconds   CacheControl: max-age参数，该参数只会在shouldCache为true并且网络可用的时候才生效<br>
     *                        表示不愿接受年龄大于指定秒数(maxAgeSeconds)的响应（一般设置为0）
     * @param maxStaleSeconds CacheControl:
     *                        max-age参数，该参数只会在shouldCache为true并且网络可用的时候才生效，建议设置为3<br>
     */
    @SuppressLint("DefaultLocale")
    public Request newRequest(@NonNull String url, @Nullable String method,
                              @Nullable Map<String, String> headers, @Nullable Map<String, String> params,
                              @Nullable String requestTag, boolean shouldCache, int maxAgeSeconds, int maxStaleSeconds) {
        if (method == null) {
            method = Http.Method.POST;
            if (debug) {
                JLog.d("request method is null, set method as default: POST");
            }
        }

        // 设置请求头
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder = readHeadersFromHttpConfig(headersBuilder);
        headersBuilder.set("version", AppInformationUtils.getVersionName(mContext));
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                // 这里使用set，确保key-value一一对应
                headersBuilder.set(entry.getKey(), entry.getValue());
            }
        }

        // 设置请求体
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder = readFormBodyFromHttpConfig(bodyBuilder);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                // entry和key为空时，则跳过
                if (entry == null || Utils.isEmpty(entry.getKey())) {
                    continue;
                }

                String key = entry.getKey();
                String value = entry.getValue() == null ? "" : entry.getValue();

                // if ("platform".equals(key) || "version".equals(key))
                // {//跳过重复的值
                // if (debug) {
                // JLog.d(TAG, "Skip repeat value:" + entry.getKey());
                // }
                // continue;
                // }

                bodyBuilder.add(key, value);
            }
        }

        RequestBody requestBody = bodyBuilder.build();

        if (method.equals(Http.Method.GET)) {
            try {
                // 将requestBody参数拼接到urls后面
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                String paramStr = buffer.readUtf8();
                if (!Utils.isEmpty(paramStr)) {
                    url = url + "?" + paramStr;
                }
                // "GET"方法不需要传入requestBody，否则会引起报错
                requestBody = null;

            } catch (IOException e) {
                e.printStackTrace();
                JLog.e(TAG, "Exception e:" + e.toString());
            }
        }

        CacheControl cacheControl;
        if (shouldCache) {// 如果允许使用缓存
            if (!NetWorkUtils.isAvailable(mContext)) {// 网络不可以则强制使用缓存
                cacheControl = CacheControl.FORCE_CACHE;

            } else {// 网络可用，则需要根据缓存是否过期来判断是否使用缓存
                cacheControl = new CacheControl.Builder()
                        .maxAge(maxAgeSeconds, TimeUnit.SECONDS)
                        .maxStale(maxStaleSeconds, TimeUnit.SECONDS).build();
            }

        } else {
            cacheControl = CacheControl.FORCE_NETWORK;
        }

        Request.Builder builder = new Request.Builder();
        builder.url(url).method(method.toUpperCase(), requestBody)
                .tag(requestTag).headers(headersBuilder.build())
                .cacheControl(cacheControl);

        Request request = builder.build();

        if (debug) {
            JLog.d(TAG, "newRequest, url:" + url + ", method:" + method
                    + ", requestTag:" + requestTag + ", shouldCache:"
                    + shouldCache + ", maxAgeSeconds:" + maxAgeSeconds
                    + ", maxStaleSeconds:" + maxStaleSeconds + "\n"
                    + "requestBody:" + bodyToString(request) + "\n"
                    + "request headers:" + headerToString(request.headers())
                    + "\n--");
        }

        return request;
    }

    // ---------------------------------
    // -- 以上是新建一个okhttp3.Request
    // ---------------------------------

    /**
     * 处理okHttp3.Response<br>
     * 不管时使用异步(addRequest)还是同步(newRequest)请求，最终结果都会回调这个方法
     *
     * @param response Response
     * @return ResponseResult
     */
    @NonNull
    protected String processResponse(@Nullable Response response) throws Exception {
        if (response == null) {
            throw new IOException("response is null");
        }

        //不成功，则抛出异常
        if (!response.isSuccessful()) {
            String errorBody = "";

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                errorBody = responseBody.string();
            }

            String errorMsg = "Not handle response code, responseCode:" + response.code();
            if (response.request() != null) {
                errorMsg = errorMsg + ", url:" + response.request().url();
            }
            errorMsg = errorMsg + ", errorBody:" + errorBody;

            JLog.e(TAG, "Exception e:" + errorMsg);

            throw new IOException(errorMsg);
        }

        String jsonData = "";

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            jsonData = responseBody.string();
            if (debug) {
                JLog.d(TAG, "jsonData:" + jsonData);
            }
        } else {
            JLog.w("responseBody is null, so jsonData is empty");
        }

        return jsonData;
    }

    private synchronized void handleErrorResponseToMainThread(final String errorMsg, final int requestCode) {
        callbackHandler.post(new Runnable() {
            @Override
            public void run() {
                handleErrorResponse(errorMsg, requestCode);
            }
        });
    }

    private synchronized void handleNetWorkDataToMainThread(final String jsonData, final int requestCode) {
        callbackHandler.post(new Runnable() {
            @Override
            public void run() {
                handleNetWorkData(jsonData, requestCode);
            }
        });
    }

    /**
     * 请求数据出错后，的处理方法<br>
     * 这里建议使用 synchronized 修饰该方法，因为同一个界面可能会存在多个请求数据的线程对它操作
     *
     * @param errorMsg    错误信息
     * @param requestCode 用户定义的code
     */
    public abstract void handleErrorResponse(String errorMsg, int requestCode);

    /**
     * 处理请求返回的数据<br>
     * 这里建议使用 synchronized 修饰该方法，因为同一个界面可能会存在多个请求数据的线程对它操作
     *
     * @param jsonData    请求返回的数据
     * @param requestCode 用户定义的code
     * @return 返回true表示已经处理数据，否则表示未处理数据
     */
    public abstract boolean handleNetWorkData(String jsonData, int requestCode);

    /**
     * 用户自定义的tag，用于标记okHttp request，便于取消网络请求
     *
     * @return String
     */
    public abstract String getRequestTag();

    /** 通过tag取消指定请求 */
    protected void cancelRequest(String tag) {
        cancelRequest(HttpClient.getHttpClient(mContext), tag);
    }

    /**
     * 获取默认的OkHttpClient<br>
     * {@link HttpClient#getHttpClient(Context)}
     *
     * @return OkHttpClient
     */
    protected OkHttpClient getDefaultOkHttpClient() {
        return HttpClient.getHttpClient(mContext);
    }

    /** 通过tag取消指定请求 */
    protected void cancelRequest(OkHttpClient client, String tag) {
        if (client == null || tag == null) {
            return;
        }
        Dispatcher dispatcher = client.dispatcher();
        synchronized (dispatcher) {
            for (Call call : dispatcher.queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                    if (debug) {
                        JLog.d(TAG, "cancel queued call, url:" + call.request().url());
                    }
                }
            }
            for (Call call : dispatcher.runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                    if (debug) {
                        JLog.d(TAG, "cancel running call, url:" + call.request().url());
                    }
                }
            }
        }
    }

    /** 取消所有的请求 */
    protected void cancelAllRequest(OkHttpClient client) {
        if (client == null) {
            return;
        }
        client.dispatcher().cancelAll();
        if (debug) {
            JLog.d(TAG, "all request are canceled");
        }
    }

    /**
     * 将request body拼接成String，如：
     * "platform=android&version=1.0.1&login_pass=123456&login_name=aaa108"
     */
    private String bodyToString(Request request) {
        try {
            Request copyRequest = request.newBuilder().build();
            if (copyRequest.body() == null) {
                return null;
            }
            Buffer buffer = new Buffer();
            copyRequest.body().writeTo(buffer);
            String body = buffer.readUtf8();

            // String result = URLDecoder.decode(body);
            String result = body;
            return Utils.isEmpty(result) ? null : result;

        } catch (final IOException e) {
            return "";
        }
    }

    /** 将header拼接成String，如："User-Agent: mvp, Cache-Control: no-cache" */
    private String headerToString(Headers headers) {
        if (headers == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0, size = headers.size(); i < size; i++) {
            result.append(headers.name(i)).append(":").append(headers.value(i))
                    .append(", ");
        }
        return result.toString();
    }

    /**
     * 从httpconfig.xml文件中读取配置好的headers
     *
     * @param builder Headers.builder
     * @return Headers.Builder
     */
    private Headers.Builder readHeadersFromHttpConfig(Headers.Builder builder) {
        String regularExpression = ",";
        String[] headers = mContext.getResources().getStringArray(
                R.array.headers);

        if (headers.length == 0) {
            return builder;
        }

        for (String value : headers) {
            String[] keyAndValue = value.split(regularExpression);
            if (keyAndValue.length != 2) {
                JLog.e(TAG, "keyAndValue length no equals 2, value:" + value);
                continue;
            }
            builder.set(keyAndValue[0], keyAndValue[1]);
        }
        return builder;
    }

    /**
     * 从httpconfig.xml文件中读取配置好的FormBody
     *
     * @param builder FormBody.Builder
     * @return FormBody.Builder
     */
    private FormBody.Builder readFormBodyFromHttpConfig(FormBody.Builder builder) {
        String regularExpression = ",";
        String[] bodys = mContext.getResources().getStringArray(
                R.array.formBody);

        if (bodys.length == 0) {
            return builder;
        }

        for (String value : bodys) {
            String[] keyAndValue = value.split(regularExpression);
            if (keyAndValue.length != 2) {
                JLog.e(TAG, "keyAndValue length no equals 2, value:" + value);
                continue;
            }
            builder.add(keyAndValue[0], keyAndValue[1]);
        }
        return builder;
    }
}
