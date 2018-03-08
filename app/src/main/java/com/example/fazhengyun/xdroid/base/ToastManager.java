package com.example.fazhengyun.xdroid.base;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by fscm-qt on 2017/8/2.
 */

public class ToastManager {
    private Toast mToast;
    private static ToastManager manager;

    private ToastManager(Context context) {
        if(this.mToast == null) {
            this.mToast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        }
    }

    public static ToastManager getInstance(Context context) {
        if(manager == null) {
            manager = new ToastManager(context);
        }

        return manager;
    }

    public ToastManager setText(CharSequence msg) {
        this.mToast.setText(msg);
        return this;
    }

    public ToastManager setText(@StringRes int resId) {
        this.mToast.setText(resId);
        return this;
    }

    public void show() {
        this.mToast.show();
    }

    public static void showShort(Context context, String msg) {
        getInstance(context).setText(msg).show();
    }

    public static void showShort(Context context, @StringRes int resId) {
        getInstance(context).setText(resId).show();
    }
}
