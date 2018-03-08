package com.example.fazhengyun.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityFileuploadBinding;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.ui.fragment.CloudFragment;
import com.example.fazhengyun.ui.fragment.LocalFileUploadFragment;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.util.ArrayList;
import java.util.List;

import cn.droidlover.xdroidbase.base.XFragmentAdapter;

/**
 * Created by fscm-qt on 2017/12/5.
 */

public class FileUploadActivity extends XActivity<ActivityFileuploadBinding> {

    String[] titles = {"本地文件","云端文件"};
    List<Fragment> fragmentList = new ArrayList<>();
    XFragmentAdapter adapter;
    LocalFileUploadFragment localFileUploadFragment;
    CloudFragment cloudPhotoFragment;

    @Override
    public void initData(Bundle savedInstanceState) {
        getBinding().toolbar.toolbarTitle.setText("文件上传");
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        localFileUploadFragment = LocalFileUploadFragment.newInstance();
        cloudPhotoFragment = CloudFragment.newInstance(Constants.LocalType.ALL);
        fragmentList.clear();
        fragmentList.add(localFileUploadFragment);
        fragmentList.add(cloudPhotoFragment);

        if(adapter == null){
            adapter = new XFragmentAdapter(getSupportFragmentManager(),fragmentList,titles);
        }
        getBinding().viewPager.setAdapter(adapter);
        getBinding().tabLayout.setupWithViewPager(getBinding().viewPager);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fileupload;
    }
}
