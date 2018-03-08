package com.example.fazhengyun.adapter;

import android.content.Context;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.AdapterAssestlistBinding;
import com.example.fazhengyun.model.AssestInfos;
import com.example.fazhengyun.xdroid.base.SimpleRecBindingViewHolder;

import cn.droidlover.xdroidbase.base.SimpleRecAdapter;
import cn.droidlover.xdroidbase.kit.Kits;

/**
 * Created by fscm-qt on 2017/12/16.
 */

public class AssestListAdapter extends SimpleRecAdapter<AssestInfos.Item,SimpleRecBindingViewHolder<AdapterAssestlistBinding>> {

    public AssestListAdapter(Context context) {
        super(context);
    }

    @Override
    public SimpleRecBindingViewHolder<AdapterAssestlistBinding> newViewHolder(View view) {
        return new SimpleRecBindingViewHolder<AdapterAssestlistBinding>(view);
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_assestlist;
    }

    @Override
    public void onBindViewHolder(SimpleRecBindingViewHolder<AdapterAssestlistBinding> holder, int position) {
        AssestInfos.Item item = data.get(position);
        holder.getBinding().tvDatetime.setText(Kits.Date.getYmdhms(item.getLogTime()));
        holder.getBinding().tvFilename.setText(item.getLogType()==0?"存证资源":"报险资源");
        if(item.getLogStatus() == 1){
            holder.getBinding().tvState.setText("已完成");
        }else{
            holder.getBinding().tvState.setText("审核中");
        }
    }
}
