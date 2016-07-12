package com.ttsea.jlibrary.component.dialog;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ttsea.jlibrary.R;


/**
 * 加载框
 *
 * @author Jason
 * @version [1.0, 2015年8月11日]
 */
public class MyProgressDialog extends ProgressDialog {
    private Context mContext;

    private Animation animRotate;
    private View mView;
    private LinearLayout llyTitle;
    private ImageView ivProgress;
    private TextView tvMessage;
    private TextView tvTitle;

    public MyProgressDialog(Context context) {
        super(context, R.style.my_progress_dialog_theme);
        this.mContext = context;
        init();
    }

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        init();
    }

    @SuppressLint("InflateParams")
    private void init() {
        animRotate = AnimationUtils.loadAnimation(mContext, R.anim.my_progress_dialog);
        mView = LayoutInflater.from(mContext).inflate(R.layout.my_progress_dialog, null);

        llyTitle = (LinearLayout) mView.findViewById(R.id.llyTitle);
        ivProgress = (ImageView) mView.findViewById(R.id.ivLoadingView);
        tvMessage = (TextView) mView.findViewById(R.id.tvMessage);
        tvTitle = (TextView) mView.findViewById(R.id.tvTitle);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIndeterminate(false);
        setContentView(mView);
    }

    @Override
    public void show() {
        ivProgress.startAnimation(animRotate);
        super.show();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (title != null && !title.equals("")) {
            tvTitle.setText(title);
            llyTitle.setVisibility(View.VISIBLE);
        } else {
            llyTitle.setVisibility(View.GONE);
        }
    }

    @Override
    public void setMessage(CharSequence message) {
        super.setMessage(message);
        if (message != null) {
            tvMessage.setText(message);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        animRotate.cancel();
    }

    private static float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }

    public static MyProgressDialog show(Context context, String title, String message, boolean cancelable) {
        MyProgressDialog dialog = new MyProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(cancelable);
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        float density = getDensity(context);
        params.width = (int) (200 * density);
        params.height = LayoutParams.WRAP_CONTENT;

        window.setAttributes(params);
        return dialog;
    }
}
