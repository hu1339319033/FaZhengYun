package com.example.fazhengyun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fazhengyun.R;
import com.example.fazhengyun.kit.BaseViewHolder;

/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class MyGridAdapter extends BaseAdapter {
	private Context mContext;

	public String[] img_text = { "通话录音","文件上传","拍照存证","现场录音","录像存证","秒帮上传","短信存证","网页存证"};
	public int[] imgs = { R.mipmap.icon_2,R.mipmap.icon_5,
			R.mipmap.icon_4,R.mipmap.icon_6, R.mipmap.icon_3,
			R.mipmap.icon_7,R.mipmap.icon_8,R.mipmap.icon_9};

	public MyGridAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.grid_item, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
		ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
		iv.setImageResource(imgs[position]);
		tv.setText(img_text[position]);
		return convertView;
	}

}
