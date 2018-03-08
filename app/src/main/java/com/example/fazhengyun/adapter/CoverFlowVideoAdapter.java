package com.example.fazhengyun.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.fazhengyun.interfaces.OnPageSelectListener;
import com.example.fazhengyun.ui.activity.VideoPlayActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoverFlowVideoAdapter extends PagerAdapter implements android.support.v4.view.ViewPager.OnPageChangeListener{
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
	

	public CoverFlowVideoAdapter(List<Map<String, Object>> mImageViewList, Context context) {
		this.mViewList = mImageViewList;
		mContext = context;
		// 设置padding值
		sWidthPadding = dp2px(24);
		sHeightPadding = dp2px(32);
		
	}
	
	@Override
	public void destroyItem(ViewGroup container, final int position, Object object) {
		container.removeView((View)mViewList.get(position).get("view"));
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View layout = (View) mViewList.get(position).get("view");
		try {
			
			((ImageButton)mViewList.get(position).get("imagebutton")).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					System.out.println("跳转全屏播放");
					Intent intent = new Intent(mContext,VideoPlayActivity.class);
					System.out.println("url:"+mViewList.get(position).get("url").toString());
					intent.putExtra("url",mViewList.get(position).get("url").toString());
					mContext.startActivity(intent);
				}
			});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		container.addView(layout);
		return layout;
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
			((View)mViewList.get(position).get("view")).setPadding(outWidthPadding, outHeightPadding, outWidthPadding, outHeightPadding);

			// position+1 为即将显示的页面，越来越大
			if (position < mViewList.size() - 1) {
				int inWidthPadding = (int) ((1 - positionOffset) * sWidthPadding);
				int inHeightPadding = (int) ((1 - positionOffset) * sHeightPadding);
				((View)mViewList.get(position + 1).get("view")).setPadding(inWidthPadding, inHeightPadding, inWidthPadding, inHeightPadding);
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


	public static Bitmap retriveVideoFrameFromVideo(String videoPath)
			throws Throwable
			{
		Bitmap bitmap = null;
		MediaMetadataRetriever mediaMetadataRetriever = null;
		try
		{
			mediaMetadataRetriever = new MediaMetadataRetriever();
			if (Build.VERSION.SDK_INT >= 14)
				mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
			else
				mediaMetadataRetriever.setDataSource(videoPath);
			//   mediaMetadataRetriever.setDataSource(videoPath);
			bitmap = mediaMetadataRetriever.getFrameAtTime();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Throwable(
					"Exception in retriveVideoFrameFromVideo(String videoPath)"
							+ e.getMessage());

		}
		finally
		{
			if (mediaMetadataRetriever != null)
			{
				mediaMetadataRetriever.release();
			}
		}
		System.out.println("c====="+System.currentTimeMillis());
		return bitmap;
			}
}
