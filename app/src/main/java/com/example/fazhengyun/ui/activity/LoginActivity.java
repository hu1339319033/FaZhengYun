package com.example.fazhengyun.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityLoginBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.LoginInfo;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.util.HashMap;

import cn.droidlover.xdroidbase.router.Router;
import okhttp3.Call;

/**
 * Created by huhao on 2017/7/21.
 * 登陆页面
 */

public class LoginActivity extends XActivity<ActivityLoginBinding>{

    private Toolbar toolbar;

    @Override
    public void initData(Bundle savedInstanceState) {
        toolbar = getBinding().toolbar.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getBinding().toolbar.toolbarTitle.setText("登录");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_right_close,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_right_close:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setListener() {
        super.setListener();
        getBinding().loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(getBinding().etPhone.getText().toString())) {
                    getUiDelegate().toastShort(getString(R.string.phone_empty_hint));
                    return;
                }
                if (TextUtils.isEmpty(getBinding().etPassword.getText().toString())) {
                    getUiDelegate().toastShort(getString(R.string.password_empty_hint));
                    return;
                }
                HashMap<String,String> hashMap = new HashMap<String,String>();
                hashMap.put("phone",getBinding().etPhone.getText().toString().trim());
                hashMap.put("password", AppKit.MD5Util.encode(getBinding().etPassword.getText().toString().trim()));
                NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_Login), hashMap, new JsonCallback<LoginInfo>() {
                    @Override
                    public void onFail(Call call, Exception e, int id) {
                        getUiDelegate().toastShort(getString(R.string.network_fail_msg));
                    }

                    @Override
                    public void onResponse(LoginInfo response, int id) {
                        getUiDelegate().toastShort(response.getMsg());
                        if(response.getResult() == 0){
                            return;
                        }
                        SharedPreferenceUtil.getInstance(context).putInt(Constants.USERID,response.getId());
                        SharedPreferenceUtil.getInstance(context).putString(Constants.USERNAEM,response.getUsername());
                        SharedPreferenceUtil.getInstance(context).putString(Constants.PHONE,getBinding().etPhone.getText().toString().trim());
                        SharedPreferenceUtil.getInstance(context).putString(Constants.PASSWORD,AppKit.MD5Util.encode(getBinding().etPassword.getText().toString().trim()));
                        SharedPreferenceUtil.getInstance(context).putBoolean(Constants.LOGIN_STATE,true);
                        SharedPreferenceUtil.getInstance(context).putInt(Constants.REALNAME,response.getAuth());
                        Router.newIntent(context).to(MainActivity.class).launch();
                        finish();
                    }
                });
            }
        });
        getBinding().tvRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router.newIntent(context).to(RegisterActivity.class).launch();
            }
        });
        getBinding().tvForgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router.newIntent(context).to(FindPasswordActivity.class).launch();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }
}
