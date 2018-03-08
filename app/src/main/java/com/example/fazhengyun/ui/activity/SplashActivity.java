package com.example.fazhengyun.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.animation.AlphaAnimation;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivitySplashBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.VersionInfo;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.service.DownloadService;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.util.HashMap;

import cn.droidlover.xdroidbase.router.Router;
import okhttp3.Call;

public class SplashActivity extends XActivity<ActivitySplashBinding> {

    @Override
    public void initData(Bundle savedInstanceState) {
        // 判断当前网络状态是否可用
        if (AppKit.isNetWorkConnection(context)) {

            // splash 做一个动画,进入主界面
            AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
            aa.setDuration(2000);
            getBinding().linLayout.setAnimation(aa);
            getBinding().linLayout.startAnimation(aa);
            // 通过handler 延时2秒 执行r任务
            new Handler().postDelayed(new LoadMainTabTask(), 1000);
        } else {
            showSetNetworkDialog();
        }
    }

    private class LoadMainTabTask implements Runnable{

        @Override
        public void run() {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("versionId", Constants.VERSIONID+"");
            NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_Version), hashMap, new JsonCallback<VersionInfo>() {
                @Override
                public void onFail(Call call, Exception e, int id) {
                    Router.newIntent(context).to(MainActivity.class).launch();
                    finish();
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
                        if(response.getIsneed() == 0){//非强制更新
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Router.newIntent(context).to(MainActivity.class).launch();
                                    finish();
                                }
                            });
                        }
                        builder.setCancelable(false);
                        builder.create().show();
                    }else {
                        Router.newIntent(context).to(MainActivity.class).launch();
                        finish();
                    }
                }
            });
        }
    }

    private void showSetNetworkDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置网络");
        builder.setMessage("网络错误请检查网络状态");
        builder.setPositiveButton("设置网络", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                // 类名一定要包含名
                if(android.os.Build.VERSION.SDK_INT > 10 ){
                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                }else {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
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
        return R.layout.activity_splash;
    }
}
