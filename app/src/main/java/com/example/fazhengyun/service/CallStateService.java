package com.example.fazhengyun.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.fazhengyun.R;
import com.example.fazhengyun.kit.NotifyUtil;
import com.example.fazhengyun.ui.activity.MainActivity;
import com.example.fazhengyun.xdroid.base.UiDelegate;
import com.example.fazhengyun.xdroid.base.UiDelegateBase;

/**
 * 一个监听用户电话状态的服务<br/>
 * 为防止服务被关闭，在onDestroy中我们又启动了另一个完全一样的服务。<br/>
 * Created by fscm-qt on 2018/1/5.
 */

public class CallStateService extends Service {

    private static final String TAG = "RecorderService";
    private PhoneStateListener listener;
    UiDelegate uiDelegate ;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        uiDelegate = UiDelegateBase.create(this);
        Log.e(TAG, "oncreate---");
        NotifyUtil.init(this);
        NotifyUtil.buildSimple(100, R.mipmap.ic_launcher,"自动录音服务","法证云正在为您服务", NotifyUtil.buildIntent(MainActivity.class))
                .setHeadup().show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        uiDelegate.toastShort("自动录音服务已启动");
        return START_STICKY;//当service因内存不足被kill，当内存又有的时候，service又被重新创建
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        uiDelegate.toastShort("自动录音服务已关闭");
//        startService(new Intent(this, CallStateService.class));//在onDestroy中再启动本服务，但是用户杀进程时不会调用onDestroy方法
        // 取消电话的监听
        ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
        super.onDestroy();
    }
}
