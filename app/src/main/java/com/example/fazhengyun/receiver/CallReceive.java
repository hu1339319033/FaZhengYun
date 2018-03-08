package com.example.fazhengyun.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by fscm-qt on 2018/1/4.
 */

public class CallReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("PhoneReciveAction",intent.getAction());
        Log.e("","----------------------------");
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){ //呼出电话的广播
            //呼出的电话号码
            String outPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.e("PhoneRecive","呼出："+outPhoneNumber);
        } else if(intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)){ //通话状态改变的广播
            //来电号码
            String incomingNum = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.e("PhoneRecive","通话号码："+incomingNum);
            //改变的状态
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(TelephonyManager.EXTRA_STATE_IDLE.equals(state)){
                //闲置状态，也就是挂断
                Log.e("PhoneReceive","TelephonyManager.EXTRA_STATE_IDLE");

            } else if(TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)){
                //通话状态，对于呼出而言，开始就是这个状态了；对于接听者而言，接起电话就是这个状态了
                Log.e("PhoneReceive","TelephonyManager.EXTRA_STATE_OFFHOOK");


            } else if(TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
                //响铃状态，即来电
                Log.e("PhoneReceive","TelephonyManager.EXTRA_STATE_RINGING");
            }
        }
    }
}
