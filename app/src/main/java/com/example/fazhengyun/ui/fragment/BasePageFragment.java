package com.example.fazhengyun.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.FragmentBasepageBinding;
import com.example.fazhengyun.xdroid.base.XLazyFragment;

import cn.droidlover.xdroidbase.base.SimpleRecAdapter;
import cn.droidlover.xrecyclerview.XRecyclerView;

/**
 * Created by fscm-qt on 2017/11/29.
 */

public abstract class BasePageFragment extends XLazyFragment<FragmentBasepageBinding> {

    protected static final int PAGE_SIZE = 10; //每页显示数量
    protected static final int MAX_PAGE = 100;//最大页数

    @Override
    public void initData(Bundle savedInstanceState) {
        initAdapter();
        loadData(1);
    }

    private void initAdapter() {
        setLayoutManager(getBinding().contentLayout.getRecyclerView());
        getBinding().contentLayout.getRecyclerView().setAdapter(getAdapter());
        getBinding().contentLayout.getRecyclerView().setOnRefreshAndLoadMoreListener(new XRecyclerView.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                loadData(1);
            }

            @Override
            public void onLoadMore(int page) {
                loadData(page);
            }
        });
        getBinding().contentLayout.loadingView(View.inflate(getContext(),R.layout.view_loading,null));
//        getBinding().contentLayout.errorView(View.inflate(getContext(),R.layout.view_error,null));
        getBinding().contentLayout.emptyView(View.inflate(getContext(),R.layout.view_empty,null));
//        getBinding().contentLayout.getRecyclerView().useDefLoadMoreView();
        getBinding().contentLayout.showLoading();
    }

    public abstract void loadData(int i) ;

    /**
     * fragment子类重写此方法，返回recycleview的adapter
     * @return
     */
    public abstract SimpleRecAdapter getAdapter();

    /**
     * fragment子类重写此方法，设置recycleview的一些参数
     * @param recyclerView
     */
    public abstract void setLayoutManager(XRecyclerView recyclerView);

    @Override
    public int getLayoutId() {
        return R.layout.fragment_basepage;
    }
}
