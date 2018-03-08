package com.example.fazhengyun.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityChangephoneBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.ResultMsg;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import cn.droidlover.xdroidbase.kit.Kits;
import cn.droidlover.xdroidbase.log.XLog;
import cn.droidlover.xdroidbase.router.Router;
import okhttp3.Call;

import static com.example.fazhengyun.net.UrlKit.ACTION_UpdatePhone;

/**
 * Created by fscm-qt on 2017/12/10.
 */

public class ChangePhoneActivity extends XActivity<ActivityChangephoneBinding> implements View.OnClickListener {

    // 验证码
    private String code = null;
    private HashMap<String, Object> result = null;// 接收结果
    @Override
    public void initData(Bundle savedInstanceState) {
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getBinding().toolbar.toolbarTitle.setText("设置新手机号");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setListener() {
        super.setListener();
        getBinding().tvSendphonecode.setOnClickListener(this);
        getBinding().btnCommit.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_changephone;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_sendphonecode:
                if (TextUtils.isEmpty(getBinding().etPhone.getText().toString())) {
                    getUiDelegate().toastShort(getString(R.string.phone_empty_hint));
                    return;
                }
                if (!Kits.PatternMatcher.isMobileNumber(getBinding().etPhone.getText().toString())) {
                    getUiDelegate().toastShort(getString(R.string.phone_rule_hint));
                    return;
                }
                if(AppKit.getInstance().getRestAPI() == null){
                    AppKit.getInstance().initSmsSdK();
                }
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        code = Kits.Random.getRandomNumbers(4);
                        result = AppKit.getInstance().getRestAPI().sendTemplateSMS(getBinding().etPhone.getText().toString(), "169476",
                                new String[] { code, "3" });
                        if ("000000".equals(result.get("statusCode"))) {
                            XLog.e("发送成功" + code);
                            //发送成功后存储时间+验证码
                            SimpleDateFormat sDateFormat    =   new  SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String  date  =  sDateFormat.format(new  java.util.Date());
                            SharedPreferenceUtil.getInstance(context).putString(Constants.CODE_STRING, code);
                            SharedPreferenceUtil.getInstance(context).putString(Constants.CODEDATE_STRING, date);
                            SharedPreferenceUtil.getInstance(context).putString(Constants.CODEPHONE_STRING, getBinding().etPhone.getText().toString());
                            System.out.println("sendcodedate"+date);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    getUiDelegate().toastShort("发送成功");
                                }
                            });
                            handler.post(runnable);
                        } else {
                            // 异常返回输出错误码和错误信息
                            // Toast.makeText(
                            // Find_password_phone_registercode_activity.this,
                            // result.get("statusMsg").toString(),
                            // Toast.LENGTH_SHORT).show();
                            System.out.println("错误码=" + result.get("statusCode")
                                    + " 错误信息= " + result.get("statusMsg"));
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    getUiDelegate().toastShort("发送失败");
                                }
                            });
                            XLog.e("code", code+"");
                        }
                    }
                }).start();
                break;
            case R.id.btn_commit:
                if (TextUtils.isEmpty(getBinding().etPhone.getText().toString())) {
                    getUiDelegate().toastShort(getString(R.string.phone_empty_hint));
                    return;
                }
                if (!Kits.PatternMatcher.isMobileNumber(getBinding().etPhone.getText().toString())) {
                    getUiDelegate().toastShort(getString(R.string.phone_rule_hint));
                    return;
                }
                if(TextUtils.isEmpty(getBinding().etPhonecode.getText().toString())){
                    getUiDelegate().toastShort("请输入验证码");
                    return;
                }
                if(!SharedPreferenceUtil.getInstance(context).getString(Constants.CODE_STRING,"-1").equals(getBinding().etPhonecode.getText().toString())){
                    getUiDelegate().toastShort("验证码错误");
                    return;
                }
                if(!SharedPreferenceUtil.getInstance(context).getString(Constants.CODEPHONE_STRING,getBinding().etPhone.getText().toString()).equals(getBinding().etPhone.getText().toString())){
                    getUiDelegate().toastShort("验证码错误");
                    return;
                }
                //提交修改
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("userId", SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
                hashMap.put("phone", getBinding().etPhone.getText().toString().trim());
                NetApi.invokeGet(UrlKit.getUrl(ACTION_UpdatePhone), hashMap, new JsonCallback<ResultMsg>() {
                    @Override
                    public void onResponse(ResultMsg response, int id) {
                        getUiDelegate().toastShort(response.getMsg());
                        if(response.getResult() == 0){
                            return;
                        }
                        //替换本地记录为新手机号
                        SharedPreferenceUtil.getInstance(context).putString(Constants.PHONE,getBinding().etPhone.getText().toString().trim());
                        //使用验证码后置空本地记录
                        SharedPreferenceUtil.getInstance(context).putString(Constants.CODE_STRING, null);
                        SharedPreferenceUtil.getInstance(context).putString(Constants.CODEDATE_STRING, null);
                        SharedPreferenceUtil.getInstance(context).putString(Constants.CODEPHONE_STRING, null);
                        Router.newIntent(context).to(MainActivity.class).launch();
                        finish();
                    }

                    @Override
                    public void onFail(Call call, Exception e, int id) {
                        getUiDelegate().toastShort(getString(R.string.network_fail_msg));
                    }
                });
                break;
        }
    }

    private Handler handler = new Handler();
    private int recLen = 180;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (recLen > 0) {
                getBinding().tvSendphonecode.setEnabled(false);
                getBinding().tvSendphonecode.setText(recLen + "s");
                recLen--;
                handler.postDelayed(this, 1000);
            } else {
                getBinding().tvSendphonecode.setEnabled(true);
                getBinding().tvSendphonecode.setText("获取验证码");
                code = null;
            }

        }
    };
}
