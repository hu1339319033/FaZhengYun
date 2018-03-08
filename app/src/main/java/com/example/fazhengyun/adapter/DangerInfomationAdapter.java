package com.example.fazhengyun.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fazhengyun.R;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.DangerInfos;
import com.example.fazhengyun.model.ResultMsg;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.ui.activity.LocationMapActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.droidlover.xdroidbase.kit.Kits;
import cn.droidlover.xdroidbase.router.Router;
import okhttp3.Call;

/**
 * Created by fscm-qt on 2017/12/16.
 */

public class DangerInfomationAdapter extends SecondaryListAdapter<DangerInfomationAdapter.GroupItemViewHolder,DangerInfomationAdapter.SubItemViewHolder> {

    private Context context;
    private PopupMenu popupMenu;

    private List<SecondaryListAdapter.DataTree<String, DangerInfos.SubItem>> dts = new ArrayList<>();

    public DangerInfomationAdapter(Context context,List<SecondaryListAdapter.DataTree<String, DangerInfos.SubItem>> datas) {
        this.context = context;
        this.dts = datas;
    }

//    public void setData(List datas) {
//        if(datas != null) {
//            this.dts = datas;
//        }
//        setData(datas);
//    }
    @Override
    public RecyclerView.ViewHolder groupItemViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dangerinfo_groupitem, parent, false);
        return new GroupItemViewHolder(v);
    }

    @Override
    public RecyclerView.ViewHolder subItemViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dangerinfo_subitem, parent, false);
        return new SubItemViewHolder(v);
    }

    @Override
    public void onGroupItemBindViewHolder(RecyclerView.ViewHolder holder, int groupItemIndex) {
        String typeString = "";
        try {
            switch (dts.get(groupItemIndex).getSubItems().get(0).getDeviceType()) {
                case "1":
                    typeString="皮带";
                    break;
                case "2":
                    typeString="吊坠";
                    break;
                case "3":
                    typeString="护航仪";
                    break;
                case "0":
                    typeString="手表";
                    break;

                default:
                    break;
            }
            ((GroupItemViewHolder)holder).tvGroup.setText("设备："+dts.get(groupItemIndex).getGroupItem()+"\t\t"+typeString);
        }catch (Exception e){
            ((GroupItemViewHolder)holder).tvGroup.setText("设备："+dts.get(groupItemIndex).getGroupItem());
        }

    }

    @Override
    public void onSubItemBindViewHolder(final RecyclerView.ViewHolder holder, final int groupItemIndex, final int subItemIndex) {
        ((SubItemViewHolder) holder).tvAddress.setText(dts.get(groupItemIndex).getSubItems().get(subItemIndex).getGpsAddr());
        ((SubItemViewHolder) holder).tvDatetime.setText(Kits.Date.getYmdhms(dts.get(groupItemIndex).getSubItems().get(subItemIndex).getCallTime()));
        ((SubItemViewHolder) holder).btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupMenu = new PopupMenu(context,((SubItemViewHolder) holder).btnApply);
                popupMenu.setGravity(Gravity.END);
                //获取菜单填充器
                MenuInflater inflater = popupMenu.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.menu_apply, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.action_open://查看
                                //跳转地图
                                Router.newIntent((Activity) context).to(LocationMapActivity.class).putString("gpsInfoId",dts.get(groupItemIndex).getSubItems().get(subItemIndex).getId()+"").launch();
                                break;
                            case R.id.action_apply://申请出证
                                //申请出证
                                HashMap<String,String> hashMap = new HashMap<String, String>();
                                hashMap.put("uploadId",dts.get(groupItemIndex).getSubItems().get(subItemIndex).getId()+"");
                                hashMap.put("userId", SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
                                hashMap.put("log_type","1");
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
                                break;
                        }
                        return false;
                    }
                });

            }
        });
    }

    @Override
    public void onGroupItemClick(Boolean isExpand, GroupItemViewHolder holder, int groupItemIndex) {
//        Toast.makeText(context, "group item " + String.valueOf(groupItemIndex) + " is expand " +
//                String.valueOf(isExpand), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubItemClick(SubItemViewHolder holder, int groupItemIndex, int subItemIndex) {
//        Toast.makeText(context, "sub item " + String.valueOf(subItemIndex) + " in group item " +
//                String.valueOf(groupItemIndex), Toast.LENGTH_SHORT).show();
    }

    public class GroupItemViewHolder extends RecyclerView.ViewHolder{
        TextView tvGroup;
        public GroupItemViewHolder(View itemView){
            super(itemView);

            tvGroup = (TextView)itemView.findViewById(R.id.item_group_tv);
        }
    }
    public class SubItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvAddress,tvDatetime;
        Button btnApply;

        public SubItemViewHolder(View itemView) {
            super(itemView);

            tvAddress = (TextView) itemView.findViewById(R.id.item_sub_address);
            tvDatetime = (TextView) itemView.findViewById(R.id.item_sub_datatime);
            btnApply = (Button)itemView.findViewById(R.id.btn_apply);
        }
    }
}
