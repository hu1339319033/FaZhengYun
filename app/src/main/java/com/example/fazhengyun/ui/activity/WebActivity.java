package com.example.fazhengyun.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityWebviewBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.model.Event;
import com.example.fazhengyun.ui.view.SharePopupOption;
import com.example.fazhengyun.xdroid.base.XActivity;
import com.example.fazhengyun.xdroid.event.BusFactory;

import cn.droidlover.xdroidbase.log.XLog;
import cn.droidlover.xdroidbase.router.Router;

/**
 * Created by wanglei on 2016/12/11.
 */

public class WebActivity extends XActivity<ActivityWebviewBinding> {


    String url,imageurl;
    String desc;

    public static final String PARAM_URL = "url";
    public static final String PARAM_IMAGEURL = "imageurl";
    public static final String PARAM_DESC = "desc";
    public static String SimpleName = "";


    @Override
    public void initData(Bundle savedInstanceState) {
        url = getIntent().getStringExtra(PARAM_URL);
        desc = getIntent().getStringExtra(PARAM_DESC);
        imageurl = getIntent().getStringExtra(PARAM_IMAGEURL);
        initToolbar();
        initContentLayout();
        initRefreshLayout();
        initWebView();
    }

    @Override
    public void setListener() {
        getBinding().btSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLog.e("getSimpleName:"+WebActivity.SimpleName);
                if(WebActivity.SimpleName.equals("MainActivity3")){
                    SharePopupOption popupOption = new SharePopupOption(context,getString(R.string.share_title));
                    popupOption.setItemClickListener(new SharePopupOption.onPopupWindowItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            switch (position){

                            }
                        }
                    });
                    popupOption.showPopupWindow();
                }else{
                    BusFactory.getBus().post(new Event.SelectLinkEvent(desc,url,imageurl));
                    BusFactory.getBus().post(new Event.FinishSelf());
                    finish();
                }
            }
        });
    }

    private void initContentLayout() {
        getBinding().contentLayout.loadingView(View.inflate(context, R.layout.view_loading, null));
    }

    private void initRefreshLayout() {
        getBinding().swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        getBinding().swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBinding().webView.loadUrl(url);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(getBinding().swipeRefreshLayout.isRefreshing()){
                            getBinding().swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },2000) ;
            }
        });
    }

    private void initWebView() {
        getBinding().webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    if (getBinding().contentLayout != null)
                        getBinding().contentLayout.showContent();
                    if (getBinding().webView != null)
                        url = getBinding().webView.getUrl();
                } else {
                    if (getBinding().contentLayout != null)
                        getBinding().contentLayout.showLoading();
                }
            }
        });
        getBinding().webView.setWebViewClient(new WebViewClient());
        getBinding().webView.getSettings().setBuiltInZoomControls(true);
        getBinding().webView.getSettings().setJavaScriptEnabled(false);
        getBinding().webView.getSettings().setDomStorageEnabled(true);
        getBinding().webView.getSettings().setDatabaseEnabled(true);
        getBinding().webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        getBinding().webView.getSettings().setAppCacheEnabled(true);

        getBinding().webView.loadUrl(url);
    }

    private void initToolbar() {
        setSupportActionBar(getBinding().toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_24dp);
        getSupportActionBar().setTitle(desc);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_web, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_share:
                AppKit.shareText(this, getBinding().webView.getTitle() + " " + getBinding().webView.getUrl() + " " +
                        "来自「Mobile」");
                break;
            case R.id.action_refresh:
                getBinding().webView.reload();
                break;
            case R.id.action_copy:
                AppKit.copyToClipBoard(this, getBinding().webView.getUrl());
                break;
            case R.id.action_open_in_browser:
                AppKit.openInBrowser(this, getBinding().webView.getUrl());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (getBinding().webView.canGoBack()) {
                    getBinding().webView.goBack();
                } else {
                    finish();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getBinding().webView != null) getBinding().webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getBinding().webView != null) getBinding().webView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getBinding().webView != null) {
            ViewGroup parent = (ViewGroup) getBinding().webView.getParent();
            if (parent != null) {
                parent.removeView(getBinding().webView);
            }
            getBinding().webView.removeAllViews();
            getBinding().webView.destroy();
        }
    }

    public static void launch(Activity activity, String url, String desc,String imageurl) {
        WebActivity.SimpleName = activity.getClass().getSimpleName();
        Router.newIntent(activity)
                .to(WebActivity.class)
                .putString(PARAM_URL, url)
                .putString(PARAM_DESC, desc)
                .putString(PARAM_IMAGEURL,imageurl)
                .launch();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_webview;
    }

}
