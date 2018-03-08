package com.example.fazhengyun.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityChangeusernameBinding;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.ResultMsg;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.util.HashMap;

import okhttp3.Call;

import static com.example.fazhengyun.net.UrlKit.ACTION_UpdateName;

/**
 * 设置用户名
 * Created by fscm-qt on 2017/12/10.
 */

public class ChangeUserNameActivity extends XActivity<ActivityChangeusernameBinding> {


    @Override
    public void initData(Bundle savedInstanceState) {
        getBinding().toolbar.toolbarTitle.setText("设置用户名");
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            hashMap.put("userId",SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
            hashMap.put("username",getBinding().etUsername.getText().toString().trim());
            NetApi.invokeGet(UrlKit.getUrl(ACTION_UpdateName), hashMap, new JsonCallback<ResultMsg>() {
                @Override
                public void onResponse(ResultMsg response, int id) {
                    getUiDelegate().toastShort(response.getMsg());
                    if(response.getResult() == 0){
                        return;
                    }
                    SharedPreferenceUtil.getInstance(context).putString(Constants.USERNAEM, getBinding().etUsername.getText().toString().trim());//更新本地用户名
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
        return R.layout.activity_changeusername;
    }

}
