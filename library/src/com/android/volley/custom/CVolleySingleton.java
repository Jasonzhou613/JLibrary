package com.android.volley.custom;

import android.content.Context;

import com.android.volley.RequestQueue;

/**
 * Volley单列
 */
public class CVolleySingleton {

    private static CVolleySingleton mInstance;
    private RequestQueue mRequestQueue;

    private CVolleySingleton(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = CVolley.newRequestQueue(context.getApplicationContext(), 4);
        }
    }

    public static CVolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            synchronized (CVolleySingleton.class) {
                mInstance = new CVolleySingleton(context.getApplicationContext());
            }
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
