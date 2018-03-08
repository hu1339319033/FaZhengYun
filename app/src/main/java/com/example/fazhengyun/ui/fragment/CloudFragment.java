package com.example.fazhengyun.ui.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import com.example.fazhengyun.R;
import com.example.fazhengyun.adapter.CloudAdapter;
import com.example.fazhengyun.databinding.AdapterLocalBinding;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.ResultMsg;
import com.example.fazhengyun.model.UploadListInfos;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.ui.activity.VideoPlayActivity;
import com.example.fazhengyun.xdroid.base.SimpleRecBindingViewHolder;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.droidlover.xdroidbase.base.SimpleItemCallback;
import cn.droidlover.xdroidbase.base.SimpleRecAdapter;
import cn.droidlover.xdroidbase.imageloader.ILFactory;
import cn.droidlover.xdroidbase.kit.Kits;
import cn.droidlover.xdroidbase.log.XLog;
import cn.droidlover.xdroidbase.router.Router;
import cn.droidlover.xrecyclerview.XRecyclerView;
import okhttp3.Call;

/**
 * Created by fscm-qt on 2017/11/28.
 */
@SuppressLint("ValidFragment")
public class CloudFragment extends BasePageFragment {

    Constants.LocalType localType;
    CloudAdapter adapter;
    private int type = -1;
    private boolean isRelease = true;   //判断是否MediaPlayer是否释放的标志
    private PopupWindow mPopupWindow;
    private MediaPlayer mediaPlayer;

    private Handler handler = new Handler(){};

    public CloudFragment(){}
    public CloudFragment(Constants.LocalType localType){
        this.localType = localType;
        switch (localType){
            case PHOTO:
                type = 0;
                break;
            case VIDEO:
                type = 2;
                break;
            case AUDIO:
                type = 1;
                break;
            case CALLRECORD:
                type = 4;
                break;
            case ALL:
                type = -1;
                break;
        }
    }
    @Override
    public SimpleRecAdapter getAdapter() {
        if(adapter == null){
            adapter = new CloudAdapter(context,localType);
        }
        adapter.setItemClick(new SimpleItemCallback<UploadListInfos.Item,SimpleRecBindingViewHolder<AdapterLocalBinding>>(){
            @Override
            public void onItemClick(final int position, final UploadListInfos.Item model, int tag, SimpleRecBindingViewHolder<AdapterLocalBinding> holder) {
                super.onItemClick(position, model, tag, holder);
                final PopupMenu popupMenu;
                popupMenu = new PopupMenu(context,holder.itemView);
                popupMenu.setGravity(Gravity.END);
                //获取菜单填充器
                MenuInflater inflater = popupMenu.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.menu_cloud, popupMenu.getMenu());
                //绑定菜单项的点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.action_open://查看
                                final HashMap<String,String> hashMap = new HashMap<String, String>();
                                hashMap.put("uploadId",model.getId()+"");
                                NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_DownLoad), hashMap, new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        DownloadUrl downloadUrl = new Gson().fromJson(response, DownloadUrl.class);
                                        switch (model.getType()){
                                            case 0: //图片
                                                try{
//                                                            context.startActivity(AppKit.OpenFileIntent.getImageFileIntentnetwork(context,""));
                                                    final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                                                    ImageView imgView = new ImageView(context);
                                                    imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                                    imgView.setScaleType(ImageView.ScaleType.CENTER);
                                                    ILFactory.getLoader().loadNet(imgView,downloadUrl.getUrl(),null);
                                                    dialog.setContentView(imgView);
                                                    dialog.show();
                                                    // 点击图片消失
                                                    imgView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            // TODO Auto-generated method stub
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                }catch (Exception e){
                                                    XLog.e(""+e);
                                                    getUiDelegate().toastShort("资源发生错误");
                                                }
                                                break;
                                            case 2: // 视频
                                                try {
                                                    Router.newIntent(context).to(VideoPlayActivity.class).putString("url",downloadUrl.getUrl()).launch();
                                                }catch (Exception e){
                                                    XLog.e(""+e);
                                                    getUiDelegate().toastShort("资源发生错误");
                                                }

                                                break;
                                            case 1: //音频
                                                showpopwindow(downloadUrl.getUrl());
                                                break;
                                            default: //其他文件类型
                                                getUiDelegate().toastShort("该文件类型暂不支持在线查看，请下载后自行查看");
                                                break;
                                        }
                                    }
                                });

                                break;
                            case R.id.action_download://下载
                                getUiDelegate().getProcessDialog().setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                getUiDelegate().showProcessDialog("正在下载");
                                HashMap<String,String> hashMap2 = new HashMap<String, String>();
                                hashMap2.put("uploadId",model.getId()+"");
                                NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_DownLoad), hashMap2, new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        getUiDelegate().dimmisProcessDiaglog();
                                        getUiDelegate().toastShort("获取下载链接失败，请重试");
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        DownloadUrl downloadUrl = new Gson().fromJson(response,DownloadUrl.class);
                                        if(!Kits.File.isFolderExist(Environment.getExternalStorageDirectory().getPath()+"/DownLoadFile/")){
                                            Kits.File.makeFolders(Environment.getExternalStorageDirectory().getPath()+"/DownLoadFile/");
                                        }
                                        NetApi.invokeDownLoad(downloadUrl.getUrl(), new FileCallBack(Environment.getExternalStorageDirectory().getPath()+"/DownLoadFile/",model.getOriginalName()) {
                                            @Override
                                            public void onError(Call call, Exception e, int id) {
                                                getUiDelegate().toastShort("文件下载失败，请重试");
                                            }

                                            @Override
                                            public void inProgress(float progress, long total, int id) {
                                                super.inProgress(progress, total, id);
                                                getUiDelegate().getProcessDialog().setProgress((int)(progress*100));
                                            }

                                            @Override
                                            public void onResponse(File response, int id) {
                                                getUiDelegate().getProcessDialog().dismiss();
                                                getUiDelegate().toastShort("文件"+response.getName() + ",已存入本地DownLoadFile文件夹");
                                            }
                                        });
                                    }
                                });
                                break;
                            case R.id.action_delete:
                                HashMap<String,String> hashMap3 = new HashMap<String, String>();
                                hashMap3.put("uploadId",model.getId()+"");
                                NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_Delete), hashMap3, new JsonCallback<ResultMsg>() {
                                    @Override
                                    public void onFail(Call call, Exception e, int id) {
                                        getUiDelegate().toastShort("删除失败");
                                    }

                                    @Override
                                    public void onResponse(ResultMsg response, int id) {
                                        getUiDelegate().toastShort(response.getMsg());
                                        if(response.getResult()==0){
                                            return;
                                        }
                                        loadData(1);
                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });
                //显示(这一行代码不要忘记了)
                popupMenu.show();
            }
        });
        return adapter;
    }

    @Override
    public void setLayoutManager(XRecyclerView recyclerView) {
        recyclerView.verticalLayoutManager(context);
    }

    @Override
    public void loadData(final int page) {
        HashMap<String,String > hashMap = new HashMap<>();
        hashMap.put("userId", SharedPreferenceUtil.getInstance(context).getInt(Constants.USERID,-1)+"");
        hashMap.put("pageNum",page+"");
        if(type!=-1){
            hashMap.put("type",type+"");
        }
        NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_UploadList), hashMap, new JsonCallback<UploadListInfos>() {
            @Override
            public void onResponse(UploadListInfos response, int id) {
                XLog.e("onResponse==="+response.getTotle());
                if (page > 1) {
                    adapter.addData(response.getUploadList());
                } else {
                    adapter.setData(response.getUploadList());
                }

                getBinding().contentLayout.getRecyclerView().setPage(page, response.getTotle()/10>=1?response.getTotle()/10+1:1);

                if (adapter.getItemCount() < 1) {
                    getBinding().contentLayout.showEmpty();
                    return;
                }
                getBinding().contentLayout.showContent();
            }

            @Override
            public void onFail(Call call, Exception e, int id) {
                getBinding().contentLayout.showError();
            }
        });
    }


    public void showpopwindow(final String url){
        final View v = View.inflate(context,R.layout.popwindow_playandio,null);
        mPopupWindow = new PopupWindow(v, Kits.Dimens.dpToPxInt(context,200), Kits.Dimens.dpToPxInt(context,100));
        mPopupWindow.setAnimationStyle(R.style.popwindow_anim_style);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowAlpa(false);
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        });
        show(v);
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        v.findViewById(R.id.img_playorpause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();//暂停播放
                    ((ImageView)v.findViewById(R.id.img_playorpause)).setImageResource(R.mipmap.play);
                }else {
                    ((ImageView)v.findViewById(R.id.img_playorpause)).setImageResource(R.mipmap.pause);
                    try {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(url);
                        mediaPlayer.prepare(); // might take long! (for buffering, etc)
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        XLog.e(""+e);
                    }
                }
            }
        });
        v.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //播放完成
                getUiDelegate().toastShort("播放完成");
                ((ImageView)v.findViewById(R.id.img_playorpause)).setImageResource(R.mipmap.play);
            }
        });
    }


    /**
     * 显示PopupWindow
     */
    private void show(View v) {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
        setWindowAlpa(true);
    }

    /**
     * 消失PopupWindow
     */
    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 动态设置Activity背景透明度
     *
     * @param isopen
     */
    public void setWindowAlpa(boolean isopen) {
        if (android.os.Build.VERSION.SDK_INT < 11) {
            return;
        }
        final Window window = ((Activity) context).getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ValueAnimator animator;
        if (isopen) {
            animator = ValueAnimator.ofFloat(1.0f, 0.5f);
        } else {
            animator = ValueAnimator.ofFloat(0.5f, 1.0f);
        }
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                lp.alpha = alpha;
                window.setAttributes(lp);
            }
        });
        animator.start();
    }

    public static CloudFragment newInstance(Constants.LocalType localType){
        return new CloudFragment(localType);
    }
    public class DownloadUrl{
        String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
