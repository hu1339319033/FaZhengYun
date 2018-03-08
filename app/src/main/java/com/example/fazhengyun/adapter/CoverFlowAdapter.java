package com.example.fazhengyun.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.example.fazhengyun.interfaces.OnPageSelectListener;

import java.util.List;
import java.util.Map;

import cn.droidlover.xdroidbase.imageloader.ILFactory;

public class CoverFlowAdapter extends PagerAdapter implements android.support.v4.view.ViewPager.OnPageChangeListener{
	/**
	 * 默认缩小的padding值
	 */
	public static int sWidthPadding;

	public static int sHeightPadding;

	/**
	 * 子元素的集合
	 */
	private List<Map<String, Object>> mViewList;

	/**
	 * 滑动监听的回调接口
	 */
	private OnPageSelectListener listener;

	/**
	 * 上下文对象
	 */
	private Context mContext;


	public CoverFlowAdapter(List<Map<String, Object>> mImageViewList, Context context) {
		System.out.println("CoverFlowAdapter====");
		this.mViewList = mImageViewList;
		mContext = context;
		// 设置padding值
		sWidthPadding = dp2px(24);
		sHeightPadding = dp2px(32);
		System.out.println("CoverFlowAdapter=========");
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		System.out.println("destroyItem");
		ImageView x=(ImageView)mViewList.get(position).get("view");
		x.setScaleType(ScaleType.FIT_CENTER);
//		x.setLayoutParams(new LayoutParams(DipPxUtil.dip2px(mContext, 160), DipPxUtil.dip2px(mContext, 240)));
		x.setPadding(CoverFlowAdapter.sWidthPadding,CoverFlowAdapter.sHeightPadding,CoverFlowAdapter.sWidthPadding,CoverFlowAdapter.sHeightPadding);
		container.removeView(x);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		System.out.println("instantiateItem");
		ImageView x=(ImageView)mViewList.get(position).get("view");
		x.setScaleType(ScaleType.FIT_CENTER);
		// 设置padding 值，默认缩小
//		x.setLayoutParams(new LayoutParams(DipPxUtil.dip2px(mContext, 160), DipPxUtil.dip2px(mContext, 240)));
		x.setPadding(CoverFlowAdapter.sWidthPadding,CoverFlowAdapter.sHeightPadding,CoverFlowAdapter.sWidthPadding,CoverFlowAdapter.sHeightPadding);
		x.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//									final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
				final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
				ImageView imgView = new ImageView(mContext);
				imgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				imgView.setScaleType(ScaleType.FIT_CENTER);
				ILFactory.getLoader().loadNet(imgView,mViewList.get(position).get("url").toString(),null);
				dialog.setContentView(imgView);
				dialog.show();
				// 点击图片消失
				imgView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
			}
		});
		ILFactory.getLoader().loadNet(x,mViewList.get(position).get("url").toString(),null);
		container.addView(x, 0);
		return mViewList.get(position).get("view");
	}

	@Override
	public int getCount() {
		return mViewList == null ? 0 : mViewList.size();
		//		 return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// 该方法回调ViewPager 的滑动偏移量
		if (mViewList.size() > 0 && position < mViewList.size()) {
			//当前手指触摸滑动的页面,从0页滑动到1页 offset越来越大，padding越来越大
			Log.i("info", "重新设置padding");
			int outHeightPadding = (int) (positionOffset * sHeightPadding);
			int outWidthPadding = (int) (positionOffset * sWidthPadding);
			// 从0滑动到一时，此时position = 0，其应该是缩小的，符合
			((ImageView)mViewList.get(position).get("view")).setPadding(outWidthPadding, outHeightPadding, outWidthPadding, outHeightPadding);

			// position+1 为即将显示的页面，越来越大
			if (position < mViewList.size() - 1) {
				int inWidthPadding = (int) ((1 - positionOffset) * sWidthPadding);
				int inHeightPadding = (int) ((1 - positionOffset) * sHeightPadding);
				((ImageView)mViewList.get(position + 1).get("view")).setPadding(inWidthPadding, inHeightPadding, inWidthPadding, inHeightPadding);
			}
		}

	}

	@Override
	public void onPageSelected(int position) {
		// 回调选择的接口
		if (listener != null) {
			listener.select(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	/**
	 * 当将某一个作为最中央时的回调
	 *
	 * @param listener
	 */
	public void setOnPageSelectListener(OnPageSelectListener listener) {
		this.listener = listener;
	}


	/**
	 * dp 转 px
	 *
	 * @param dp
	 * @return
	 */
	public int dp2px(int dp) {
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());

		return px;
	}


}
