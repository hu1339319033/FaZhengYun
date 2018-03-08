package com.example.fazhengyun.ui.fragment;

import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.fazhengyun.xdroid.base.XLazyFragment;
import com.example.fazhengyun.ui.activity.BaseActivity;

/**
 * Created by fscm-qt on 2017/8/1.
 */

public abstract class BaseFragment<D extends ViewDataBinding> extends XLazyFragment<D> {

    protected BaseActivity getHoldingActivity(){
        if (getActivity() instanceof BaseActivity){
            return (BaseActivity)getActivity();
        }else {
            throw new ClassCastException("activity must extends BaseActivity");
        }
    }

    protected void initToolBar(Toolbar toolbar) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popFragment();
            }
        });
        setHasOptionsMenu(true);
    }

    protected void pushFragment(Fragment fragment){
        getHoldingActivity().pushFragment(fragment);
    }

    protected void popFragment(){
        getHoldingActivity().popFragment();
    }
}
