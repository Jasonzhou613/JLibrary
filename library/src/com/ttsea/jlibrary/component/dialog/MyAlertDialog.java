package com.ttsea.jlibrary.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ttsea.jlibrary.R;
import com.ttsea.jlibrary.utils.Utils;


/**
 * 自定义AlertDialog <br/>
 * <p/>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/4/11 20:52 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/4/11 20:52
 */
public class MyAlertDialog extends Dialog {

    public MyAlertDialog(Context context) {
        super(context);
    }

    public MyAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveText;
        private String negativeText;
        private View contentView;
        private OnClickListener positiveClickListener;
        private OnClickListener negativeClickListener;
        private boolean canceledOnTouchOutside = true;
        private boolean cancelable = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String msg) {
            this.message = msg;
            return this;
        }

        public Builder setMessage(int resId) {
            return setMessage(getStringById(resId));
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int resId) {
            return setTitle(getStringById(resId));
        }

        public Builder setContentView(View view) {
            this.contentView = view;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            return setPositiveButton(getStringById(positiveButtonText), listener);
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveText = positiveButtonText;
            this.positiveClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            return setNegativeButton(getStringById(negativeButtonText), listener);
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeText = negativeButtonText;
            this.negativeClickListener = listener;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public MyAlertDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final MyAlertDialog dialog = new MyAlertDialog(context, R.style.my_alert_dialog);
            View layout = inflater.inflate(R.layout.jmy_alert_dialog_layout, null);

            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            View llyTitle = layout.findViewById(R.id.llyTitle);
            TextView tvTitle = (TextView) layout.findViewById(R.id.tvTitle);
            Button positiveButton = (Button) layout.findViewById(R.id.positiveButton);
            Button negativeButton = (Button) layout.findViewById(R.id.negativeButton);
            LinearLayout llyContentView = (LinearLayout) layout.findViewById(R.id.llyContentView);
            TextView tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
            View divider = layout.findViewById(R.id.divider);

            if (title == null) {
                llyTitle.setVisibility(View.GONE);
            } else {
                llyTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
            }

            if (positiveText == null) {
                positiveButton.setVisibility(View.GONE);
                negativeButton.setBackgroundResource(R.drawable.jmy_alert_dialog_one_btn_selector);
                divider.setVisibility(View.GONE);
            } else {
                positiveButton.setVisibility(View.VISIBLE);
                positiveButton.setText(positiveText);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (positiveClickListener != null) {
                            positiveClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    }
                });
            }

            if (negativeText == null) {
                negativeButton.setVisibility(View.GONE);
                positiveButton.setBackgroundResource(R.drawable.jmy_alert_dialog_one_btn_selector);
                divider.setVisibility(View.GONE);
            } else {
                negativeButton.setVisibility(View.VISIBLE);
                negativeButton.setText(negativeText);

                negativeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (negativeClickListener != null) {
                            negativeClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    }
                });
            }

            if (Utils.isEmpty(message)) {
                tvMessage.setText("");
            } else {
                tvMessage.setText(message);
            }

            if (contentView != null) {
                llyContentView.removeAllViews();
                llyContentView.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            dialog.setCancelable(cancelable);

            return dialog;
        }

        private String getStringById(int resId) {
            return context.getResources().getString(resId);
        }
    }
}