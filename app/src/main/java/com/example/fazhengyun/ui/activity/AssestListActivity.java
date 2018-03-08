package com.example.fazhengyun.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.adapter.AssestListAdapter;
import com.example.fazhengyun.databinding.ActivityAssestlistBinding;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.AssestInfos;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.ui.view.RvDividerItemDecoration;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.util.HashMap;

import cn.droidlover.xrecyclerview.XRecyclerView;
import okhttp3.Call;

import static com.example.fazhengyun.App.getContext;

/**
 * 出证列表
 * Created by fscm-qt on 2017/12/16.
 */

public class AssestListActivity extends XActivity<ActivityAssestlistBinding>{

    private AssestListAdapter adapter;
    @Override
    public void initData(Bundle savedInstanceState) {
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getBinding().toolbar.toolbarTitle.setText("出证列表");

        getBinding().contentLayout.getRecyclerView();
        getBinding().contentLayout.getRecyclerView().verticalLayoutManager(context).setRefreshEnabled(false);
        getBinding().contentLayout.getRecyclerView().addItemDecoration(new RvDividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        getBinding().contentLayout.getSwipeRefreshLayout().setEnabled(false);
        getBinding().contentLayout.loadingView(View.inflate(getContext(),R.layout.view_loading,null));
        getBinding().contentLayout.emptyView(View.inflate(getContext(),R.layout.view_empty,null));
        getBinding().contentLayout.errorView(View.inflate(getContext(),R.layout.view_error,null));
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
        adapter = new AssestListAdapter(context);
        getBinding().contentLayout.getRecyclerView().setAdapter(adapter);
        getBinding().contentLayout.getRecyclerView().addHeaderView(View.inflate(context,R.layout.header_adapter_assestlist,null));
        getBinding().contentLayout.showLoading();
        loadData(1);
    }

    /**
     * 加载数据
     */
    private void loadData(final int page){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("userId", SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
        hashMap.put("pageNum",page+"");
        NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_AttestList), hashMap, new JsonCallback<AssestInfos>() {
            @Override
            public void onFail(Call call, Exception e, int id) {
                getBinding().contentLayout.showError();
            }

            @Override
            public void onResponse(AssestInfos response, int id) {
                if (page > 1) {
                    adapter.addData(response.getApplyList());
                } else {
                    adapter.setData(response.getApplyList());
                }

                getBinding().contentLayout.getRecyclerView().setPage(page, response.getTotle()/10>=1?response.getTotle()/10+1:1);

                if (adapter.getItemCount() < 1) {
                    getBinding().contentLayout.showEmpty();
                    return;
                }
                getBinding().contentLayout.showContent();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_assestlist;
    }
}
