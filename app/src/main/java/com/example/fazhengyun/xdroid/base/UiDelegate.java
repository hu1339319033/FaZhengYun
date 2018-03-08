package com.example.fazhengyun.xdroid.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import com.example.fazhengyun.ui.view.EmbellishDialog;

/**
 * Created by wanglei on 2016/12/1.
 */

public interface UiDelegate {

    void resume();
    void pause();
    void destory();

    void visible(boolean flag, View view);
    void gone(boolean flag, View view);
    void inVisible(View view);

    void toastShort(String msg);

    void showProcessDialog(String msg);
    void dimmisProcessDiaglog();
    ProgressDialog getProcessDialog();

    AlertDialog.Builder getDialogBuilder();

    void showEmbellishDialog(Context context,String message,String PositiveText, EmbellishDialog.ConfirmButtonOnClickListener confirmButtonOnClickListener,String NegativeText, EmbellishDialog.CancelButtonOnClickListener cancelButtonOnClickListener);
    void showEmbellishDialog(Context context,String message,String PositiveText, EmbellishDialog.ConfirmButtonOnClickListener confirmButtonOnClickListener);
}
