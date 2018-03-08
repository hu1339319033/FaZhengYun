package com.example.fazhengyun.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityChangepasswordBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.ResultMsg;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.util.HashMap;

import okhttp3.Call;

import static com.example.fazhengyun.net.UrlKit.ACTION_UpdatePassword;

/**
 * 修改密码
 * Created by fscm-qt on 2017/12/10.
 */

public class ChangePasswordActivity extends XActivity<ActivityChangepasswordBinding> {


    @Override
    public void initData(Bundle savedInstanceState) {
        getBinding().toolbar.toolbarTitle.setText("修改密码");
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getBinding().tvAccount.setText(SharedPreferenceUtil.getInstance(context).getString(Constants.PHONE,""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_right_confirm,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_confirm){
            //提交修改
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("phone",SharedPreferenceUtil.getInstance(context).getString(Constants.PHONE,""));
            hashMap.put("newPassword",AppKit.MD5Util.encode(getBinding().tvPassword.getText().toString().trim()));
            NetApi.invokeGet(UrlKit.getUrl(ACTION_UpdatePassword), hashMap, new JsonCallback<ResultMsg>() {
                @Override
                public void onResponse(ResultMsg response, int id) {
                    getUiDelegate().toastShort(response.getMsg());
                    if(response.getResult() == 0){
                        return;
                    }
                    SharedPreferenceUtil.getInstance(context).putString(Constants.PASSWORD, AppKit.MD5Util.encode(getBinding().tvPassword.getText().toString().trim()));//更新本地密码
                    finish();
                }

                @Override
                public void onFail(Call call, Exception e, int id) {
                    getUiDelegate().toastShort(getString(R.string.network_fail_msg));
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_changepassword;
    }

}
