package com.example.fazhengyun.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityGuideBinding;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.xdroid.base.XActivity;

import java.util.ArrayList;

public class GuideActivity extends XActivity<ActivityGuideBinding> {
	private final String TAG = GuideActivity.class.getSimpleName();
	private SharedPreferences sp;
	
	private Editor editor;
	private ViewPager viewPager;

	/**装分页显示的view的数组*/
    private ArrayList<View> pageViews;    
    private ImageView imageView;
     
    /**将小圆点的图片用数组表示*/
    private ImageView[] imageViews;
     
    //包裹滑动图片的LinearLayout
    private ViewGroup viewPics;
     
    //包裹小圆点的LinearLayout
    private ViewGroup viewPoints;

	Button btstart;
	CheckBox isagree;
	private OnClickListener btstartclicker=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
				Intent intent=new Intent(GuideActivity.this,MainActivity.class);
				startActivity(intent);
				editor.putBoolean(Constants.FirstSTART,false);
				editor.commit();
				finish();
		}
	};

	public void btclick(View view){
		System.out.println("btclick");
		btstart=(Button) findViewById(R.id.btn_start);
		btstart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(GuideActivity.this,MainActivity.class);
				startActivity(intent);
				editor.putBoolean(Constants.FirstSTART,false);
				editor.commit();
				finish();
			}
		});
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		editor = sp.edit();
		LayoutInflater inflater = getLayoutInflater();

		pageViews = new ArrayList<View>();
		pageViews.add(inflater.inflate(R.layout.viewpager_page1, null));
		pageViews.add(inflater.inflate(R.layout.viewpager_page2, null));

		//创建imageviews数组，大小是要显示的图片的数量
		imageViews = new ImageView[pageViews.size()];
		//从指定的XML文件加载视图

		//实例化小圆点的linearLayout和viewpager
		viewPoints = getBinding().viewGroup;
		viewPager = getBinding().guidePages;

		//添加小圆点的图片
		for(int i=0;i<pageViews.size();i++){
			imageView = new ImageView(GuideActivity.this);
			//设置小圆点imageview的参数
			android.widget.LinearLayout.LayoutParams layoutParams2 = new android.widget.LinearLayout.LayoutParams(20,20);
			layoutParams2.setMargins(40, 0, 0, 0);
			imageView.setLayoutParams(layoutParams2);//创建一个宽高均为20 的布局
			//将小圆点layout添加到数组中
			imageViews[i] = imageView;

			//默认选中的是第一张图片，此时第一个小圆点是选中状态，其他不是
			if(i==0){
				imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
			}else{
				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
			}

			//将imageviews添加到小圆点视图组
			viewPoints.addView(imageViews[i]);
		}

		// 设置viewpager的适配器和监听事件
		viewPager.setAdapter(new GuidePageAdapter());
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_guide;
	}

	/**
	 * 引导界面的adapter
	 * @author admin
	 *
	 */
	class GuidePageAdapter extends PagerAdapter {

		// 销毁position位置的界面
		@Override
		public void destroyItem(View v, int position, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) v).removeView(pageViews.get(position));

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		// 获取当前窗体界面数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pageViews.size();
		}

		// 初始化position位置的界面
		@Override
		public Object instantiateItem(View v, int position) {
			// TODO Auto-generated method stub
			((ViewPager) v).addView(pageViews.get(position));

				if(position==1){
					isagree=(CheckBox) v.findViewById(R.id.isagree);
					btstart=(Button) v.findViewById(R.id.btn_start);
					btstart.setOnClickListener(btstartclicker);
					isagree.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(((CheckBox)v).isChecked()){
								btstart.setVisibility(View.VISIBLE);
							}else{
								btstart.setVisibility(View.GONE);
							}
						}
					});
				}
			return pageViews.get(position);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View v, Object arg1) {
			// TODO Auto-generated method stub
			return v == arg1;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			if (position==3) {
				viewPoints.setVisibility(View.GONE);
			}else {
				viewPoints.setVisibility(View.VISIBLE);
			}
				
			super.setPrimaryItem(container, position, object);
		}
		
	}

	class GuidePageChangeListener implements OnPageChangeListener {
		 
        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
             
        }
 
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
             System.out.println("onPageScrolled"+arg0);
        }
 
        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            for(int i=0;i<imageViews.length;i++){
                imageViews[position].setBackgroundResource(R.drawable.page_indicator_focused);
                //不是当前选中的page，其小圆点设置为未选中的状态
                if(position !=i){
                    imageViews[i].setBackgroundResource(R.drawable.page_indicator);
                }
            }
             
        }
    }
}
