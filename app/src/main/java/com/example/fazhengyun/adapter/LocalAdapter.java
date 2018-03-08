package com.example.fazhengyun.adapter;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CompoundButton;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.AdapterLocalBinding;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.ResourceInfos;
import com.example.fazhengyun.xdroid.base.SimpleRecBindingViewHolder;

import java.io.File;
import java.util.HashMap;

import cn.droidlover.xdroidbase.base.SimpleRecAdapter;
import cn.droidlover.xdroidbase.imageloader.ILFactory;

/**
 * Created by fscm-qt on 2017/11/29.
 */

public class LocalAdapter extends SimpleRecAdapter<ResourceInfos.Item,SimpleRecBindingViewHolder<AdapterLocalBinding>> {

    private Constants.LocalType localType;
    public static final int TAG_VIEW = 0;
    private CheckboxOnCheckedChange checkboxOnCheckedChange;
    public static Visibility visibility = Visibility.GONE;
    public static HashMap<Integer, Boolean> isSelected;

    // 初始化 设置postion位置选中，其它checkbox都为未选中
    public void init(int position) {
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < data.size();i++) {
            if(i == position){
                isSelected.put(i, true);
                continue;
            }
            isSelected.put(i, false);
        }
    }

    /**
     * 全选
     */
    public void all() {
        if(isSelected ==null || isSelected.size() == 0){
            return;
        }
        for (int i = 0; i < data.size();i++) {
            isSelected.put(i, true);
        }
    }

    /**
     * 全不选
     */
    public void notall() {
        if(isSelected ==null || isSelected.size() == 0){
            return;
        }
        for (int i = 0; i < data.size();i++) {
            isSelected.put(i, false);
        }
    }

    public HashMap<Integer, Boolean> getIsSelected(){
        return isSelected;
    }
    public LocalAdapter(Context context, Constants.LocalType localType) {
        super(context);
        this.localType = localType;
    }

    @Override
    public SimpleRecBindingViewHolder<AdapterLocalBinding> newViewHolder(View view) {
        return new SimpleRecBindingViewHolder<AdapterLocalBinding>(view);
    }

    @Override
    public void onBindViewHolder(final SimpleRecBindingViewHolder<AdapterLocalBinding> holder, final int position) {
        final ResourceInfos.Item item = data.get(position);
        holder.getBinding().tvName.setText(item.getName());
        holder.getBinding().tvDatetime.setText(item.getDatetime());
        holder.getBinding().tvFilesize.setText(item.getFilesize());
        holder.getBinding().tvState.setText(item.getState() == 0 ? "未存证":"已存证");
        switch (localType){
            case PHOTO:
                ILFactory.getLoader().loadFile(holder.getBinding().ivIcon,new File(item.getFilepath()+item.getName()),null);
                holder.getBinding().ivIconR.setVisibility(View.GONE);
                break;
            case VIDEO:
                holder.getBinding().ivIcon.setImageBitmap(ThumbnailUtils.createVideoThumbnail(item.getFilepath()+item.getName(), MediaStore.Images.Thumbnails.MINI_KIND));
                holder.getBinding().ivIconR.setVisibility(View.VISIBLE);
                break;
            case AUDIO://现场录音
            case CALLRECORD://通话录音
                holder.getBinding().ivIcon.setImageResource(R.mipmap.icon_wav);
                holder.getBinding().ivIconR.setVisibility(View.GONE);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getSimpleItemClick() != null){
                    getSimpleItemClick().onItemClick(position,item,TAG_VIEW,holder);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(getSimpleItemClick() != null){
                    getSimpleItemClick().onItemLongClick(position,item, TAG_VIEW,holder);
                }
                return true;
            }
        });
        if(visibility == Visibility.GONE){
            holder.getBinding().checkbox.setVisibility(View.GONE);
        }else{
            holder.getBinding().checkbox.setVisibility(View.VISIBLE);
            holder.getBinding().checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    isSelected.put(position,b);//修改map集合的状态存储
                    checkboxOnCheckedChange.checkedchange(position,b);
                }
            });
            if(null!=isSelected){
                holder.getBinding().checkbox.setChecked(isSelected.get(position));
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_local;
    }

    public void setCheckChangeListener(CheckboxOnCheckedChange checkChangeListener){
        this.checkboxOnCheckedChange = checkChangeListener;
    }

    public interface CheckboxOnCheckedChange{
        void checkedchange(int position,boolean checked);
    }
    public enum Visibility{
        VISIBLE,GONE
    }
}

