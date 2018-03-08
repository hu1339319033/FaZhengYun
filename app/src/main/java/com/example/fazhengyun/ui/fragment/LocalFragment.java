package com.example.fazhengyun.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.example.fazhengyun.R;
import com.example.fazhengyun.adapter.LocalAdapter;
import com.example.fazhengyun.databinding.AdapterLocalBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.ResourceInfos;
import com.example.fazhengyun.model.ResultMsg;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.ui.activity.BaseXActivity;
import com.example.fazhengyun.xdroid.base.SimpleRecBindingViewHolder;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.droidlover.xdroidbase.base.SimpleItemCallback;
import cn.droidlover.xdroidbase.base.SimpleRecAdapter;
import cn.droidlover.xdroidbase.kit.Kits;
import cn.droidlover.xdroidbase.log.XLog;
import cn.droidlover.xrecyclerview.XRecyclerView;

import static com.example.fazhengyun.net.NetApi.encrypt;

/**
 * Created by fscm-qt on 2017/11/28.
 */

@SuppressLint("ValidFragment")
public class LocalFragment extends BasePageFragment {

    LocalAdapter adapter;
    int totalCount; //记录选中的item的postion值
    List<Integer> checkedPostion;
    private Handler handler;
    List<ResourceInfos.Item> resourcesInfos;
    List<String> checkPostionFilePath;
    private Constants.LocalType localType;
    private String titleText;
    private String spName;
    private int type = 0;
    public LocalFragment(){}
    public LocalFragment(Constants.LocalType localType, Handler handler){
        this.localType = localType;
        this.handler = handler;
        switch (localType){
            case PHOTO:
                titleText = Constants.PHOTOTITLE;
                spName = Constants.LOCALPHOTOLIST;
                type = 0;
                break;
            case VIDEO:
                titleText = Constants.VIDEOTITLE;
                spName = Constants.LOCALVIDEOLIST;
                type = 2;
                break;
            case AUDIO:
                titleText = Constants.AUDIOTITLE;
                spName = Constants.LOCALAUDIOLIST;
                type = 1;
                break;
            case CALLRECORD://通话录音
                titleText = Constants.CallRECORDTITLE;
                spName = Constants.LOCALCallRECORDLIST;
                type = 4;
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public SimpleRecAdapter getAdapter() {
        if(adapter == null){
            adapter = new LocalAdapter(context,localType);
        }
        adapter.notall();
        adapter.setItemClick(new SimpleItemCallback<ResourceInfos.Item,SimpleRecBindingViewHolder<AdapterLocalBinding>>() {

            @Override
            public void onItemClick(final int position, final ResourceInfos.Item model, int tag, SimpleRecBindingViewHolder<AdapterLocalBinding> holder) {
                super.onItemClick(position, model, tag, holder);
                final PopupMenu popupMenu;
                popupMenu = new PopupMenu(context,holder.itemView);
                popupMenu.setGravity(Gravity.END);
                //获取菜单填充器
                MenuInflater inflater = popupMenu.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.menu_center, popupMenu.getMenu());
                //绑定菜单项的点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.action_open://查看
                                switch (localType){
                                    case PHOTO:
                                        context.startActivity(AppKit.OpenFileIntent.getImageFileIntent(context,model.getFilepath()+model.getName()));
                                        break;
                                    case VIDEO:
                                        context.startActivity(AppKit.OpenFileIntent.getVideoFileIntent(context,model.getFilepath()+model.getName()));
                                        break;
                                    case AUDIO:
                                        context.startActivity(AppKit.OpenFileIntent.getAudioFileIntent(context,model.getFilepath()));//音频文件保存的时候路径里包含文件名了
                                        break;
                                    case CALLRECORD:
                                        context.startActivity(AppKit.OpenFileIntent.getAudioFileIntent(context,model.getFilepath()+model.getName()));
                                        break;
                                }
                                break;
                            case R.id.action_save://存证
                                if(model.getState() == 1){
                                    getUiDelegate().toastShort("该文件已存证！");
                                }else{
                                    if(type==1){
                                        formUploadFile(type,model.getFilepath(),position);
                                    }else{
                                        formUploadFile(type,model.getFilepath()+model.getName(),position);
                                    }
                                }
                                break;
                            case R.id.action_delete://删除
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("确定删除吗？");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        adapter.removeElement(position);
                                        adapter.notifyDataSetChanged();
                                        //删除对应adapter中的数据
                                        List<ResourceInfos.Item> resourcesInfos = (List<ResourceInfos.Item>) SharedPreferenceUtil.getInstance(context).getObject(spName);
                                        //从本地记录中删除对应记录
                                        resourcesInfos.remove(position);
                                        SharedPreferenceUtil.getInstance(context).putObject(spName,resourcesInfos);
                                    }
                                });
                                builder.setNegativeButton("取消",null);
                                builder.create().show();

                                break;
                        }
                        return false;
                    }
                });
                //显示(这一行代码不要忘记了)
                popupMenu.show();
            }

            @Override
            public void onItemLongClick(int position, ResourceInfos.Item model, int tag, SimpleRecBindingViewHolder<AdapterLocalBinding> holder) {
                super.onItemLongClick(position, model, tag, holder);
                LocalAdapter.visibility = LocalAdapter.Visibility.VISIBLE;
                adapter.init(position);
                adapter.notifyDataSetChanged();
                //修改标题
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = "已选中1项";
                handler.sendMessage(message);
                //设置底部菜单显示隐藏
                handler.sendEmptyMessage(3);
                //设置左上角取消按钮显示
                handler.sendEmptyMessage(4);
            }
        });
        adapter.setCheckChangeListener(new LocalAdapter.CheckboxOnCheckedChange() {
            @Override
            public void checkedchange(int position, boolean checked) {
                if(checked){
                    totalCount++;
                }else{
                    if(totalCount <= 0){
                        return;
                    }
                    totalCount--;
                }
                if(totalCount > 0){
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    message.obj = "已选中"+totalCount+"项";
                    handler.sendMessage(message);
                }
            }
        });
        return adapter;
    }

    @Override
    public void setLayoutManager(final XRecyclerView recyclerView) {
        recyclerView.verticalLayoutManager(context);
        ((BaseXActivity)getActivity()).setClickListener(new BaseXActivity.ClickListener() {
            @Override
            public void click(int ResId) {
                switch (ResId){
                    case R.id.btn_delete://多选删除
                        if(checkedPostion == null){
                            checkedPostion = new ArrayList<Integer>();
                        }
                        checkedPostion.clear();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("确定删除吗？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for (Map.Entry<Integer, Boolean> entry : adapter.getIsSelected().entrySet()){
                                    //遍历IsSelect中的值,取出值为true的对应key
                                    if(entry.getValue() == true){
                                        checkedPostion.add(entry.getKey());
                                    }
                                }
                                //删除对应adapter中的数据
                                List<ResourceInfos.Item> resourcesInfos = (List<ResourceInfos.Item>) SharedPreferenceUtil.getInstance(context).getObject(spName);
                                for (Integer position:checkedPostion){
                                    XLog.e("checkedPostion:"+position);
                                    adapter.removeElement(position);
                                }
                                //删除后记得取消adapter中Item选中状态
                                handler.sendEmptyMessage(2);
                                handler.sendEmptyMessage(5);
                                LocalAdapter.visibility = LocalAdapter.Visibility.GONE;
                                clearTotalCount();
                                adapter.notifyDataSetChanged();
                                //从本地记录中删除对应记录
                                for (int j = 0;j < resourcesInfos.size();j++){
                                    if(checkedPostion.contains(j)){
                                        resourcesInfos.remove(j);
                                    }
                                }

                                SharedPreferenceUtil.getInstance(context).putObject(spName,resourcesInfos);

                            }
                        });
                        builder.setNegativeButton("取消",null);
                        builder.create().show();

                        break;
                    case R.id.btn_commit://多选存证
                        if(checkedPostion == null){
                            checkedPostion = new ArrayList<Integer>();
                        }
                        checkedPostion.clear();
                        checkPostionFilePath = new ArrayList<String>();
                        for (Map.Entry<Integer, Boolean> entry : adapter.getIsSelected().entrySet()){
                            //遍历IsSelect中的值,取出值为true的对应key
                            if(entry.getValue() == true){
                                if(adapter.getItemData(entry.getKey()).getState()==0){//未存证
                                    checkedPostion.add(entry.getKey());
                                }
                            }
                        }
                        for (Integer item:checkedPostion) {
                            checkPostionFilePath.add(adapter.getItemData(item).getFilepath()+adapter.getItemData(item).getName());
                        }
                        if(checkPostionFilePath.size()<=0){
                            getUiDelegate().toastShort("选中文件均已存证！");
                            return;
                        }

                        formUploadFile(type,null,-1);

                        break;
                }
            }
        });
    }

    @Override
    public void loadData(final int page) {
        getBinding().contentLayout.showContent();
        getBinding().contentLayout.setEnabled(false);
        getBinding().contentLayout.getSwipeRefreshLayout().setEnabled(false);
        getBinding().contentLayout.getRecyclerView().setRefreshEnabled(false);
        resourcesInfos = (List<ResourceInfos.Item>) SharedPreferenceUtil.getInstance(context).getObject(spName);

        if(resourcesInfos == null || resourcesInfos.size() == 0){
            getBinding().contentLayout.showEmpty();
            return;
        }
        if (page > 1) {
            adapter.addData(resourcesInfos);
        } else {
            adapter.setData(resourcesInfos);
        }

//        getBinding().contentLayout.getRecyclerView().setPage(page, MAX_PAGE);

        if (adapter.getItemCount() < 1) {
            getBinding().contentLayout.showEmpty();
            return;
        }
    }

    /**
     * 文件上传
     * Int type（类型 0图片，1音频，2 视频 3 其他）
     Int upType（0 pc文件上传）
     */
    private void formUploadFile(int type, String filepath, final int position){
        HashMap<String,String> params = new HashMap<>();
        params.put("userId", SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
        params.put("upTime", Kits.Date.getYmdhms(System.currentTimeMillis()));
        params.put("type",type+"");//文件类型
        params.put("upType",0+"");//文件上传
        params.put("desc","Android端现场上传");

        ArrayList<File> files = new ArrayList<>();
        if(filepath==null){ //图片类型为可多选，路径存储在list集合里
            if (checkPostionFilePath != null && checkPostionFilePath.size() > 0) {
                for (int i = 0; i < checkPostionFilePath.size(); i++) {
                    files.add(new File(checkPostionFilePath.get(i)));
                    XLog.e("filepath",checkPostionFilePath.get(i));
                }
            }
        }else{//音频和视频类型传递单个文件路径
            XLog.e("filepath",filepath);
            files.add(new File(filepath));
        }
        //加密start
        long timestamp = System.currentTimeMillis();
        String sign = encrypt(params,"7EECF7A8337C4C8489674C6F3DC794F3",timestamp);
        XLog.e("sha1sign:"+sign);
        params.put("sign",sign);
        params.put("timestamp",timestamp+"");
        //加密end

        OkGo.<String>post(UrlKit.getUrl(UrlKit.ACTION_FileUpload))
                .params(params)
                .addFileParams("file",files)
                .execute(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        XLog.e("Response:","start");
                        getUiDelegate().getProcessDialog().setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        getUiDelegate().showProcessDialog("正在上传");
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        super.uploadProgress(progress);
                        XLog.e("uploadProgress: " + progress);
                        String speed = Formatter.formatFileSize(context, progress.speed);
                        XLog.e("speed:"+String.format("%s/s", speed));
                        getUiDelegate().getProcessDialog().setProgress((int) (progress.fraction * 100));
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        XLog.e("Response:","error");
                        getUiDelegate().dimmisProcessDiaglog();
                        getUiDelegate().toastShort("上传失败，请重试");
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        XLog.e("Response:",response.body().toString());
                        getUiDelegate().dimmisProcessDiaglog();
                        ResultMsg resultMsg = new Gson().fromJson(response.body().toString(),ResultMsg.class);
                        getUiDelegate().toastShort(resultMsg.getMsg());
                        if(resultMsg.getResult() == 0){
                            return;
                        }
                        //本地状态更新为已存证
                        List<ResourceInfos.Item> resourcesInfos = (List<ResourceInfos.Item>) SharedPreferenceUtil.getInstance(context).getObject(spName);
                        if(position != -1){  //执行单个操作
                            resourcesInfos.get(position).setState(1);
                        }else{
                            for (int p : checkedPostion){
                                resourcesInfos.get(p).setState(1);
                            }
                        }
                        SharedPreferenceUtil.getInstance(context).putObject(spName,resourcesInfos);
                        loadData(1);
                    }
                });
    }

    /**
     * 显示全部
     */
    public void showAll(){
        resourcesInfos = (List<ResourceInfos.Item>) SharedPreferenceUtil.getInstance(context).getObject(spName);
        adapter.setData(resourcesInfos);
    }

    /**
     * 显示已存证
     */
    public void showAlready(){
        resourcesInfos = (List<ResourceInfos.Item>) SharedPreferenceUtil.getInstance(context).getObject(spName);
        if(null==resourcesInfos||resourcesInfos.size()==0){
            return;
        }
        for (ResourceInfos.Item item : resourcesInfos){
            if(item.getState() != 1){
                resourcesInfos.remove(item);
            }
        }
        adapter.setData(resourcesInfos);
    }

    /**
     * 显示未存证
     */
    public void showNotAlready(){
        resourcesInfos = (List<ResourceInfos.Item>) SharedPreferenceUtil.getInstance(context).getObject(spName);
        if(null==resourcesInfos||resourcesInfos.size()==0){
            return;
        }
        for (ResourceInfos.Item item : resourcesInfos){
            if(item.getState() != 0){
                resourcesInfos.remove(item);
            }
        }
        adapter.setData(resourcesInfos);
    }

    public void clearTotalCount(){
        totalCount = 0;
        adapter.notall();
        adapter.notifyDataSetChanged();
        Message message = handler.obtainMessage();
        message.what = 1;
        message.obj = titleText;
        handler.sendMessage(message);
    }

    public static LocalFragment newInstance(Constants.LocalType localType,Handler handler){
        return new LocalFragment(localType,handler);
    }
}
