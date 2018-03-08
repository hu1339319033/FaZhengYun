package com.example.fazhengyun.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.AdapterCloudBinding;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.ResultMsg;
import com.example.fazhengyun.model.UploadListInfos;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.xdroid.base.SimpleRecBindingViewHolder;

import java.util.HashMap;

import cn.droidlover.xdroidbase.base.SimpleRecAdapter;
import cn.droidlover.xdroidbase.imageloader.ILFactory;
import cn.droidlover.xdroidbase.imageloader.ILoader;
import cn.droidlover.xdroidbase.kit.Kits;
import okhttp3.Call;

/**
 * Created by fscm-qt on 2017/11/29.
 */

public class CloudAdapter extends SimpleRecAdapter<UploadListInfos.Item,SimpleRecBindingViewHolder<AdapterCloudBinding>> {

    private Constants.LocalType localType;
    public static final int TAG_VIEW = 0;

    public CloudAdapter(Context context,Constants.LocalType localType) {
        super(context);
        this.localType = localType;
    }

    @Override
    public SimpleRecBindingViewHolder<AdapterCloudBinding> newViewHolder(View view) {
        return new SimpleRecBindingViewHolder<AdapterCloudBinding>(view);
    }

    @Override
    public void onBindViewHolder(final SimpleRecBindingViewHolder<AdapterCloudBinding> holder, final int position) {
        final UploadListInfos.Item item = data.get(position);
        holder.getBinding().tvName.setText(item.getOriginalName());
        holder.getBinding().tvDatetime.setText(Kits.Date.getYmdhms(item.getUploadDate()));
        switch (item.getType()){
            case 0:
                ILFactory.getLoader().loadNet(holder.getBinding().ivIcon,item.getUploadUrlmin(),new ILoader.Options(R.mipmap.icon_jpg,R.mipmap.icon_jpg));//加载缩略图
                break;
            case 1://现场录音
            case 4://通话录音
                holder.getBinding().ivIcon.setImageResource(R.mipmap.icon_wav);
                break;
            case 2:
                holder.getBinding().ivIcon.setImageResource(R.mipmap.icon_avi);
                break;
            default:
                holder.getBinding().ivIcon.setImageResource(R.mipmap.icon_txt);
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
        holder.getBinding().btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //申请出证
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("uploadId",item.getId()+"");
                hashMap.put("userId", SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
                hashMap.put("log_type","0");
                NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_ApplyAsset), hashMap, new JsonCallback<ResultMsg>() {
                    @Override
                    public void onResponse(ResultMsg response, int id) {
                        Toast.makeText(context,response.getMsg(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(Call call, Exception e, int id) {
                        Toast.makeText(context,context.getString(R.string.network_fail_msg),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_cloud;
    }
}

