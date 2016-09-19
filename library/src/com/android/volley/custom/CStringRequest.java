/**
 * ff * @Title: HwsStringRequest.java
 *
 * @Package comments.volleybase
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 掌易科技 zfl
 * @date 2015-5-13 下午1:31:53
 * @version V1.0
 */
package com.android.volley.custom;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 */

public class CStringRequest extends StringRequest {
    /** 缓存的时间，单位毫秒 */
    private int cacheExpiresTime = 1000 * 3;
    private Map<String, String> sendHeader = new HashMap<String, String>();

    public CStringRequest(int method, String url, Listener<String> listener,
                          ErrorListener errorListener, int expiresTime) {
        super(method, url, listener, errorListener);
        cacheExpiresTime = expiresTime;
    }

    public CStringRequest(int method, String url, Listener<String> listener,
                          ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, "UTF-8");

            return Response.success(jsonString, CHttpHeaderParser
                    .parseCacheHeaders(response, cacheExpiresTime));

        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        return super.setRetryPolicy(retryPolicy);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        sendHeader.put("User-Agent", System.getProperty("http.agent")
                + "APP=JASYNCHTTP");

        return sendHeader;
    }

    @Override
    public String getCacheKey() {
        String paramsStr = "";
        try {
            Map<String, String> params = getParams();
            //需要对params排序，否则虽然params中包含的值是一样的，但是顺序不一样也会影响获取不到缓存
            Map<String, String> sortedParams = sortByComparator(params);
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                paramsStr = paramsStr + "key=" + entry.getKey() + "-value="
                        + entry.getValue() + "--";
            }
        } catch (AuthFailureError e) {
            e.printStackTrace();
        }
        // 如果参数长度大于100，则取前面100个作为key，防止param太长而导致内存溢出
        if (paramsStr != null && paramsStr.length() > 100) {
            paramsStr = paramsStr.substring(0, 99);
        }

        String url = super.getCacheKey();
        if (url != null && url.length() > 100) {
            url = url.substring(0, 99);
        }

        return (url + paramsStr);
    }

    public void setSendCookie(String cookie) {
        sendHeader.put("Cookie", cookie);
    }

    private static Map<String, String> sortByComparator(Map<String, String> unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());
        // sort list based on comparator
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1 == null || o2 == null
                        || ((Map.Entry) (o1)).getValue() == null
                        || ((Map.Entry) (o2)).getValue() == null) {
                    return 0;
                }
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}