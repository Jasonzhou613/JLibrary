package com.ttsea.jlibrary.common.customHttpClient;

import android.content.Context;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.utils.Utils;
import com.ttsea.jlibrary.debug.JLog;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttpClient <br>
 * <p>
 * <b>date:</b> 2017/7/12 10:25 <br>
 * <b>author:</b> zhijian.zhou <br>
 * <b>version:</b> 1.0 <br>
 */
public class HttpClient {
    private final static String TAG = "HttpClient";
    /**
     * Http请求全局变量
     */
    private static OkHttpClient okHttpClient;

    public static OkHttpClient getHttpClient(Context context) {
        if (okHttpClient == null) {
            synchronized (HttpClient.class) {
                //File file = new File(CacheDirUtils.getDataCacheDir(context) + File.separator + "okHttpCache");
                File file = new File(context.getApplicationContext().getCacheDir() + File.separator + "okHttpCache");
                Cache cache = new Cache(file, 30 * 1024 * 1024);
                if (!file.exists()) {
                    file.mkdirs();
                }

                int connectTimeout = context.getResources().getInteger(R.integer.connectTimeout);
                int writeTimeout = context.getResources().getInteger(R.integer.writeTimeout);
                int readTimeout = context.getResources().getInteger(R.integer.connectTimeout);
                int connectionPoolCount = context.getResources().getInteger(R.integer.connectionPoolCount);

                if (connectTimeout <= 1) {
                    throw new IllegalArgumentException("connectTimeout must be large than 1, connectTimeout:" + connectTimeout);
                }
                if (writeTimeout <= 1) {
                    throw new IllegalArgumentException("writeTimeout must be large than 1, writeTimeout:" + writeTimeout);
                }
                if (readTimeout <= 1) {
                    throw new IllegalArgumentException("readTimeout must be large than 1, readTimeout:" + readTimeout);
                }
                if (connectionPoolCount <= 0) {
                    throw new IllegalArgumentException("connectionPoolCount must be large than 0, connectionPoolCount:" + connectionPoolCount);
                }

                okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(new HttpLogInterceptor("OkHttpLog"))
                        .cache(cache)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .connectionPool(new ConnectionPool(6, 6, TimeUnit.MINUTES))
                        .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                        .build();

                JLog.d(TAG, "cache Dir:" + okHttpClient.cache().directory().getAbsolutePath() +
                        ", \n" + "cacheHitCount: " + okHttpClient.cache().hitCount() +
                        ", \n" + "cacheMaxCount: " + okHttpClient.cache().maxSize() +
                        ", \n" + "cache network count: " + okHttpClient.cache().networkCount() +
                        ", \n" + "cache request count: " + okHttpClient.cache().requestCount() +
                        ", \n" + "connectTimeoutMillis: " + okHttpClient.connectTimeoutMillis() +
                        ", \n" + "writeTimeoutMillis: " + okHttpClient.writeTimeoutMillis() +
                        ", \n" + "readTimeoutMillis: " + okHttpClient.readTimeoutMillis() +
                        ", \n" + "connectionPoolCount: " + okHttpClient.connectionPool().connectionCount() +
                        ", \n" + "connectionPoolIdleCount: " + okHttpClient.connectionPool().idleConnectionCount() +
                        "");
                try {
                    JLog.d(TAG, "cacheSize: " + okHttpClient.cache().size() +
                            "");
                } catch (Exception e) {
                    JLog.e(TAG, "Exception e:" + e.toString());
                }
            }
        }

        return okHttpClient;
    }

    /**
     * response Interceptor,这里将从request中获取Cache-Control信息，然后设置给response，便于缓存
     */
    private static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            Response response = chain.proceed(request);

            //获取请求头部信息
            String cacheControl = "";
            if (request.cacheControl() != null) {
                cacheControl = request.cacheControl().toString();
            }
            Response.Builder builder = response.newBuilder();
            if (!Utils.isEmpty(cacheControl)) {
                builder.removeHeader("Pragma")//清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .header("Cache-Control", "public, " + cacheControl);
            }

            return builder.build();
        }
    };
}
