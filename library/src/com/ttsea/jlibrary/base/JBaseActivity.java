package com.ttsea.jlibrary.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.common.ExitApplication;
import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.component.dialog.MyAlertDialog;
import com.ttsea.jlibrary.component.dialog.MyDialog;
import com.ttsea.jlibrary.component.dialog.MyProgressDialog;
import com.ttsea.jlibrary.debug.ViewServer;
import com.ttsea.jlibrary.interfaces.OnActivityLifeChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity基类，这里只对UI进行处理 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/4/11 20:13 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/4/11 20:13
 */
public class JBaseActivity extends Activity {
    public Activity mActivity;
    private MyProgressDialog progressDialog;
    private MyDialog myDialog;
    private Toast mToast;
    private List<OnActivityLifeChangedListener> onActivityLifeChangedListenerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        ExitApplication.getInstance().addActivity(mActivity);
        //调试模式下，使其能够使用hierarchyview
        if (JLog.isDebugMode()) {
            ViewServer.get(mActivity).addWindow(this);
        }

        init();
    }

    private void init() {
        onActivityLifeChangedListenerList = new ArrayList<OnActivityLifeChangedListener>();
        myDialog = new MyDialog(mActivity, R.style.my_dialog_theme, null, 120, 120);
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (int i = 0; i < onActivityLifeChangedListenerList.size(); i++) {
            OnActivityLifeChangedListener l = onActivityLifeChangedListenerList.get(i);
            if (l != null) {
                l.onStart();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //调试模式下，使其能够使用hierarchyview
        if (JLog.isDebugMode()) {
            ViewServer.get(mActivity).setFocusedWindow(this);
        }
        for (int i = 0; i < onActivityLifeChangedListenerList.size(); i++) {
            OnActivityLifeChangedListener l = onActivityLifeChangedListenerList.get(i);
            if (l != null) {
                l.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i = 0; i < onActivityLifeChangedListenerList.size(); i++) {
            OnActivityLifeChangedListener l = onActivityLifeChangedListenerList.get(i);
            if (l != null) {
                l.onPause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (int i = 0; i < onActivityLifeChangedListenerList.size(); i++) {
            OnActivityLifeChangedListener l = onActivityLifeChangedListenerList.get(i);
            if (l != null) {
                l.onStop();
            }
        }
    }


    @Override
    protected void onDestroy() {
        //调试模式下，使其能够使用hierarchyview
        if (JLog.isDebugMode()) {
            ViewServer.get(mActivity).removeWindow(this);
        }
        while (!onActivityLifeChangedListenerList.isEmpty()) {
            OnActivityLifeChangedListener l = onActivityLifeChangedListenerList.remove(0);
            if (l != null) {
                l.onDestroy();
            }
        }

        super.onDestroy();
    }

    /** 显示加载框 */
    public void showProgress(String title, String message, boolean canCancel) {
        if (mActivity != null && !mActivity.isFinishing() && !isProgressShowing()) {
            progressDialog = MyProgressDialog.show(mActivity, title, message,
                    canCancel);
            progressDialog.setCanceledOnTouchOutside(canCancel);
        }
    }

    /** 关闭加载框 */
    public void dismissProgress() {
        if (mActivity != null && !mActivity.isFinishing()
                && progressDialog != null && isProgressShowing()) {
            progressDialog.dismiss();
        }
    }

    /** 加载框进行中 */
    public boolean isProgressShowing() {
        if (progressDialog != null) {
            return progressDialog.isShowing();
        }
        return false;
    }

    /** 显示dialog */
    public void showDialog(String msg, boolean canceledOnTouchOutside) {
        showDialog(msg, canceledOnTouchOutside, true);
    }

    /** 显示dialog */
    public void showDialog(String msg, boolean canceledOnTouchOutside, boolean cancelable) {
        showDialog(msg, null, canceledOnTouchOutside, cancelable);
    }

    /** 显示dialog */
    public void showDialog(String msg, DialogInterface.OnDismissListener listener, boolean canceledOnTouchOutside, boolean cancelable) {
        if (myDialog == null || myDialog.isShowing()) {
            return;
        }
        myDialog.setOnDismissListener(listener);
        myDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        myDialog.setCancelable(cancelable);
        myDialog.show(msg);
    }

    /** 关闭dialog */
    public void dismissDialog() {
        if (myDialog != null && myDialog.isShowing()
                && !mActivity.isFinishing()) {
            myDialog.dismiss();
        }
    }

    /** 弹出MyAlertDialog */
    public void showAlertDialog(String title, String msg,
                                String positiveTxt, DialogInterface.OnClickListener positiveListener,
                                boolean canceledOnTouchOutside,
                                boolean cancelable) {
        MyAlertDialog dialog = createAlertDialog(title, msg,
                positiveTxt, positiveListener,
                null, null,
                canceledOnTouchOutside,
                cancelable);
        dialog.show();
    }

    /** 弹出MyAlertDialog */
    public void showAlertDialog(String title, String msg,
                                String positiveTxt, DialogInterface.OnClickListener positiveListener,
                                String negativeTxt, DialogInterface.OnClickListener negativeListener,
                                boolean canceledOnTouchOutside,
                                boolean cancelable) {
        MyAlertDialog dialog = createAlertDialog(title, msg,
                positiveTxt, positiveListener,
                negativeTxt, negativeListener,
                canceledOnTouchOutside,
                cancelable);
        dialog.show();
    }

    /** 创建MyAlertDialog */
    public MyAlertDialog createAlertDialog(String title, String msg,
                                           String positiveTxt, DialogInterface.OnClickListener positiveListener,
                                           String negativeTxt, DialogInterface.OnClickListener negativeListener,
                                           boolean canceledOnTouchOutside,
                                           boolean cancelable) {
        MyAlertDialog.Builder builder = createAlertDialogBuilder(title, msg,
                positiveTxt, positiveListener,
                negativeTxt, negativeListener,
                canceledOnTouchOutside,
                cancelable);
        return builder.create();
    }

    /** 创建MyAlertDialog.Builder实例 */
    public MyAlertDialog.Builder createAlertDialogBuilder(String title, String msg,
                                                          String positiveTxt, DialogInterface.OnClickListener positiveListener,
                                                          String negativeTxt, DialogInterface.OnClickListener negativeListener,
                                                          boolean canceledOnTouchOutside,
                                                          boolean cancelable) {
        MyAlertDialog.Builder builder = new MyAlertDialog.Builder(mActivity);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveTxt, positiveListener);
        builder.setNegativeButton(negativeTxt, negativeListener);
        builder.setCanceledOnTouchOutside(canceledOnTouchOutside);
        builder.setCancelable(cancelable);

        return builder;
    }

    /**
     * 通过字串ID获取到字串
     *
     * @param resId string id
     * @return String
     * @author Jason
     */
    public String getStringById(int resId) {
        return mActivity.getResources().getString(resId);
    }

    /**
     * 通过id获取颜色值
     *
     * @param resId color id
     * @return int
     */
    public int getColorById(int resId) {
        return mActivity.getResources().getColor(resId);
    }

    /** toast 消息 */
    public void toastMessage(String msg) {
        if (msg != null) {
            Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /** toast 消息 */
    public void toastMessage(int resId) {
        toastMessage(getStringById(resId));
    }

    /** 单例toast，toast不会重复 */
    public void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mActivity, text, Toast.LENGTH_SHORT);
        }
        mToast.setText(text);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    /** 单例toast，toast不会重复 */
    public void showToast(int resId) {
        showToast(getStringById(resId));
    }

    /** finish该acitivity，并且设置resultCode */
    public void finish(int resultCode) {
        mActivity.setResult(resultCode);
        mActivity.finish();
    }

    /** finish该acitivity，并且设置resultCode和Intent */
    public void finish(int resultCode, Intent data) {
        mActivity.setResult(resultCode, data);
        mActivity.finish();
    }

    /** 添加Activity生命周期监听器 */
    public void addActivityLifeCycleListener(OnActivityLifeChangedListener l) {
        if (!onActivityLifeChangedListenerList.contains(l)) {
            onActivityLifeChangedListenerList.add(l);
        }
    }

    /** 移除指定的Activity生命周期监听器 */
    public void removeActivityLifeCycleListener(OnActivityLifeChangedListener l) {
        onActivityLifeChangedListenerList.remove(l);
    }

    /** 显示正常的View */
    public void showNormalView() {
    }

    /** 显示无数据的View */
    public void showNoDataView() {
    }

    /** 显示正在加载的View */
    public void showLoadingView() {
    }

    /** 显示数据异常的View */
    public void showErrorView() {
    }
}
