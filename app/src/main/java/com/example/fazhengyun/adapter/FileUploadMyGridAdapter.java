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
public class FileUploadMyGridAdapter extends BaseAdapter {
	private Context mContext;

	public String[] img_text = { "图片","视频", "音频","文档", "全部"};
	public int[] imgs = { R.mipmap.ic_upload_picture,R.mipmap.ic_upload_video,R.mipmap.ic_upload_sound,
			R.mipmap.ic_upload_txt,R.mipmap.ic_upload_all,};

	public FileUploadMyGridAdapter(Context mContext) {
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
