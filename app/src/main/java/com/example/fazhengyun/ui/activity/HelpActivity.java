package com.example.fazhengyun.ui.activity;

import android.os.Bundle;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityHelpBinding;
import com.example.fazhengyun.xdroid.base.XActivity;

public class HelpActivity extends XActivity<ActivityHelpBinding> {


    @Override
    public void initData(Bundle savedInstanceState) {
        getBinding().toolbar.toolbarTitle.setText("帮助中心");
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_help;
    }
}
