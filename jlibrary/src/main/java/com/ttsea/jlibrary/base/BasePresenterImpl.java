package com.ttsea.jlibrary.base;

import android.content.Context;

import com.ttsea.jlibrary.debug.JLog;

import okhttp3.Response;

/**
 * // to do <br>
 * <p>
 * <b>date:</b> 2017/2/17 18:27 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class BasePresenterImpl extends BaseRequestWork {//implements BasePresenter
    private final String TAG = "BasePresenterImpl";

    public BasePresenterImpl(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     *
     * @param response Response
     * @return
     * @throws Exception
     */
    @Override
    protected String processResponse(Response response) throws Exception {
        return super.processResponse(response);
    }

    @Override
    public String getRequestTag() {
        return this.getClass().getSimpleName() + "_" + this.hashCode();
    }

    /**
     * 通过String id得到String
     *
     * @param resId 资源Id
     * @return String
     */
    protected String getStringById(int resId) {
        return mContext.getResources().getString(resId);
    }

    /**
     * 通过color id得到color
     *
     * @param resId 资源Id
     * @return color
     */
    protected int getColorById(int resId) {
        return mContext.getResources().getColor(resId);
    }

    /**
     * 通过资源Id获取到对应的数字
     *
     * @param resId 资源Id
     * @return 对应的数字
     */
    protected int getIntByResId(int resId) {
        return mContext.getResources().getInteger(resId);
    }

    @Override
    public synchronized void handleErrorResponse(String errorMsg, int requestCode) {
        JLog.e(TAG, "handleErrorResponse, errorMsg:" + errorMsg);
    }

    @Override
    public synchronized boolean handleNetWorkData(String jsonData, int requestCode) {
        return false;
    }
}