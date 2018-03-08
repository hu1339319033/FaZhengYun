package com.example.fazhengyun.ui.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.FragmentPhotoandaudioBinding;
import com.example.fazhengyun.kit.BaseViewHolder;
import com.example.fazhengyun.kit.MyThreadPool;
import com.example.fazhengyun.model.GpsResourceInfos;
import com.example.fazhengyun.net.JsonCallback;
import com.example.fazhengyun.net.NetApi;
import com.example.fazhengyun.net.UrlKit;
import com.example.fazhengyun.service.MusicService;
import com.example.fazhengyun.ui.activity.LookSwitchActivity;
import com.example.fazhengyun.xdroid.base.XLazyFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

import cn.droidlover.xdroidbase.imageloader.ILFactory;
import cn.droidlover.xdroidbase.log.XLog;
import okhttp3.Call;

/**
 * Created by fscm-qt on 2017/12/17.
 */

public class LookPhotoAndAudioFragment extends XLazyFragment<FragmentPhotoandaudioBinding> implements View.OnClickListener{

    private List<String> imageurls = new ArrayList<>();
    private List<SoundItem> soundItems = new ArrayList<>();
    private ViewPager viewPager;
    ListView listView;
    SoundListAdapter soundlistadapter;
    ImageView img_exit;
    private SeekBar seekBar;
    private MusicService musicService;
    @Override
    public void initData(Bundle savedInstanceState) {
        seekBar = getBinding().seekBar;
        musicService = new MusicService();
        bindServiceConnection();
        process();
        viewPager = getBinding().viewpager;
        viewPager.setPageMargin(30);
        getBinding().imgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomPop(getBinding().imgList);
            }
        });
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };
    private void bindServiceConnection() {
        Intent intent = new Intent(context, MusicService.class);
        context.startService(intent);
        context.bindService(intent, sc, this.context.BIND_AUTO_CREATE);
    }

    private void process(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("gpsInfoId",getActivity().getIntent().getStringExtra("gpsInfoId"));
        hashMap.put("resourceType",0+"");
        hashMap.put("pageNum",1+"");

        NetApi.invokeGet(UrlKit.getUrl(UrlKit.ACTION_GetGpsUploadList), hashMap, new JsonCallback<GpsResourceInfos>() {
            @Override
            public void onFail(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(GpsResourceInfos response, int id) {
                XLog.e("onResponse=======");
                for (GpsResourceInfos.Item item: response.getGpsUploadList()){
                    if(item.getUploadType() == 0){
                        imageurls.add(item.getUploadUrl());
                    }else{
                        soundItems.add(new SoundItem(item.getUploadTitle(),item.getUploadUrl()));
                    }
                }
                XLog.e("imageurls:"+imageurls.size());
                if(imageurls.size()<=0){
                    getBinding().viewpager.setVisibility(View.GONE);
                    getBinding().relaEmpty.setVisibility(View.VISIBLE);
                }
                for(SoundItem item: soundItems){
                    MusicService.musicDir.add(LookSwitchActivity.httpurlhead+item.getUrl());
                }
                XLog.e("musicDir:"+ MusicService.musicDir.size());
                if(MusicService.musicDir.size() > 0){
                    getBinding().tvDescriptionSound.setText("当前第"+(MusicService.musicIndex+1)+"段音频");
                    initThread();
                }
                viewPager.setAdapter(new PagerAdapter() {
                    @Override
                    public int getCount() {
                        return imageurls.size();
                    }

                    @Override
                    public boolean isViewFromObject(View view, Object object) {
                        return view == object;
                    }
//            @Override
//            public float getPageWidth(int position) {
//                return 0.8f;
//            }

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {
                        View view = View.inflate(container.getContext(),R.layout.widget_gallery_view, null);
                        ImageView iv = (ImageView) view.findViewById(R.id.headRIV);
//                        iv.setImageResource(R.mipmap.a1);
                        ILFactory.getLoader().loadNet(iv, LookSwitchActivity.httpurlhead+imageurls.get(position),null);
                        XLog.e("urliv"+LookSwitchActivity.httpurlhead+imageurls.get(position));
                        container.addView(view);
                        return view;
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position, Object object) {
                        container.removeView((View) object);
                    }
                });
                viewPager.setCurrentItem(1);
            }
        });
    }

    @Override
    public void setListener() {
        super.setListener();
        getBinding().getproofBtnPlayorPause.setOnClickListener(this);
        getBinding().getproofBtnNextt.setOnClickListener(this);
        getBinding().getproofBtnPre.setOnClickListener(this);
    }

    //此boolean为true 工作线程会一直运行 在ondestroy的时候设置为false 让工作线程正常结束 防止内存泄漏
    private boolean isShowCurrentTime=true;
    /**
     * 初始化线程 更新播放进度条和播放时间
     */
    private void initThread() {
        ExecutorService e = MyThreadPool.getInstance().getMyExecutorService();
        e.execute(new Runnable() {
            @Override
            public void run() {
                while(isShowCurrentTime){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        getActivity().runOnUiThread(new  Runnable() {
                            public void run() {
                                updataProgressBar();
                            }
                        });
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    /**
     * 更新当前进度条和时间
     */
    private void updataProgressBar() {
        if(MusicService.mp==null){
            return;
        }
        int position = MusicService.mp.getCurrentPosition();

        int time = MusicService.mp.getDuration();
        if(time!=0){
            int max = seekBar.getMax();

            seekBar.setProgress(position*max/time);
        }

    }

    /**
     * 从底部弹出popupwindow
     */
    private void showBottomPop(View parent) {
        final View popView = LayoutInflater.from(context).inflate(R.layout.popupwindow_soundlist, null);
        listView=(ListView) popView.findViewById(R.id.soundlist);
        img_exit=(ImageView)popView.findViewById(R.id.img_exit);
        soundlistadapter=new SoundListAdapter();
        listView.setAdapter(soundlistadapter);

        showAnimation(popView);//开启动画
        final PopupWindow mPopWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopWindow.showAtLocation(parent,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setFocusable(true);
        mPopWindow.update();
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.7f;
        ((Activity)context).getWindow().setAttributes(lp);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
                lp.alpha = 1f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        img_exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mPopWindow.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mPopWindow.dismiss();
                String returnstr = musicService.PlaySelectMusic(arg2);
                if(returnstr!=null){
                    if(returnstr.equals("暂停")){
                        getBinding().getproofBtnPlayorPause.setImageResource(R.mipmap.pause);
                    }else{
                        getBinding().getproofBtnPlayorPause.setImageResource(R.mipmap.play);
                    }
                }
                getBinding().tvDescriptionSound.setText("当前第"+(MusicService.musicIndex+1)+"段音频");
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_photoandaudio;
    }

    public static LookPhotoAndAudioFragment newInstance(){
        return new LookPhotoAndAudioFragment();
    }

    String state;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.getproof_btnNextt://下一段
                getBinding().getproofBtnPlayorPause.setImageResource(R.mipmap.play);
                state= musicService.nextMusic();
                if(state!=null){
                    if(state.equals("暂停")){
                        getBinding().getproofBtnPlayorPause.setImageResource(R.mipmap.pause);
                        getBinding().tvDescriptionSound.setText("当前第"+(MusicService.musicIndex+1)+"段音频");
                    }else{
                        getBinding().getproofBtnPlayorPause.setImageResource(R.mipmap.play);
                    }
                }
                break;
            case R.id.getproof_btnPre://上一段
                getBinding().getproofBtnPlayorPause.setImageResource(R.mipmap.play);
                state= musicService.preMusic();
                if(state!=null){
                    if(state.equals("暂停")){
                        getBinding().getproofBtnPlayorPause.setImageResource(R.mipmap.pause);
                        getBinding().tvDescriptionSound.setText("当前第"+(MusicService.musicIndex+1)+"段音频");
                    }else{
                        getBinding().getproofBtnPlayorPause.setImageResource(R.mipmap.play);
                    }
                }

                break;
            case R.id.getproof_BtnPlayorPause://播放/暂停
                state=musicService.playOrPause();
                if(state.equals("暂停")){
                    getBinding().getproofBtnPlayorPause.setImageResource(R.mipmap.pause);
                }else{
                    getBinding().getproofBtnPlayorPause.setImageResource(R.mipmap.play);
                }
                break;
            case R.id.img_list:
                showBottomPop(view);
                break;

            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(musicService!=null)
            musicService.stop();
        isShowCurrentTime=false;
    }

    /**
     * 给popupwindow添加动画
     *
     * @param popView
     */
    private void showAnimation(View popView) {
        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1.0f);
        alphaAnimation.setDuration(300);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        translateAnimation.setDuration(300);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        popView.startAnimation(animationSet);
    }

    class SoundListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return soundItems.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.soundlist_item_tv, null);

            }
            TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
            tv.setText(soundItems.get(position).getTitle());
            return convertView;
        }

    }
    public class SoundItem{

        public SoundItem(){}
        public SoundItem(String title,String url){
            this.title = title;
            this.url = url;
        }
        private String title;
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
