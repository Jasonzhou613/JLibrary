package com.ttsea.jlibrary.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ttsea.jlibrary.JLibrary;
import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.component.dialog.MyAlertDialog;
import com.ttsea.jlibrary.component.dialog.MyDialog;
import com.ttsea.jlibrary.component.dialog.MyProgressDialog;
import com.ttsea.jlibrary.debug.ViewServer;
import com.ttsea.jlibrary.common.interfaces.OnActivityLifeChangedListener;
import com.ttsea.jlibrary.common.interfaces.OnSingleClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * /Activity基类，这里只对UI进行处理 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class JBaseActivity extends AppCompatActivity {
    public Activity mActivity;
    public OnSingleClickListener mOnSingleClickListener;

    private MyProgressDialog progressDialog;
    private MyDialog myDialog;
    private Toast mToast;
    private List<OnActivityLifeChangedListener> mOnActivityLifeChangedListenerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        JBaseApplication.addActivity(mActivity);
        //调试模式下，使其能够使用hierarchyviewer
        if (JLibrary.isDebugMode()) {
            ViewServer.get(mActivity).addWindow(mActivity);
        } else {
        }

        init();
    }

    private void init() {
        mOnActivityLifeChangedListenerList = new ArrayList<OnActivityLifeChangedListener>();
        myDialog = new MyDialog(mActivity, R.style.my_dialog_theme, null, 120, 120);
        mOnSingleClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                JBaseActivity.this.onSingleClick(v);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        for (int i = 0; i < mOnActivityLifeChangedListenerList.size(); i++) {
            OnActivityLifeChangedListener l = mOnActivityLifeChangedListenerList.get(i);
            if (l != null) {
                l.onStart();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //调试模式下，使其能够使用hierarchyview
        if (JLibrary.isDebugMode()) {
            ViewServer.get(mActivity).setFocusedWindow(mActivity);
        }

        for (int i = 0; i < mOnActivityLifeChangedListenerList.size(); i++) {
            OnActivityLifeChangedListener l = mOnActivityLifeChangedListenerList.get(i);
            if (l != null) {
                l.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        for (int i = 0; i < mOnActivityLifeChangedListenerList.size(); i++) {
            OnActivityLifeChangedListener l = mOnActivityLifeChangedListenerList.get(i);
            if (l != null) {
                l.onPause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        for (int i = 0; i < mOnActivityLifeChangedListenerList.size(); i++) {
            OnActivityLifeChangedListener l = mOnActivityLifeChangedListenerList.get(i);
            if (l != null) {
                l.onStop();
            }
        }
    }

    @Override
    protected void onDestroy() {
        //调试模式下，使其能够使用hierarchyview
        if (JLibrary.isDebugMode()) {
            ViewServer.get(mActivity).removeWindow(mActivity);
        }
        while (!mOnActivityLifeChangedListenerList.isEmpty()) {
            OnActivityLifeChangedListener l = mOnActivityLifeChangedListenerList.remove(0);
            if (l != null) {
                l.onDestroy();
            }
        }
        JBaseApplication.removeActivity(mActivity);

        super.onDestroy();
    }

    public void dismissAllDialog() {
        dismissDialog();
        dismissProgress();
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
        showDialog(msg, null, null, canceledOnTouchOutside, true);
    }

    /** 显示dialog */
    public void showDialog(String msg, boolean canceledOnTouchOutside, boolean cancelable) {
        showDialog(msg, null, null, canceledOnTouchOutside, cancelable);
    }

    /** 显示dialog */
    public void showDialog(String msg, DialogInterface.OnDismissListener dismissListener) {
        showDialog(msg, dismissListener, null, false, true);
    }

    /** 显示dialog */
    public void showDialog(String msg, DialogInterface.OnKeyListener onKeyListener) {
        showDialog(msg, null, onKeyListener, false, true);
    }

    /** 显示dialog */
    public void showDialog(String msg, DialogInterface.OnDismissListener dismissListener, DialogInterface.OnKeyListener keyListener,
                           boolean canceledOnTouchOutside, boolean cancelable) {
        if (myDialog == null || myDialog.isShowing()) {
            return;
        }
        myDialog.setOnDismissListener(dismissListener);
        myDialog.setOnKeyListener(keyListener);
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

    /** finish该activity，并且设置resultCode */
    public void finish(int resultCode) {
        mActivity.setResult(resultCode);
        mActivity.finish();
    }

    /** finish该activity，并且设置resultCode和Intent */
    public void finish(int resultCode, Intent data) {
        mActivity.setResult(resultCode, data);
        mActivity.finish();
    }

    protected void onSingleClick(View v) {

    }

    /** 添加Activity生命周期监听器 */
    public void addActivityLifeCycleListener(OnActivityLifeChangedListener l) {
        if (!mOnActivityLifeChangedListenerList.contains(l)) {
            mOnActivityLifeChangedListenerList.add(l);
        }
    }

    /** 移除指定的Activity生命周期监听器 */
    public void removeActivityLifeCycleListener(OnActivityLifeChangedListener l) {
        mOnActivityLifeChangedListenerList.remove(l);
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