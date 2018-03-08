package com.example.fazhengyun.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.FragmentVideoBinding;
import com.example.fazhengyun.model.GpsResourceInfos;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.ui.activity.LookSwitchActivity;
import com.example.fazhengyun.ui.activity.VideoPlayActivity;
import com.example.fazhengyun.xdroid.base.XLazyFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.droidlover.xdroidbase.log.XLog;
import cn.droidlover.xdroidbase.router.Router;
import okhttp3.Call;

/**
 * Created by fscm-qt on 2017/12/17.
 */

public class LookVideoFragment extends XLazyFragment<FragmentVideoBinding>{

    private List<String> videourls = new ArrayList<>();
    private ViewPager viewPager;
    @Override
    public void initData(Bundle savedInstanceState) {

        process();
        viewPager = getBinding().viewpager;
        viewPager.setPageMargin(30);
    }

    private void process(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("gpsInfoId",getActivity().getIntent().getStringExtra("gpsInfoId"));
        hashMap.put("resourceType",1+"");
        hashMap.put("pageNum",1+"");

        NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_GetGpsUploadList), hashMap, new JsonCallback<GpsResourceInfos>() {
            @Override
            public void onFail(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(GpsResourceInfos response, int id) {
                XLog.e("onResponse=======");
                for (GpsResourceInfos.Item item: response.getGpsUploadList()){
                        videourls.add(item.getUploadUrl());
                }
                XLog.e("videourls:"+videourls.size());
                if(videourls.size()<=0){
                    getBinding().viewpager.setVisibility(View.GONE);
                    getBinding().relaEmpty.setVisibility(View.VISIBLE);
                }
                viewPager.setAdapter(new PagerAdapter() {
                    @Override
                    public int getCount() {
                        return videourls.size();
                    }

                    @Override
                    public boolean isViewFromObject(View view, Object object) {
                        return view == object;
                    }
//            @Override
//            public float getPageWidth(int position) {
//                return 0.8f;
//            }

                    @Override
                    public Object instantiateItem(final ViewGroup container, final int position) {
                        View view = View.inflate(container.getContext(),R.layout.widget_gallery_view, null);
                        view.findViewById(R.id.iv_icon_r).setVisibility(View.VISIBLE);
                        ImageView iv = (ImageView) view.findViewById(R.id.headRIV);
                        XLog.e("urliv"+LookSwitchActivity.httpurlhead+videourls.get(position));
                        container.addView(view);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Router.newIntent(getActivity()).to(VideoPlayActivity.class).putString("url",LookSwitchActivity.httpurlhead+videourls.get(position)).launch();
                            }
                        });
                        return view;
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position, Object object) {
                        container.removeView((View) object);
                    }
                });
                viewPager.setCurrentItem(1);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_video;
    }

    public static LookVideoFragment newInstance(){
        return new LookVideoFragment();
    }
}
