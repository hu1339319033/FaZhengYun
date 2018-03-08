package com.example.fazhengyun.ui.activity;

import android.databinding.ViewDataBinding;

import com.example.fazhengyun.xdroid.base.XActivity;

/**
 * Created by fscm-qt on 2017/12/11.
 */

public abstract class BaseXActivity<D extends ViewDataBinding> extends XActivity<D> {


    public ClickListener clickListener;

    public  void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    /**
     * 删除、存证等点击事件监听
     */
    public interface ClickListener{
        void  click(int ResId);
    }
}
