package com.example.fazhengyun.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityFeedbackBinding;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.xdroid.base.XActivity;

import cn.droidlover.xdroidbase.cache.SharedPref;

public class FeedbackActivity extends XActivity<ActivityFeedbackBinding> {


    @Override
    public void initData(Bundle savedInstanceState) {
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getBinding().btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(getBinding().etTitle.getText().toString().trim())){
                    getUiDelegate().toastShort("请输入主题");
                    return;
                }
                if(TextUtils.isEmpty(getBinding().etContent.getText().toString().trim())){
                    getUiDelegate().toastShort("请输入反馈内容");
                    return;
                }
                final String userId = SharedPref.getInstance(context).getString(Constants.USERID,null);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_feedback;
    }
}
