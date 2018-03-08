package com.example.fazhengyun.ui.activity;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.example.fazhengyun.xdroid.base.XActivity;

/**
 * 用于fragment切换的activity外壳
 * Created by fscm-qt on 2017/8/1.
 */

public abstract class BaseActivity<D extends ViewDataBinding> extends XActivity<D> {
    //由于有些跳转无需参数,所以这里无需抽象方法
    protected void handleIntent(Intent intent){
    };
    protected abstract Fragment getFirstFragment();
    protected abstract int getFragmentContainerId();
    protected void initToolBar(){};

    @Override
    public void initData(Bundle savedInstanceState) {
        //处理Intent(主要用来获取其中携带的参数)
        if (getIntent() != null){
            handleIntent(getIntent());
        }

        initToolBar();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //设置contentView
        setContentView(getLayoutId());
        //添加栈底的第一个fragment
        if (getSupportFragmentManager().getBackStackEntryCount() == 0){
            if (getFirstFragment() != null){
                pushFragment(getFirstFragment());
            }else {
                throw new NullPointerException("getFirstFragment() cannot be null");
            }
        }
    }

    public void pushFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContainerId(), fragment)
                    .addToBackStack(((Object)fragment).getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    public void popFragment(){
        if (getSupportFragmentManager().getBackStackEntryCount() > 1){
            getSupportFragmentManager().popBackStack();
        }else {
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1){
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = super.getSupportParentActivityIntent();
        if (intent == null){
            finish();
        }
        return intent;
    }

    //回退键处理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode){
            if (getSupportFragmentManager().getBackStackEntryCount() == 1){
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
