package com.example.fazhengyun.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityLookswitchBinding;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.ui.fragment.LookPhotoAndAudioFragment;
import com.example.fazhengyun.ui.fragment.LookVideoFragment;
import com.example.fazhengyun.xdroid.base.XActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import cn.droidlover.xdroidbase.base.XFragmentAdapter;
import cn.droidlover.xdroidbase.log.XLog;
import okhttp3.Call;

/**
 * Created by fscm-qt on 2017/12/17.
 */

public class LookSwitchActivity extends XActivity<ActivityLookswitchBinding> {

    String[] titles = {"音频","视频"};
    List<Fragment> fragmentList = new ArrayList<>();
    XFragmentAdapter adapter;
    private LookPhotoAndAudioFragment lookPhotoAndAudioFragment;
    private LookVideoFragment lookVideoFragment;
    public static String httpurlhead = "http://8snf0ca1cc2m.d0o9w1n.t3re.miaosos.com";
    @Override
    public void initData(Bundle savedInstanceState) {
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getBinding().toolbar.toolbarTitle.setText("查看资源");
        lookPhotoAndAudioFragment = LookPhotoAndAudioFragment.newInstance();
        lookVideoFragment = LookVideoFragment.newInstance();
        fragmentList.clear();
        fragmentList.add(lookPhotoAndAudioFragment);
        fragmentList.add(lookVideoFragment);

        if(adapter == null){
            adapter = new XFragmentAdapter(getSupportFragmentManager(),fragmentList,titles);
        }
        getBinding().viewPager.setAdapter(adapter);
        getBinding().tabLayout.setupWithViewPager(getBinding().viewPager);
        NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_GetFtpAddr), null, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                httpurlhead = jsonObject.get("http_addr").toString().substring(1,jsonObject.get("http_addr").toString().length()-1);
                XLog.e("httpurlhead"+httpurlhead);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_lookswitch;
    }
}
