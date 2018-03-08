package com.example.fazhengyun.xdroid.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import com.example.fazhengyun.ui.view.EmbellishDialog;

/**
 * Created by wanglei on 2016/12/1.
 */

public class UiDelegateBase implements UiDelegate {

    public Context context;
    ProgressDialog dialog;
    AlertDialog.Builder builder;
    EmbellishDialog embellishDialog;

    private UiDelegateBase(Context context) {
        this.context = context;
    }

    public static UiDelegateBase create(Context context) {
        return new UiDelegateBase(context);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destory() {

    }

    @Override
    public void visible(boolean flag, View view) {
        if (flag) view.setVisibility(View.VISIBLE);
    }

    @Override
    public void gone(boolean flag, View view) {
        if (flag) view.setVisibility(View.GONE);
    }

    @Override
    public void inVisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public void toastShort(String msg) {
        ToastManager.showShort(context, msg);
    }

    @Override
    public void showProcessDialog(String msg) {
        if(dialog == null){
            dialog = new ProgressDialog(context);
        }
        dialog.setMessage(msg);
        dialog.show();
    }

    @Override
    public void dimmisProcessDiaglog() {
        if(dialog != null&&dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    public ProgressDialog getProcessDialog() {
        if(dialog == null){
            dialog = new ProgressDialog(context);
        }
        return dialog;
    }

    @Override
    public AlertDialog.Builder getDialogBuilder() {
        if (builder == null)
        builder = new AlertDialog.Builder(context);
        return null;
    }

    @Override
    public void showEmbellishDialog(Context context, String message, String PositiveText, EmbellishDialog.ConfirmButtonOnClickListener confirmButtonOnClickListener, String NegativeText, EmbellishDialog.CancelButtonOnClickListener cancelButtonOnClickListener) {
        embellishDialog = new EmbellishDialog(context, 4,cancelButtonOnClickListener, confirmButtonOnClickListener);
        embellishDialog.setContent(message);
        embellishDialog.setPositiveText(PositiveText);
        embellishDialog.setNegativeText(NegativeText);
        embellishDialog.show();
    }

    @Override
    public void showEmbellishDialog(Context context, String message, String PositiveText, EmbellishDialog.ConfirmButtonOnClickListener confirmButtonOnClickListener) {
        embellishDialog = new EmbellishDialog(context,1,null,confirmButtonOnClickListener);
        embellishDialog.setTitle(message);
        embellishDialog.setPositiveText(PositiveText);
        embellishDialog.show();
    }


}
