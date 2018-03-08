package com.example.fazhengyun.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fazhengyun.R;

import cn.droidlover.xdroidbase.log.XLog;

/**
 * Created by fscm-qt on 2017/8/22.
 */

public class EmbellishDialog extends Dialog {

    private TextView tv_dialog_title, tv_dialog_content;
    private Button button_dialog_cancel, button_dialog_confirm;
    private EditText dialog5_editText;
    private EmbellishDialog myDialog;


    /**
     * 构造方法
     *
     * @param context
     * @param type
     *            ：0，无按钮；1，有确定按钮；2，上下2个选项；3，有确定和取消按钮   4 , 奖励钥匙  5 , 解锁
     */
    public EmbellishDialog(Context context, int type,
                           final CancelButtonOnClickListener cancelButtonOnClickListener,
                           final ConfirmButtonOnClickListener confirmButtonOnClickListener) {
        super(context, R.style.dialog);
        myDialog = this;
        View views = null;
        switch (type) {
            case 0:
                views = LayoutInflater.from(context)
                        .inflate(R.layout.dialog0, null);
                tv_dialog_content = (TextView) views
                        .findViewById(R.id.tv_dialog_content);
                break;
            case 1:
                views = LayoutInflater.from(context)
                        .inflate(R.layout.dialog1, null);
                tv_dialog_title = (TextView) views
                        .findViewById(R.id.tv_dialog_title);
                button_dialog_confirm = (Button) views
                        .findViewById(R.id.button_dialog_confirm);
                break;
            case 2:
                views = LayoutInflater.from(context)
                        .inflate(R.layout.dialog2, null);
                button_dialog_confirm = (Button) views
                        .findViewById(R.id.button_dialog_confirm);
                button_dialog_cancel = (Button) views
                        .findViewById(R.id.button_dialog_cancel);
                break;
            case 3:
                views = LayoutInflater.from(context)
                        .inflate(R.layout.dialog3, null);
                tv_dialog_title = (TextView) views
                        .findViewById(R.id.tv_dialog_title);
                tv_dialog_content = (TextView) views
                        .findViewById(R.id.tv_dialog_content);
                button_dialog_confirm = (Button) views
                        .findViewById(R.id.button_dialog_confirm);
                button_dialog_cancel = (Button) views
                        .findViewById(R.id.button_dialog_cancel);
                break;

            case 4:
                views = LayoutInflater.from(context)
                        .inflate(R.layout.dialog4, null);
                tv_dialog_content = (TextView) views
                        .findViewById(R.id.tv_dialog_content);
                button_dialog_confirm = (Button) views
                        .findViewById(R.id.button_dialog_confirm);
                button_dialog_cancel = (Button) views
                        .findViewById(R.id.button_dialog_cancel);
                break;
            case 5:
                views = LayoutInflater.from(context)
                        .inflate(R.layout.dialog5, null);
                tv_dialog_title = (TextView) views
                        .findViewById(R.id.tv_dialog_title);
                dialog5_editText = (EditText) views
                        .findViewById(R.id.dialog5_et);
                button_dialog_confirm = (Button) views
                        .findViewById(R.id.button_dialog_confirm);
                button_dialog_cancel = (Button) views
                        .findViewById(R.id.button_dialog_cancel);
                break;
        }


        if (button_dialog_cancel != null) {
            button_dialog_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO 取消按钮接口回调
                    if (cancelButtonOnClickListener != null) {
                        cancelButtonOnClickListener.cancelButtonOnClick();
                    }
                    myDialog.dismiss();
                }
            });
        }

        if (button_dialog_confirm != null) {
            button_dialog_confirm
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO 确定按钮接口回调
                            if (confirmButtonOnClickListener != null) {
                                confirmButtonOnClickListener
                                        .confirmButtonOnClick();
                            }
                            if(confirmdimmiss == true){
                                myDialog.dismiss();
                            }
                        }
                    });
        }
        this.setContentView(views);

        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        p.width = (int) (dm.widthPixels * 0.85); // 宽度设置为屏幕的0.8
        this.getWindow().setAttributes(p);

    }

    /**
     * 修改确定按钮显示文字
     * @param text 确定按钮显示文字
     */
    public void setPositiveText(String text) {
        if (button_dialog_confirm != null) {
            button_dialog_confirm.setText(text);
        }
    }

    /**
     * 修改取消按钮显示文字
     * @param text 取消按钮显示文字
     */
    public void setNegativeText(String text) {
        if (button_dialog_cancel != null) {
            button_dialog_cancel.setText(text);
        }
    }

    /**
     * 修改取消按钮显示文字
     * @param text 取消按钮显示文字
     */
    public void setEditTextHint(String text) {
        if (dialog5_editText != null) {
            dialog5_editText.setHint(text);
        }
    }


    public void showDialog() {
        if (!myDialog.isShowing()) {
            myDialog.show();
        }
    }

    public String getdialog5_ed_text(){
        return dialog5_editText.getText().toString().trim();
    }

    public interface CancelButtonOnClickListener {
        void cancelButtonOnClick();
    }

    public interface ConfirmButtonOnClickListener {
        void confirmButtonOnClick();
    }

    public void setTitle(String title) {
        tv_dialog_title.setText(title);
    }

    public void setContent(String content) {
        XLog.e("content",""+content);
        if(tv_dialog_content!=null){
            tv_dialog_content.setText(content);
        }
    }
    private boolean confirmdimmiss = true;

    public void setConfirmdimmiss(boolean confirmdimmiss) {
        this.confirmdimmiss = confirmdimmiss;
    }

    public void setConfirmButtonOnClickListener(final ConfirmButtonOnClickListener listener) {
        if (button_dialog_confirm != null) {
            button_dialog_confirm
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO 确定按钮接口回调
                            if (listener != null) {
                                listener
                                        .confirmButtonOnClick();
                            }
                            if(confirmdimmiss == true){
                                myDialog.dismiss();
                            }
                        }
                    });
        }
    }
}
