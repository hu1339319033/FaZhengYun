package com.example.fazhengyun.ui.activity;

import android.os.Bundle;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityMessageBinding;
import com.example.fazhengyun.xdroid.base.XActivity;

/**
 * Created by fscm-qt on 2017/12/8.
 */

public class MessageActivity extends XActivity<ActivityMessageBinding> {
    @Override
    public void initData(Bundle savedInstanceState) {
        getBinding().toolbar.toolbarTitle.setText("消息");
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_message;
    }
}
