package com.android.volley.custom;

import android.content.Context;

import com.android.volley.RequestQueue;

/**
 * Volley单列
 */
public class CVolleySingleton {

    private static CVolleySingleton mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;


    private CVolleySingleton(Context context) {
        mRequestQueue = getRequestQueue();
    }

    public static CVolleySingleton getInstance(Context context) {
        mContext = context.getApplicationContext();
        if (mInstance == null) {
            synchronized (CVolleySingleton.class) {
                if (null == mInstance) {
                    mInstance = new CVolleySingleton(mContext);
                }
            }
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = CVolley.newRequestQueue(mContext, 4);
        }
        return mRequestQueue;
    }
}
