package com.example.fazhengyun.ui.activity;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivitySettingBinding;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.VersionInfo;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.service.DownloadService;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by fscm-qt on 2017/12/7.
 */

public class SettingActivity extends XActivity<ActivitySettingBinding> {

    @Override
    public void initData(Bundle savedInstanceState) {
        getBinding().toolbar.toolbarTitle.setText("设置");
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getBinding().tvPhone.setText(SharedPreferenceUtil.getInstance(context).getString(Constants.PHONE,""));
        getBinding().itemUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("versionId", Constants.VERSIONID+"");
                NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_Version), hashMap, new JsonCallback<VersionInfo>() {
                    @Override
                    public void onFail(Call call, Exception e, int id) {
                        getUiDelegate().toastShort("网络连接失败");
                    }

                    @Override
                    public void onResponse(final VersionInfo response, int id) {
                        if(getVersion() < response.getVerCode()){ //有新版本
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                            builder.setMessage("有新版本，是否下载更新？");
                            builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getUiDelegate().showProcessDialog("正在后台下载更新。。。");
                                    DownloadService.startDownload(context,response.getVerUrl());
                                }
                            });
                            builder.setNegativeButton("取消", null);
                            builder.setCancelable(false);
                            builder.create().show();
                        }else {
                            getUiDelegate().toastShort("当前已经是最新版本！");
                        }
                    }
                });
            }
        });
    }

    private int versionCode;
    /**
     * 获取版本号
     *
     * @return
     */
    protected int getVersion() {
        // TODO Auto-generated method stub
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }
}
