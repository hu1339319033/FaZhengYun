package com.example.fazhengyun.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.fazhengyun.R;
import com.example.fazhengyun.kit.NotifyUtil;
import com.example.fazhengyun.ui.activity.MainActivity;

import cn.droidlover.xdroidbase.log.XLog;

/**
 * Created by fscm-qt on 2017/8/21.
 */

public class MyPushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals("")){
            XLog.e("bmob", "客户端收到推送内容："+intent.getStringExtra("msg"));
            NotifyUtil.init(context);
            NotifyUtil.buildSimple(100,R.mipmap.ic_launcher,"您有新的定位结果","您有新的定位结果，点击查看详情",NotifyUtil.buildIntent(MainActivity.class))
                .setHeadup()
//                    .setContentIntent(NotifyUtil.buildIntent(MainActivity3.class))
//                .addBtn(R.mipmap.ic_launcher,"left", NotifyUtil.buildIntent(MainActivity3.class))
//                .addBtn(R.mipmap.ic_launcher,"rightdd", NotifyUtil.buildIntent(MainActivity3.class))
                .show();
        }
    }
}
