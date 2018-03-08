package com.example.fazhengyun.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.adapter.DangerInfomationAdapter;
import com.example.fazhengyun.adapter.SecondaryListAdapter;
import com.example.fazhengyun.databinding.ActivityDangerinfomationBinding;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.DangerInfos;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.droidlover.xdroidbase.log.XLog;
import okhttp3.Call;

import static com.example.fazhengyun.App.getContext;

/**
 * Created by fscm-qt on 2017/12/16.
 */

public class DangerInformationActivity extends XActivity<ActivityDangerinfomationBinding> {

    private List<SecondaryListAdapter.DataTree<String, DangerInfos.SubItem>> datas = new ArrayList<>();
    private DangerInfomationAdapter adapter;
    @Override
    public void initData(Bundle savedInstanceState) {
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getBinding().toolbar.toolbarTitle.setText("秒帮上传");
        process();//初始化数据
        getBinding().contentLayout.getRecyclerView()
                .verticalLayoutManager(context).setRefreshEnabled(false);
//        getBinding().contentLayout.getRecyclerView().addItemDecoration(new RvDividerItemDecoration(context,LinearLayoutManager.VERTICAL));
        getBinding().contentLayout.getSwipeRefreshLayout().setEnabled(false);
        adapter = new DangerInfomationAdapter(context,datas);
        getBinding().contentLayout.getRecyclerView().setAdapter(adapter);
        getBinding().contentLayout.loadingView(View.inflate(getContext(),R.layout.view_loading,null));
        getBinding().contentLayout.emptyView(View.inflate(getContext(),R.layout.view_empty,null));
        getBinding().contentLayout.errorView(View.inflate(getContext(),R.layout.view_error,null));
        getBinding().contentLayout.showLoading();
    }

    private void process(){
        HashMap<String,String> hashmap = new HashMap<>();
        hashmap.put("userId", SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
        NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_GetGPSInfo), hashmap, new JsonCallback<DangerInfos>() {
            @Override
            public void onFail(Call call, Exception e, int id) {
                getBinding().contentLayout.showError();
            }

            @Override
            public void onResponse(DangerInfos response, int id) {
            for (Map.Entry<String, List<DangerInfos.SubItem>> entry: response.getGpsInfoList().entrySet()){
                XLog.e("entrykey:"+entry.getKey());
                XLog.e("entryvalue:"+entry.getValue().toString());
                datas.add(new SecondaryListAdapter.DataTree<String, DangerInfos.SubItem>(entry.getKey(),entry.getValue()));
            }
            XLog.e("datas.size"+datas.size());
            if(datas.size()==0){
                getBinding().contentLayout.showEmpty();
                return;
            }
            adapter.setData(datas);
            adapter.notifyDataSetChanged();
            getBinding().contentLayout.showContent();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_dangerinfomation;
    }


    private static class ItemStatus {

        public static final int VIEW_TYPE_GROUPITEM = 0;
        public static final int VIEW_TYPE_SUBITEM = 1;

        private int viewType;
        private int groupItemIndex = 0;
        private int subItemIndex = -1;

        public ItemStatus() {
        }

        public int getViewType() {
            return viewType;
        }

        public void setViewType(int viewType) {
            this.viewType = viewType;
        }

        public int getGroupItemIndex() {
            return groupItemIndex;
        }

        public void setGroupItemIndex(int groupItemIndex) {
            this.groupItemIndex = groupItemIndex;
        }

        public int getSubItemIndex() {
            return subItemIndex;
        }

        public void setSubItemIndex(int subItemIndex) {
            this.subItemIndex = subItemIndex;
        }
    }

    public class DataTree<K, V> {

        private K groupItem;
        private List<V> subItems;

        public DataTree(K groupItem, List<V> subItems) {
            this.groupItem = groupItem;
            this.subItems = subItems;
        }

        public K getGroupItem() {
            return groupItem;
        }

        public List<V> getSubItems() {
            return subItems;
        }
    }
}
