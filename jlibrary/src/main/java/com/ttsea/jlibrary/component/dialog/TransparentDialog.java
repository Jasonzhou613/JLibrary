package com.ttsea.jlibrary.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ttsea.jlibrary.R;

/**
 * 透明的dialog <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/4/10 9:55 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
public class TransparentDialog extends Dialog {
    private TextView loadingHintText;
    private Context mContext;

    private TransparentDialog(Context context, int theme) {
        super(context, theme);
    }

    public TransparentDialog(Context context) {
        this(context, R.style.my_transparent_theme);
        init(context, Gravity.CENTER, 120, 120, true);
    }

    private void init(Context context, int gravity, int width, int height, boolean canceledOnTouchOutside) {

        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.jmy_dialog_view, null);

        loadingHintText = (TextView) view.findViewById(R.id.loadingHintText);
        setContentView(view);
        // set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        // set width,height by density and gravity
        float density = getDensity(context);
        if (width > 0) {
            params.width = (int) (width * density);
        }
        if (height > 0) {
            params.height = (int) (height * density);
        }

        params.gravity = gravity;
        window.setAttributes(params);
        setCanceledOnTouchOutside(canceledOnTouchOutside);
    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }

    public void show(String msg) {
        if (loadingHintText != null && msg != null && msg.length() > 0) {
            loadingHintText.setText(msg);
            loadingHintText.setVisibility(View.VISIBLE);
            if (mContext != null) {
                loadingHintText.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        } else if (loadingHintText != null) {
            loadingHintText.setVisibility(View.GONE);
        }
        super.show();
    }

    @Override
    public void dismiss() {
        try {
            if (this.isShowing()) {
                super.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}