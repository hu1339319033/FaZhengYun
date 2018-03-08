package com.example.fazhengyun.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.fazhengyun.R;
import com.example.fazhengyun.adapter.CoverFlowAdapter;
import com.example.fazhengyun.interfaces.OnPageSelectListener;
import com.example.fazhengyun.ui.activity.LookSwitchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoverFlowViewPager extends RelativeLayout implements OnPageSelectListener {
	/**
	 * 用于左右滚动
	 */
	private ViewPager mViewPager;
	CoverFlowAdapter mAdapter;
	OnPageSelectListener listener;
	List<Map<String, Object>> mViewList;
	Context context;
	boolean isLooper=false;
	public CoverFlowViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		LayoutInflater.from(context).inflate(R.layout.widget_conver_flow,this);
		mViewPager = (ViewPager) findViewById(R.id.vp_conver_flow);
		init();
	}

	/**
	 * 初始化方法
	 */
	private void init() {
		mViewList=new ArrayList<Map<String, Object>>();
		// 构造适配器，传入数据源
		mAdapter = new CoverFlowAdapter(mViewList,getContext());
		// 设置选中的回调
		mAdapter.setOnPageSelectListener(this);
		// 设置适配器
		mViewPager.setAdapter(mAdapter);
		// 设置滑动的监听，因为adpter实现了滑动回调的接口，所以这里直接设置adpter
		mViewPager.setOnPageChangeListener(mAdapter);
		// 自己百度
		mViewPager.setOffscreenPageLimit(5);

		// 设置触摸事件的分发
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 传递给ViewPager 进行滑动处理
				return mViewPager.dispatchTouchEvent(event);
			}
		});
	}

	public void Setautomation(){
		 //修改添加设置ViewPager的当前页，为了保证左右轮播
        mViewPager.setCurrentItem(0);
 
        //开启一个线程，用于循环
        new Thread(new Runnable() {
            @Override
            public void run() {
                isLooper = true;
                while (isLooper){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
 
                    ((LookSwitchActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //这里是设置当前页的下一页
                        	if(mViewPager.getCurrentItem()<mViewList.size()){
                        		mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                        	}
                        }
                    });
                }
            }
        }).start();
	}
	/**
	 * 设置显示的数据，进行一层封装
	 * @param lists
	 */
	public void setViewList(List<Map<String, Object>> lists){
//		System.out.println("lists.size"+lists.size());
		if(lists==null){
			return;
		}
//		for (int i = lists.size()-3; i < lists.size(); i++) {
//		
//			FrameLayout layout = new FrameLayout(getContext());
//			// 设置padding 值，默认缩小
//			layout.setPadding(CoverFlowAdapter.sWidthPadding,CoverFlowAdapter.sHeightPadding,CoverFlowAdapter.sWidthPadding,CoverFlowAdapter.sHeightPadding);
//			layout.addView(lists.get(i));
//			mViewList.add(layout);
//		}
		
		mViewList.addAll(lists);
		// 刷新数据
		mAdapter.notifyDataSetChanged();
	}

	
	@Override
	public void select(int position) {
		// TODO Auto-generated method stub
		if(listener!=null){
			listener.select(position);
		}

	}
	/**
	 * 当将某一个作为最中央时的回调
	 *
	 * @param listener
	 */
	 public void setOnPageSelectListener(OnPageSelectListener listener) {
	 this.listener = listener;
	 }


}
