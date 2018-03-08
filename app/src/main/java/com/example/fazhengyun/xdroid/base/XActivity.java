package com.example.fazhengyun.xdroid.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.fazhengyun.xdroid.event.BusFactory;

/**
 * Created by wanglei on 2016/11/27.
 */

public abstract class XActivity<D extends ViewDataBinding> extends AppCompatActivity implements UiCallback {
    protected Activity context;
    protected UiDelegate uiDelegate;
    private D binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;

        if (getLayoutId() > 0) {
            bindUI(getLayoutId());
        }
        setListener();
        initData(savedInstanceState);
    }

    protected D getBinding() {
        return binding;
    }

    public void bindUI(@LayoutRes int layoutResID) {
        binding = DataBindingUtil.setContentView(context, layoutResID);
    }


    protected UiDelegate getUiDelegate() {
        if (uiDelegate == null) {
            uiDelegate = UiDelegateBase.create(this);
        }
        return uiDelegate;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (useEventBus()) {
            BusFactory.getBus().register(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getUiDelegate().resume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        getUiDelegate().pause();
    }


    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public void setListener() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusFactory.getBus().unregister(this);
        getUiDelegate().destory();
    }
}
